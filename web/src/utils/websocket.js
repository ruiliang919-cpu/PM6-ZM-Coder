/**
 * WebSocket 实时推送工具
 *
 * 由于项目无 sockjs-client / stompjs npm 依赖，采用原生 WebSocket + 自实现 STOMP 1.2 协议客户端。
 * 后端 Spring STOMP 端点同时注册了 SockJS（/ws）和原生 WebSocket（/ws/native），
 * 本工具连接 /ws/native 以原生 WebSocket 通信，使用 STOMP 帧格式进行订阅/收发。
 */

import { getToken } from '@/utils/auth'

// ==================== 状态变量 ====================
let ws = null           // WebSocket 实例
let connected = false   // STOMP 连接是否已就绪
let connecting = false  // 是否正在连接中
let subscriptions = {}  // 订阅映射 { subId: { id, destination, callback } }
let subIdCounter = 0    // 订阅ID计数器
let reconnectTimer = null  // 重连定时器
let heartbeatTimer = null  // 心跳定时器
let shouldReconnect = true // 是否应该重连（登出时置为false）

const RECONNECT_INTERVAL = 5000  // 重连间隔 5秒
const HEARTBEAT_INTERVAL = 10000 // STOMP 心跳间隔 10秒

// ==================== STOMP 帧解析/序列化 ====================

/**
 * 解析 STOMP 帧
 * 帧格式: COMMAND
header:value
...

body\0
 */
function parseFrame(data) {
  if (!data || typeof data !== 'string') return null

  // 去除末尾的 null 字符
  let str = data.replace(/\0$/, '')
  const lines = str.split('\n')

  if (lines.length === 0) return null

  // 跳过空行（心跳帧是 \n\n）
  let startIdx = 0
  while (startIdx < lines.length && lines[startIdx] === '') {
    startIdx++
  }
  if (startIdx >= lines.length) return { command: 'HEARTBEAT', headers: {}, body: '' }

  const command = lines[startIdx]
  const headers = {}
  let bodyStart = startIdx + 1

  for (let i = bodyStart; i < lines.length; i++) {
    if (lines[i] === '') {
      bodyStart = i + 1
      break
    }
    const idx = lines[i].indexOf(':')
    if (idx > 0) {
      headers[lines[i].substring(0, idx)] = lines[i].substring(idx + 1)
    }
  }

  const body = lines.slice(bodyStart).join('\n')
  return { command, headers, body }
}

/**
 * 序列化 STOMP 帧
 */
function serializeFrame(command, headers, body) {
  headers = headers || {}
  body = body || ''

  let frame = command + '\n'
  const keys = Object.keys(headers)
  for (let i = 0; i < keys.length; i++) {
    frame += keys[i] + ':' + headers[keys[i]] + '\n'
  }
  frame += '\n'
  frame += body
  frame += '\0'
  return frame
}

// ==================== WebSocket URL 构造 ====================

/**
 * 根据 VUE_APP_BASE_API 构造 WebSocket URL
 * - 开发环境: VUE_APP_BASE_API = '/dev-api' → ws://localhost:80/dev-api/ws/native
 * - 生产环境: VUE_APP_BASE_API = 'http://127.0.0.1/prod-api' → ws://127.0.0.1/prod-api/ws/native
 */
function getWebSocketUrl() {
  const baseApi = process.env.VUE_APP_BASE_API || ''
  let url

  if (baseApi.startsWith('http://') || baseApi.startsWith('https://')) {
    // 生产环境：替换 http 为 ws
    url = baseApi.replace(/^http/, 'ws') + '/ws/native'
  } else {
    // 开发环境：基于当前页面地址构造
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    url = protocol + '//' + window.location.host + baseApi + '/ws/native'
  }

  // 附带 token 参数
  const token = getToken()
  if (token) {
    url += '?token=' + encodeURIComponent(token)
  }

  return url
}

// ==================== 核心连接管理 ====================

/**
 * 建立 WebSocket + STOMP 连接
 */
export function connectWebSocket() {
  if (connected || connecting) return
  shouldReconnect = true
  connecting = true

  const url = getWebSocketUrl()
  console.log('[WebSocket] 正在连接:', url)

  try {
    ws = new WebSocket(url)
  } catch (e) {
    console.error('[WebSocket] 创建连接失败:', e)
    connecting = false
    scheduleReconnect()
    return
  }

  ws.onopen = function() {
    console.log('[WebSocket] TCP连接已建立，发送STOMP CONNECT帧')
    // 发送 STOMP CONNECT 帧
    var connectFrame = serializeFrame('CONNECT', {
      'accept-version': '1.1,1.0',
      'heart-beat': HEARTBEAT_INTERVAL + ',' + HEARTBEAT_INTERVAL
    })
    ws.send(connectFrame)
  }

  ws.onmessage = function(event) {
    var frame = parseFrame(event.data)
    if (!frame) return

    if (frame.command === 'CONNECTED') {
      // STOMP 连接成功
      connected = true
      connecting = false
      console.log('[WebSocket] STOMP连接成功, session:', frame.headers['session'])

      // 清除重连定时器
      if (reconnectTimer) {
        clearTimeout(reconnectTimer)
        reconnectTimer = null
      }

      // 重新订阅所有已注册的订阅
      resubscribeAll()

      // 启动心跳
      startHeartbeat()
    } else if (frame.command === 'MESSAGE') {
      // 收到消息
      var subId = frame.headers['subscription']
      var sub = subscriptions[subId]
      if (sub && sub.callback) {
        try {
          var data = JSON.parse(frame.body)
          sub.callback(data)
        } catch (e) {
          // 非 JSON 消息，直接传递 body
          sub.callback(frame.body)
        }
      }
    } else if (frame.command === 'ERROR') {
      console.error('[WebSocket] STOMP错误:', frame.headers['message'], frame.body)
    } else if (frame.command === 'RECEIPT') {
      // 收据确认，忽略
    } else if (frame.command === 'HEARTBEAT') {
      // 心跳帧，忽略
    }
  }

  ws.onclose = function(event) {
    console.log('[WebSocket] 连接关闭, code:', event.code, 'reason:', event.reason)
    connected = false
    connecting = false
    stopHeartbeat()
    ws = null
    if (shouldReconnect) {
      scheduleReconnect()
    }
  }

  ws.onerror = function(error) {
    console.error('[WebSocket] 连接异常:', error)
    connected = false
    connecting = false
  }
}

/**
 * 断开 WebSocket + STOMP 连接
 */
export function disconnectWebSocket() {
  shouldReconnect = false
  // 清除重连定时器
  if (reconnectTimer) {
    clearTimeout(reconnectTimer)
    reconnectTimer = null
  }
  stopHeartbeat()

  if (ws) {
    try {
      // 发送 STOMP DISCONNECT 帧
      if (connected) {
        var disconnectFrame = serializeFrame('DISCONNECT', {
          'receipt': 'disconnect-receipt'
        })
        ws.send(disconnectFrame)
      }
      ws.close()
    } catch (e) {
      // 忽略关闭时的异常
    }
    ws = null
  }

  connected = false
  connecting = false
  subscriptions = {}
  subIdCounter = 0
  console.log('[WebSocket] 已断开连接')
}

// ==================== 订阅管理 ====================

/**
 * 订阅 STOMP 目标
 * @param {string} destination - STOMP 目标路径，如 /topic/device/status/all
 * @param {function} callback - 消息回调函数
 * @returns {string} 订阅ID，用于取消订阅
 */
function subscribe(destination, callback) {
  var id = 'sub-' + (++subIdCounter)
  subscriptions[id] = { id: id, destination: destination, callback: callback }

  if (connected && ws) {
    var subFrame = serializeFrame('SUBSCRIBE', {
      'id': id,
      'destination': destination
    })
    ws.send(subFrame)
    console.log('[WebSocket] 已订阅:', destination, 'id:', id)
  }

  return id
}

/**
 * 取消订阅
 * @param {string} subId - 订阅ID
 */
export function unsubscribe(subId) {
  if (!subscriptions[subId]) return

  if (connected && ws) {
    var unsubFrame = serializeFrame('UNSUBSCRIBE', {
      'id': subId
    })
    ws.send(unsubFrame)
    console.log('[WebSocket] 已取消订阅:', subId)
  }

  delete subscriptions[subId]
}

/**
 * 重新订阅所有已注册的订阅（重连后调用）
 */
function resubscribeAll() {
  var ids = Object.keys(subscriptions)
  for (var i = 0; i < ids.length; i++) {
    var sub = subscriptions[ids[i]]
    if (ws) {
      var subFrame = serializeFrame('SUBSCRIBE', {
        'id': sub.id,
        'destination': sub.destination
      })
      ws.send(subFrame)
      console.log('[WebSocket] 重新订阅:', sub.destination, 'id:', sub.id)
    }
  }
}

// ==================== 重连与心跳 ====================

/**
 * 安排重连
 */
function scheduleReconnect() {
  if (!shouldReconnect) return
  if (reconnectTimer) return
  console.log('[WebSocket] 将在 ' + RECONNECT_INTERVAL / 1000 + ' 秒后尝试重连...')
  reconnectTimer = setTimeout(function() {
    reconnectTimer = null
    if (!connected && !connecting) {
      connectWebSocket()
    }
  }, RECONNECT_INTERVAL)
}

/**
 * 启动 STOMP 心跳（定时发送换行符）
 */
function startHeartbeat() {
  stopHeartbeat()
  heartbeatTimer = setInterval(function() {
    if (ws && ws.readyState === WebSocket.OPEN && connected) {
      try {
        ws.send('\n')
      } catch (e) {
        // 忽略心跳发送异常
      }
    }
  }, HEARTBEAT_INTERVAL)
}

/**
 * 停止心跳
 */
function stopHeartbeat() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer)
    heartbeatTimer = null
  }
}

// ==================== 高级订阅函数 ====================

/**
 * 订阅单设备状态
 * @param {number|string} deviceNo - 设备编号
 * @param {function} callback - 回调函数
 * @returns {string} 订阅ID
 */
export function subscribeDeviceStatus(deviceNo, callback) {
  return subscribe('/topic/device/status/' + deviceNo, callback)
}

/**
 * 订阅全部设备状态变更
 * @param {function} callback - 回调函数
 * @returns {string} 订阅ID
 */
export function subscribeAllDeviceStatus(callback) {
  return subscribe('/topic/device/status/all', callback)
}

/**
 * 订阅设备告警
 * @param {number|string} deviceNo - 设备编号
 * @param {function} callback - 回调函数
 * @returns {string} 订阅ID
 */
export function subscribeDeviceAlarm(deviceNo, callback) {
  return subscribe('/topic/device/alarm/' + deviceNo, callback)
}

/**
 * 订阅功率数据
 * @param {function} callback - 回调函数
 * @returns {string} 订阅ID
 */
export function subscribePowerData(callback) {
  return subscribe('/topic/device/power', callback)
}

/**
 * D-3: 订阅设备机柜实时数据
 * @param {number|string} deviceNo - 设备编号
 * @param {function} callback - 回调函数
 * @returns {string} 订阅ID
 */
export function subscribeCabinetData(deviceNo, callback) {
  return subscribe('/topic/device/cabinet/' + deviceNo, callback)
}

/**
 * 获取当前连接状态
 * @returns {boolean}
 */
export function isWebSocketConnected() {
  return connected
}
