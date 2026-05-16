import Cookies from 'js-cookie'
import { CookiesInElectron, isInElectron } from '@/utils/ruoyi'

const TokenKey = 'Admin-Token'

export function getToken() {
  if (isInElectron()) return CookiesInElectron.get(TokenKey)
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  if (isInElectron()) return CookiesInElectron.set(TokenKey, token)
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  if (isInElectron()) return CookiesInElectron.remove(TokenKey)
  return Cookies.remove(TokenKey)
}
