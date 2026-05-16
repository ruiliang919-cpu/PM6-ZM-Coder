import request from '@/utils/request'

// 获取每一个机柜的总耗电量
export function powersByAll(query) {
  return request({
    url: '/zm/home/powersByAll',
    method: 'get',
    params: query
  })
}

// 获取每一个机柜的日耗电量
export function powersByDay(query) {
  return request({
    url: '/zm/home/powersByDay',
    method: 'get',
    params: query
  })
}

// 机柜综合信息
export function cabinetList(data) {
  return request({
    url: '/zm/home/cabinetList',
    method: 'POST',
    data: data
  })
}

//就地/远程
export function getModule(data = {
  module: undefined
}) {
  return request({
    url: '/zm/point/getModule',
    method: 'get',
    params: data
  })
}

export function getVersion(data = {
  module: undefined
}) {
  return request({
    url: '/zm/home/version',
    method: 'get',
    params: data
  })
}
