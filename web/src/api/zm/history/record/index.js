import request from '@/utils/request'

// 查询历史记录-历史记录列表
export function getFaultAll(data = {
  startTime: undefined,
  deviceName: undefined,
  eventName: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/record/getFaultAll',
    method: 'POST',
    data: data
  })
}
