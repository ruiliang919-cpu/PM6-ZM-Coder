import request from '@/utils/request'

//机柜
export function calculationCabinetYear(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/cabinet/year',
    method: 'POST',
    data: data
  })
}

export function calculationCabinetQuarter(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/cabinet/quarter',
    method: 'POST',
    data: data
  })
}

export function calculationCabinetMonth(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/cabinet/month',
    method: 'POST',
    data: data
  })
}

export function calculationCabinetWeek(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/cabinet/week',
    method: 'POST',
    data: data
  })
}

export function calculationCabinetDay(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/cabinet/day',
    method: 'POST',
    data: data
  })
}

//分区
export function calculationZoneYear(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/year',
    method: 'POST',
    data: data
  })
}

export function calculationZoneQuarter(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/quarter',
    method: 'POST',
    data: data
  })
}

export function calculationZoneMonth(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/month',
    method: 'POST',
    data: data
  })
}

export function calculationZoneWeek(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/week',
    method: 'POST',
    data: data
  })
}

export function calculationZoneDay(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/day',
    method: 'POST',
    data: data
  })
}

export function lightZoneLeftList(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/light/zone/leftList',
    method: 'GET',
    params: data
  })
}
export function calculationZoneList(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/list',
    method: 'POST',
    data: data
  })
}
export function calculationZoneZoneList(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/zoneList',
    method: 'get',
    params: data
  })
}


export function calculationZoneSet(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/zm/calculation/zone/set',
    method: 'post',
    data: data
  })
}

export function lightZoneSet(data = {
  pageNum: 1,
  pageSize: 10
}) {
  return request({
    url: '/light/zone/set',
    method: 'post',
    data: data
  })
}
