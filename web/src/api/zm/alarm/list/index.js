import request from '@/utils/request'

// 查询当前告警-告警信息列表
export function getFaultSeven(data = {
  startTime: undefined,
  deviceName: undefined,
  eventName: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/record/getFaultSeven',
    method: 'POST',
    data: data
  })
}

//最新一条告警记录
export function getNews(data = {}) {
  return request({
    url: '/zm/record/getNews',
    method: 'get'
  })
}
