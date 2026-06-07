import request from '@/utils/request'

// 查询设备区域列表
export function listBaseRegion(query) {
  return request({
    url: '/zm/baseRegion/list',
    // method: 'get',
    method: 'post',
    // params: query
    data: query
  })
}

export function listBaseRegionNoPage(query) {
  return request({
    url: '/zm/baseRegion/noPageList',
    // method: 'get',
    method: 'get',
    // params: query
    data: query
  })
}

// 查询设备区域详细
export function getBaseRegion(id) {
  return request({
    url: '/zm/baseRegion/' + id,
    method: 'get'
  })
}

// 新增设备区域
export function addBaseRegion(data) {
  return request({
    // url: '/zm/baseRegion',
    url: '/zm/baseRegion/add',
    method: 'post',
    data: data
  })
}

// 修改设备区域
export function updateBaseRegion(data) {
  return request({
    // url: '/zm/baseRegion',
    url: '/zm/baseRegion/edit',
    method: 'put',
    data: data
  })
}

// 删除设备区域
export function delBaseRegion(id) {
  return request({
    url: '/zm/baseRegion/' + id,
    method: 'delete'
  })
}
