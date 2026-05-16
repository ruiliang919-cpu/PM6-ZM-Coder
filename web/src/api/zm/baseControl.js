import request from '@/utils/request'

// 照明控制-分区控制
export function zoneControlList(data) {
  return request({
    url: '/zm/lighting/zoneControlList',
    method: 'POST',
    data: data
  })
}

/**
 * 修改照明控制-分区控制
 * @param data
 * @returns {*}
 */
export function updateZoneLightSwitch(data = {
  zoneId: undefined,
  swStatus: undefined
}) {
  return request({
    url: '/zm/write/updateZoneLightSwitch',
    method: 'get',
    data: data,
    params: data
  })
}

/**
 * 修改照明控制-分区控制 调光
 * @param data
 * @returns {*}
 */
export function updateZoneLightLux(data = {
  zoneId: undefined,
  lux: undefined
}) {
  return request({
    url: '/zm/write/updateZoneLightLux',
    method: 'get',
    data: data,
    params: data
  })
}

/**
 * 照明状态&控制-照明控制-场景控制/进入场景（所有机柜）
 * @param data
 * @returns {*}
 */
export function intoScenes(data = {
  sceneId: undefined
}) {
  return request({
    url: '/zm/write/intoScenes',
    method: 'get',
    data: data,
    params: data
  })
}

/**
 * /zm/point/getZoneStatus
 */
export function getZoneStatus() {
  return request({
    url: '/zm/point/getZoneStatus',
    method: 'get'
  })
}

export function getSceneStatus() {
  return request({
    url: '/zm/point/getSceneStatus',
    method: 'get'
  })
}

/**
 * /zm/point/clearZoneStatus
 * @returns {*}
 */
export function clearZoneStatus() {
  return request({
    url: '/zm/point/clearZoneStatus',
    method: 'get'
  })
}

export function clearSceneStatus() {
  return request({
    url: '/zm/point/clearSceneStatus',
    method: 'get'
  })
}

//获取全亮/全灭状态

export function brightStatus() {
  return request({
    url: '/full/bright/status',
    method: 'get'
  })
}

export function brightSet(flag) {
  return request({
    url: '/full/bright/set',
    method: 'get',
    params: {
      flag: flag
    }
  })
}

export function getLoopStatus() {
  return request({
    url: '/zm/point/getLoopStatus',
    method: 'get'
  })
}

/**
 * /zm/point/clearZoneStatus
 * @returns {*}
 */
export function clearLoopStatus() {
  return request({
    url: '/zm/point/clearLoopStatus',
    method: 'get'
  })
}
