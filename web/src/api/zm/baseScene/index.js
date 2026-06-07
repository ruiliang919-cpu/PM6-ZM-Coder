import request from '@/utils/request'

// 查询场景列表
export function listBaseScene(query = {}) {
  return request({
    // url: '/zm/baseScene/list',
    url: '/zm/basic/getSceneList',
    method: query.method ||  'get',
    params: query,
    data: query
  })
}

export function getSceneSelectList(query) {
  return request({
    // url: '/zm/baseScene/list',
    url: '/zm/baseScene/getSceneSelectList',
    method: 'get',
    params: query,
    data: query
  })
}


// 查询场景详细
export function getBaseScene(id) {
  return request({
    url: '/zm/baseScene/' + id,
    method: 'get'
  })
}

// 新增场景
export function addBaseScene(data) {
  return request({
    // url: '/zm/baseScene',
    url: '/zm/baseScene/add',
    method: 'post',
    data: data
  })
}

// 修改场景
export function updateBaseScene(data) {
  return request({
    // url: '/zm/baseScene',
    url: '/zm/baseScene/edit',
    method: 'put',
    data: data
  })
}

// 删除场景
export function delBaseScene(id) {
  return request({
    url: '/zm/baseScene/' + id,
    method: 'delete'
  })
}
