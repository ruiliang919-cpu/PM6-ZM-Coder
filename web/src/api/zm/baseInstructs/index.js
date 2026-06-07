import request from '@/utils/request'

/**
 * 获取报文
 * @param data
 * @returns {*}
 */
export function getInstructs(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/basic/getInstructs',
    method: 'POST',
    params: data,
    data: data
  })
}
