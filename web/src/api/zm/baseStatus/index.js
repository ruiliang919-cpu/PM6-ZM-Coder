import request from '@/utils/request'
// 查询照明状态列表
export function lightList(data) {
  return request({
    url: '/zm/lighting/lightList',
    method: 'POST',
    data: data
  })
}
