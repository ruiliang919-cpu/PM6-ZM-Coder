import request from '@/utils/request'

// 回路信息-交流回路信息
export function acList(data = {
  slaveId: -1,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/cabinetInfo/acList',
    method: 'POST',
    params: data,
    data: data
  })
}

// 回路信息-直流回路状态
export function dccList(data = {
  slaveId: -1,
  pageNum: 1,
  pageSize: 10
}) {

  return request({
    url: '/zm/cabinetInfo/dccList',
    method: 'POST',
    params: data,
    data: data
  })

}

// 回路信息-母线信息
export function busInfo(data = {
  slaveId: undefined,
}) {
  return request({
    url: '/zm/cabinetInfo/busInfo',
    method: 'get',
    params: data,
  })
}

// 回路信息-DC/DC AC/DC信息
export function dcAc(data = {
  slaveId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/cabinetInfo/dcAc',
    method: 'POST',
    params: data,
    data: data
  })
}

// 回路信息-母线绝缘
export function getBusInsulation(data = {
  slaveId: undefined,
}) {
  return request({
    url: '/zm/cabinetInfo/getBusInsulation',
    method: 'get',
    params: data,
  })
}

// 交流信息-交流 1 路与 2 路
export function getAlternating(data = {
  slaveId: undefined,
}) {
  return request({
    url: '/zm/cabinetInfo/getAlternating',
    method: 'get',
    params: data,
  })
}

//直流机柜-机柜信息-传感器信息-表格 POST /leakage/getTable
export function leakageGetTable(data = {
  deviceId: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/leakage/getTable',
    method: 'POST',
    params: data,
    data: data
  })
}
