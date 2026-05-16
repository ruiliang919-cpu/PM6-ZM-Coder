import request from '@/utils/request'

//刷新
export function getStatus(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/point/getStatus',
    method: 'get',
    params: data
  })
}

export function setStatus(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/point/setStatus',
    method: 'get',
    params: data
  })
}

// ============================================= 回路分组模块开始 ====================================================
/**
 * 机柜设置-回路分组-查询回路分组列表
 * @param data
 * @returns {*}
 */
export function configGroupList(data = {
  pageNum: 1,
  pageSize: 10,
  deviceId: undefined
}) {
  return request({
    url: '/zm/configGroup/list',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-回路分组-根据分组ID查询对应的回路
 * @param data
 * @returns {*}
 */
export function getLoopsByGroup(data = {
  slaveId: undefined,
  groupId: undefined
}) {
  return request({
    url: '/zm/configGroup/getLoopsByGroup',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-回路分组-根据设备ID查询未被分组的回路
 * @param data
 * @returns {*}
 */
export function getLoopsByFree(data = {
  slaveId: undefined,
  groupId: undefined
}) {
  return request({
    url: '/zm/configGroup/getLoopsByFree',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-回路分组-分区下拉列表
 * @param data
 * @returns {*}
 */
export function getDistrictList(data = {}) {
  return request({
    url: '/zm/configGroup/getDistrictList',
    method: 'GET',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-回路分组- 获取与分区下拉列表对应的ID
 * @param data
 * @returns {*}
 */
export function getDistrictByGroupId(data = {
  slaveId: undefined,
  groupId: undefined
}) {
  return request({
    url: '/zm/configGroup/getDistrictByGroupId',
    method: 'GET',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-回路分组-保存接口
 * @param data
 * @returns {*}
 */
export function updateLoop(data = {
  deviceId: undefined,
  groupId: undefined,
  zoneId: undefined,
  loopNo: []
}) {
  return request({
    url: '/zm/write/updateLoop',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 获取分组名称的下拉列表
 * @param data
 * @returns {*}
 */
export function getDistrictNames(data = {}) {
  return request({
    // url: '/zm/configGroup/getDistrictNames',
    url: '/zm/configGroup/getDistrictNames',
    // method: 'post',
    method: 'get',
    params: data
    // data: data
  })
}

export function sceneName(data = {
  deviceId: undefined,
  sceneId: undefined,
  name: undefined
}) {
  return request({
    url: '/zm/write/sceneName',
    method: 'get',
    params: data
    // data: data
  })
}

export function groupName(data = {
  deviceId: undefined,
  groupId: undefined,
  name: undefined
}) {
  return request({
    url: '/zm/write/groupName',
    method: 'post',
    // params: data
    data: data
  })
}

// ============================================= 回路分组模块结束 ====================================================

// ============================================= 场景设置模块开始 ====================================================

/**
 * 机柜设置-场景设置-场景参数
 * @param data
 * @returns {*}
 */
export function getSceneParamsList(data = {
  deviceId: undefined,
  sceneId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/configGroup/getSceneParamsList',
    method: 'post',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-场景设置-场景参数 保存接口
 * @param data
 * @returns {*}
 */
export function updateSceneParams(data = {
  deviceId: undefined,
  sceneId: undefined,
  selectGroupIdArr: [],
  selectLuxArr: [],
  selectSwitchArr: []
}) {
  return request({
    url: '/zm/write/updateSceneParams',
    method: 'post',
    // params: data,
    data: data
  })
}

/**
 * 获取场景名称下拉列表
 * @param data
 * @returns {*}
 */
export function getSceneNames(data = {}) {
  return request({
    url: '/zm/scene/getSceneNames',
    method: 'get',
    params: data
  })
}

// ============================================= 场景设置模块结束 ====================================================

// ============================================= 时控模式模块开始 ====================================================
/**
 * 查询普通模式时控列表
 * @param data
 * @returns {*}
 */
export function timeControlSimpleList(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/timeControl/simpleList',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 查询场景时控列表
 * @param data
 * @returns {*}
 */
export function timeControlSceneList(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/timeControl/sceneList',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取普通时控下拉列表
 * @param data
 * @returns {*}
 */
export function simpleListSelect(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/timeControl/simpleListSelect',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 获取普通时控下拉列表
 * @param data
 * @returns {*}
 */
export function sceneListSelect(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/timeControl/sceneListSelect',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 获取参数引用的机柜列表（普通+场景）
 * @param data
 * @returns {*}
 */
export function getCabinetList(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/timeControl/getCabinetList',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 普通时控 参数引用 点击确定后得到的时序表数据
 * @param data
 * @returns {*}
 */
export function simpleTimeScale(data = {
  deviceId: undefined,
  controlId: undefined
}) {
  return request({
    // url: '/zm/timeControl/simpleTimeScale',
    url: '/zm/timeControl/sceneList',
    // method: 'get',
    method: 'POST',
    params: data,
    data: data
  })
}

export function simpleList(data = {
  deviceId: undefined,
  controlId: undefined
}) {
  return request({
    // url: '/zm/timeControl/simpleTimeScale',
    url: '/zm/timeControl/simpleList',
    // method: 'get',
    method: 'get',
    params: data,
    data: data
  })
}

export function sceneList(data = {
  deviceId: undefined,
  controlId: undefined
}) {
  return request({
    // url: '/zm/timeControl/simpleTimeScale',
    url: '/zm/timeControl/sceneList',
    // method: 'get',
    method: 'post',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-交流开关 列表
 * @param data
 * @returns {*}
 */
export function acSwitchControlList(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/baseDevice/acSwitchControlList',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 直流机柜-机柜设置-交流开关 更新列表
 * @param data
 * @returns {*}
 */
export function updateAcControl(data = []) {
  return request({
    url: '/zm/write/updateAcControl',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 机柜设置-交流开关 列表
 * @param data
 * @returns {*}
 */
export function simpleGroupList(data = {
  deviceId: undefined,
  controlId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/timeControl/simpleGroupList',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 根据设备ID与普通时控ID获取普通时控模式是否启用
 * @param data
 * @returns {*}
 */
export function getSimpleEnabled(data = {
  deviceId: undefined,
  controlId: undefined
}) {
  return request({
    url: '/zm/timeControl/getSimpleEnabled',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 根据设备ID与场景时控ID获取场景时控模式是否启用
 * @param data
 * @returns {*}
 */
export function getSceneEnabled(data = {
  deviceId: undefined,
  controlId: undefined
}) {
  return request({
    url: '/zm/timeControl/getSceneEnabled',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-时控模式-普通时控模式参数-保存接口
 * @param data
 * @returns {*}
 */
export function updateSimpleControl(data = {
  deviceId: undefined,
  controlId: undefined,
  enabled: undefined,
  groupIds: [],
  table: []
}) {
  return request({
    url: '/zm/write/updateSimpleControl',
    method: 'POST',
    // params: data,
    data: data
  })
}

export function updateSimpleControlToAll(data = {
  deviceId: undefined,
  controlId: undefined,
  enabled: undefined,
  groupIds: [],
  table: []
}) {
  return request({
    url: '/zm/write/updateSimpleControlToAll',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 机柜设置-时控模式-场景时控模式参数-保存接口
 * @param data
 * @returns {AxiosPromise}
 */
export function updateSceneControl(data = {
  deviceId: undefined,
  controlId: undefined,
  enabled: undefined,
  table: []
}) {
  return request({
    url: '/zm/write/updateSceneControl',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 机柜设置-时控模式-场景时控模式参数-一键设置参数接口
 * @param data
 * @returns {AxiosPromise}
 */
export function updateSceneControlToAll(data = {
  deviceId: undefined,
  controlId: undefined,
  enabled: undefined,
  table: []
}) {
  return request({
    url: '/zm/write/updateSceneControlToAll',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 *
 * @param data
 * @returns {AxiosPromise}
 */
export function getSceneList(data = {}) {
  return request({
    url: '/zm/timeControl/getSceneList',
    method: 'get',
    params: data,
    data: data
  })
}


/**
 *
 * @param data
 * @returns {AxiosPromise}
 */
export function getModuleSceneList(data = {}) {
  return request({
    url: '/zm/timeControl/getModuleSceneList',
    method: 'get',
    params: data,
    data: data
  })
}
// ============================================= 时控模式模块结束 ====================================================

// ============================================= 传感模式模块开始 ====================================================

/**
 * 启用外控
 */
export function illuminanceEnabled(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/sensorModule/illuminanceEnabled',
    method: 'get',
    params: data
  })
}

/**
 * 机柜设置-传感模式-照度传感器-下拉列表
 * @param data
 * @returns {AxiosPromise}
 */
export function illuminanceSelectList(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/sensorModule/illuminanceSelectList',
    method: 'get',
    params: data
  })
}

/**
 * 机柜设置-传感模式-红外传感器-下拉列表
 * @param data
 * @returns {AxiosPromise}
 */
export function infraredSelectList(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/sensorModule/infraredSelectList',
    method: 'get',
    params: data
  })
}

/**
 * 机柜设置-传感模式-红外传感器-照明参数
 * @param data
 * @returns {AxiosPromise}
 */
export function infraredParams(data = {
  deviceId: undefined,
  sensorId: undefined
}) {
  return request({
    url: '/zm/sensorModule/infraredParams',
    method: 'get',
    params: data
  })
}

/**
 * 机柜设置-传感模式-照度传感器-照明参数
 * @param data
 * @returns {AxiosPromise}
 */
export function illuminanceParams(data = {
  deviceId: undefined,
  sensorId: undefined
}) {
  return request({
    url: '/zm/sensorModule/illuminanceParams',
    method: 'get',
    params: data
  })
}


export function getAddress(data = {
  deviceId: undefined,
  sensorId: undefined
}) {
  return request({
    url: '/zm/sensorModule/getAddress',
    method: 'get',
    params: data
  })
}
export function getIllLux(data = {
  deviceId: undefined,
  moduleId: undefined,
  sourceId:undefined
}) {
  return request({
    url: '/zm/sensorModule/getIllLux',
    method: 'get',
    params: data
  })
}


export function getIllBaseLux(data = {
  deviceId: undefined,
  moduleId: undefined
}) {
  console.log(data)
  return request({
    url: '/zm/sensorModule/getIllBaseLux',
    method: 'get',
    params: data
  })
}

/**
 * 机柜设置-传感模式-红外传感器-分组选择
 * @param data
 * @returns {AxiosPromise}
 */
export function infraredGroupSelect(data = {
  deviceId: undefined,
  sensorId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/sensorModule/infraredGroupSelect',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-传感模式-照度传感器-分组选择
 * @param data
 * @returns {AxiosPromise}
 */
export function illuminanceGroupSelect(data = {
  deviceId: undefined,
  sensorId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/sensorModule/illuminanceGroupSelect',
    method: 'POST',
    params: data,
    data: data
  })
}

export function updateInfraredParams(data = {
  deviceId: undefined,
  controlId: undefined,
  enabled: undefined,
  groupIds: [],
  table: []
}) {
  return request({
    url: '/zm/write/updateInfraredParams',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 机柜设置-传感模式-照度传感器
 * @param data
 * @returns {*}
 */
export function updateIlluminanceParams(data = {
  deviceId: undefined,
  sensorId: undefined,
  enabled: undefined,
  groupIds: [],
  table: []
}) {
  return request({
    url: '/zm/write/updateIlluminanceParams',
    method: 'POST',
    // params: data,
    data: data
  })
}

// ============================================= 传感模式模块结束 ====================================================

// ============================================= 其他设置模块结束 ====================================================
/**
 * 机柜信息-传感器信息-根据设备ID查询照度传感器列表
 * @param data
 * @returns {*}
 */
export function otherMsg(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/baseDevice/otherMsg',
    method: 'get',
    params: data,
    data: data
  })
}

/**
 * 机柜设置-其他设置-机柜信息保存
 * @param data
 * @returns {*}
 */
export function deviceSave(data = {
  deviceId: undefined,
  deviceName: undefined,
  zoneId: undefined
}) {
  return request({
    url: '/zm/write/deviceSave',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 机柜设置-其他设置-通讯参数保存
 * @param data
 * @returns {*}
 */
export function communicateSave(data = {
  deviceId: undefined,
  deviceNo: undefined,
  ip: undefined
}) {
  return request({
    url: '/zm/write/communicateSave',
    method: 'POST',
    // params: data,
    data: data
  })
}

/**
 * 机柜设置-其他设置-对时保存
 * @param data
 * @returns {*}
 */
export function timeSave(data = {
  deviceId: undefined,
  year: undefined,
  month: undefined,
  day: undefined,
  hour: undefined,
  min: undefined,
  second: undefined
}) {
  return request({
    url: '/zm/write/timeSave',
    method: 'POST',
    // params: data,
    data: data
  })
}

// ============================================= 其他设置模块结束 ====================================================
