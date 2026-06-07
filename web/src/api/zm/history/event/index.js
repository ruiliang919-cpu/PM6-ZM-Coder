import request from '@/utils/request'

// 查询历史记录-历史事件列表
export function getRecordSeven(data = {
  startTime: undefined,
  deviceName: undefined,
  eventName: undefined,
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/record/getRecordSeven',
    method: 'POST',
    data: data
  })
}
