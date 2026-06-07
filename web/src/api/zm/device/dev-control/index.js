import request from '@/utils/request'

/**
 * 机柜控制-照明控制 总开关、工作模式、场景控制列表、回路控制列表、分组控制列表
 * @param data
 * @returns {*}
 */
export function lightControlList(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/process/lightControlList',
    method: 'GET',
    params: data
  })
}

/**
 * 机柜控制-模块控制 ACDC/DCDC输出电压
 * @param data
 * @returns {*}
 */
export function moduleControlList(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/process/moduleControlList',
    method: 'GET',
    params: data
  })
}

/**
 * 直流机柜-机柜控制-照明控制-场景控制/进入场景（单个机柜）
 * @param data
 * @returns {AxiosPromise}
 */
export function intoScene(data = {
  deviceId: undefined,
  sceneId: undefined
}) {
  return request({
    url: '/zm/write/intoScene',
    method: 'GET',
    params: data
  })
}

/**
 * 直流机柜-机柜控制-模块控制 AC/DC输出电压设置
 * @param data
 * @returns {*}
 */
export function updateAcDc(data = {
  deviceId: undefined,
  value: undefined
}) {
  return request({
    url: '/zm/write/updateAcDc',
    method: 'GET',
    params: data
  })
}

/**
 * 直流机柜-机柜控制-模块控制 DC/DC输出电压设置
 * @param data
 * @returns {*}
 */
export function updateDcDc(data = {
  deviceId: undefined,
  value: undefined
}) {
  return request({
    url: '/zm/write/updateDcDc',
    method: 'GET',
    params: data
  })
}

/**
 * 机柜控制-照明控制-总开关-开关控制
 * @param data
 * @returns {AxiosPromise}
 */
export function systemSwitch(data = {
  deviceId: undefined,
  systemSwitch: undefined
}) {
  return request({
    url: '/zm/write/systemSwitch',
    method: 'GET',
    params: data
  })
}

/**
 * 机柜控制-照明控制-工作模式-开关控制
 * @param data
 * @returns {AxiosPromise}
 */
export function workModule(data = {
  deviceId: undefined,
  workModule: undefined
}) {
  return request({
    url: '/zm/write/workModule',
    method: 'GET',
    params: data
  })
}

/**
 * 直流机柜-机柜控制-照明控制-回路控制-控制亮度
 * @param data
 * @returns {AxiosPromise}
 */
export function loopControlLux(data = {
  deviceId: undefined,
  lux: undefined,
  loopControl: []
}) {
  return request({
    url: '/zm/write/loopControlLux',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 直流机柜-机柜控制-照明控制-回路控制-控制开关
 * @param data
 * @returns {*}
 */
export function loopControlSwitch(data = {
  deviceId: undefined,
  switchStatus: undefined,
  loopControl: []
}) {
  return request({
    url: '/zm/write/loopControlSwitch',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 直流机柜-机柜控制-照明控制-分组控制-控制亮度
 * @param data
 * @returns {AxiosPromise}
 */
export function groupControlLux(data = {
  deviceId: undefined,
  lux: undefined,
  groupSelectArr: []
}) {
  return request({
    url: '/zm/write/groupControlLux',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 直流机柜-机柜控制-照明控制-分组控制-控制开关
 * @param data
 * @returns {*}
 */
export function groupControlSwitch(data = {
  deviceId: undefined,
  switchStatus: undefined,
  groupSelectArr: []
}) {
  return request({
    url: '/zm/write/groupControlSwitch',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 获取设备当前模式
 * @param data
 * @returns {AxiosPromise}
 */
export function getModuleNow(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/write/getModuleNow',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *  系统设置-控制方式-时控模式选择
 * @param data
 * @returns {AxiosPromise}
 */
export function selectTimeModule(data = {
  deviceId: undefined,
  timeModule: undefined
}) {

  if (data.timeModule === 'noEnabled') {
    data.timeModule = 1
  } else if (data.timeModule === 'simple') {
    data.timeModule = 2
  } else if (data.timeModule === 'scene') {
    data.timeModule = 3
  } else {
    data.timeModule = undefined
  }

  return request({
    url: '/zm/write/selectTimeModule',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *  系统设置-控制方式-红外传感模式
 * @param data
 * @returns {AxiosPromise}
 */
export function selectInfraredModule(data = {
  deviceId: undefined,
  enabled: undefined
}) {

  return request({
    url: '/zm/write/selectInfraredModule',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 * 系统设置-控制方式-照度传感模式
 * @param data
 * @returns {AxiosPromise}
 */
export function selectIlluminanceModule(data = {
  deviceId: undefined,
  enabled: undefined
}) {

  return request({
    url: '/zm/write/selectIlluminanceModule',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 * 系统控制-照明控制-手动模式选择
 * @param data
 * @returns {AxiosPromise}
 */
export function selectHandModule(data = {
  deviceId: undefined,
  handModule: undefined
}) {
  if (data.handModule === 'loop') {
    data.handModule = 1
  } else if (data.handModule === 'group') {
    data.handModule = 2
  } else if (data.handModule === 'scene') {
    data.handModule = 3
  } else {
    data.handModule = undefined
  }
  return request({
    url: '/zm/write/selectHandModule',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 * 直流机柜-机柜控制-交流开关控制 POST /acs/getTable
 */
export function acsGetTable(data = {}) {
  return request({
    url: '/acs/getTable',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 *交流开关，开
 */
export function acsOn(data = {
  deviceId: undefined,
  onNo: undefined
}) {

  return request({
    url: '/acs/on',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *交流开关，关
 */
export function acsOff(data = {
  deviceId: undefined,
  offNo: undefined
}) {

  return request({
    url: '/acs/off',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *交流开关，全部开
 */
export function acsAllOn(data = {
  deviceId: undefined
}) {

  return request({
    url: '/acs/allOn',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *交流开关，全部关
 */
export function acsAllOff(data = {
  deviceId: undefined
}) {

  return request({
    url: '/acs/allOff',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 * 直流机柜-机柜控制-其他开关控制 POST /otherSwitch/getTable
 */
export function otherSwitchGetTable(data = {}) {
  return request({
    url: '/otherSwitch/getTable',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 *其他开关，开
 */
export function otherSwitchOn(data = {
  deviceId: undefined,
  onNo: undefined
}) {

  return request({
    url: '/otherSwitch/on',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *其他开关，关
 */
export function otherSwitchOff(data = {
  deviceId: undefined,
  offNo: undefined
}) {

  return request({
    url: '/otherSwitch/off',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *其他开关，全部开
 */
export function otherSwitchAllOn(data = {
  deviceId: undefined
}) {

  return request({
    url: '/otherSwitch/allOn',
    method: 'get',
    params: data
    // data: data
  })
}

/**
 *其他开关，全部关
 */
export function otherSwitchAllOff(data = {
  deviceId: undefined
}) {

  return request({
    url: '/otherSwitch/allOff',
    method: 'get',
    params: data
    // data: data
  })
}
