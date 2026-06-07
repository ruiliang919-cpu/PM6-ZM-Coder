import request from '@/utils/request'

// 查询机柜列表
export function listBaseDevice(query) {
  return request({
    // url: '/zm/baseDevice/list',
    url: '/zm/basic/getCabinetList',
    // method: 'get',
    method: 'post',
    data: query
  })
}

// 查询机柜详细
export function getBaseDevice(id) {
  return request({
    url: '/zm/baseDevice/' + id,
    method: 'get'
  })
}

// 新增机柜
export function addBaseDevice(data) {
  return request({
    // url: '/zm/baseDevice',
    // url: '/zm/basic/addCabinet',
    url: '/zm/baseDevice/add',
    method: 'post',
    data: data
  })
}

// 修改机柜
export function updateBaseDevice(data) {
  return request({
    // url: '/zm/baseDevice',
    url: '/zm/basic/editCabinet',
    // method: 'put',
    method: 'post',
    data: data
  })
}

// 删除机柜
export function delBaseDevice(id) {
  return request({
    // url: '/zm/baseDevice/' + id,
    url: '/zm/baseDevice/remove/' + id,
    method: 'delete'
  })
}

//无分页获取设备区域
export function noPageList(query) {
  return request({
    url: '/zm/baseRegion/noPageList',
    method: 'get',
    params: query
  })
}

//初始化设备
export function baseDeviceInit(query = { deviceId: undefined }) {
  return request({
    url: '/zm/baseDevice/init',
    method: 'get',
    params: query
  })
}

//验证密码 /recover/password?password=123456
export function recoverPassword(query = { password: undefined }) {
  return request({
    url: '/recover/password',
    method: 'get',
    params: query
  })
}

//迁移设备数据
export function recoverIssued(query = { deviceId: undefined }) {
  return request({
    url: '/recover/issued',
    method: 'get',
    params: query
  })
}

//读取设备最新数据
export function recoverRead(query = { deviceId: undefined }) {
  return request({
    url: '/recover/read',
    method: 'get',
    params: query
  })
}
