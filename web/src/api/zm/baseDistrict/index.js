import request from '@/utils/request'

// 查询控制分区列表
export function listBaseDistrict(query) {
  return request({
    // url: '/zm/baseDistrict/list',
    // url: '/zm/basic/getControlPartitionList',
    url: '/zm/baseDistrict/list',
    method: 'post',
    // params: query,
    data: query
  })
}

// 查询控制分区详细
export function getBaseDistrict(id) {
  return request({
    url: '/zm/baseDistrict/' + id,
    method: 'get'
  })
}

// 新增控制分区
export function addBaseDistrict(data) {
  return request({
    // url: '/zm/baseDistrict',
    url: '/zm/baseDistrict/add',
    method: 'post',
    data: data
  })
}

// 修改控制分区
export function updateBaseDistrict(data) {
  return request({
    // url: '/zm/baseDistrict',
    url: '/zm/baseDistrict/edit',
    method: 'put',
    data: data
  })
}

// 删除控制分区
export function delBaseDistrict(id) {
  return request({
    url: '/zm/baseDistrict/' + id,
    method: 'delete'
  })
}
