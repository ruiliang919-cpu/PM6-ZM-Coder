import request from '@/utils/request'

// 查询照明状态列表
export function setModule(data = {
  module: undefined
}) {
  return request({
    url: '/zm/point/setModule',
    method: 'get',
    params: data
  })
}

//手动采集设备电量
export function testGetData(data = {
  module: undefined
}) {
  return request({
    url: '/zm/time/test/getData',
    method: 'get',
    params: data
  })
}
