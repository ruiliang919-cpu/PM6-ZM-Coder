import request from '@/utils/request'

// 机柜记录-耗电统计-总耗电量
export function getOneTotalElectricByDeviceId(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/powerRecord/getOneTotalElectricByDeviceId',
    method: 'get',
    params: data
  })
}

//根据设备ID获取机柜的总功率
export function getOneTotalPower(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/powerRecord/getOneTotalPower',
    method: 'get',
    params: data
  })
}
//获取机柜的设备类型
export function getDeviceType(data = {
  deviceId: undefined
}) {
  return request({
    url: '/zm/energy/getDeviceType',
    method: 'get',
    params: data
  })
}


//机柜记录-耗电统计-日耗电量列表
export function powerRecordGetDay(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/powerRecord/getDay',
    method: 'POST',
    params: data,
    data: data
  })
}

//机柜记录-耗电统计-周耗电量列表
export function powerRecordGetWeek(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/powerRecord/getWeek',
    method: 'POST',
    params: data,
    data: data
  })
}

//机柜记录-耗电统计-月耗电量列表
export function powerRecordGetMonth(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/powerRecord/getMonth',
    method: 'POST',
    params: data,
    data: data
  })
}

//机柜记录-耗电统计-季耗电量列表
export function powerRecordGetQuarter(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/powerRecord/getQuarter',
    method: 'POST',
    params: data,
    data: data
  })
}

//机柜记录-耗电统计-年耗电量列表
export function powerRecordGetYear(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/powerRecord/getYear',
    method: 'POST',
    params: data,
    data: data
  })
}

//机柜记录-事件记录列表（显示近七天数据）
export function getRecordOne(data = {
  deviceId: undefined,
  startTime: undefined,
  endTime: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/record/getRecordOne',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取四十电表总耗电量
 * @param data
 * @returns {AxiosPromise}
 */
export function meterTotalEnergy(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/total',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取单一电表年耗电量
 * @param data
 * @returns {AxiosPromise}
 */
export function meterYearEnergy(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/meterYearEnergy',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取单一电表月耗电量
 * @param data
 * @returns {AxiosPromise}
 */
export function meterMonthEnergy(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/month',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取单一电表日耗电量
 * @param data
 * @returns {AxiosPromise}
 */
export function meterDayEnergy(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/day',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取单一电表周耗电量
 * @param data
 * @returns {AxiosPromise}
 */
export function meterWeekEnergy(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/week',
    method: 'POST',
    params: data,
    data: data
  })
}

/**
 * 获取单一电表季耗电量
 * @param data
 * @returns {AxiosPromise}
 */
export function meterQuarterEnergy(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/quarter',
    method: 'POST',
    params: data,
    data: data
  })
}
export function allPower(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/energy/allPower',
    method: 'POST',
    params: data,
    data: data
  })
}
