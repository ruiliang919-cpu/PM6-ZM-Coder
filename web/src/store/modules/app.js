import Cookies from 'js-cookie'
import { CookiesInElectron, isInElectron } from '@/utils/ruoyi'

const state = {
  sidebar: {
    opened: (isInElectron() ? CookiesInElectron.get('sidebarStatus') : Cookies.get('sidebarStatus')) ? !!+
      (isInElectron() ? CookiesInElectron.get('sidebarStatus') : Cookies.get('sidebarStatus')) : true,
    withoutAnimation: false,
    hide: false
  },
  device: 'desktop',
  size: isInElectron() ? CookiesInElectron.get('size') : Cookies.get('size') || 'medium'
}

const mutations = {
  TOGGLE_SIDEBAR: state => {
    if (state.sidebar.hide) {
      return false
    }
    state.sidebar.opened = !state.sidebar.opened
    state.sidebar.withoutAnimation = false
    if (state.sidebar.opened) {
      isInElectron() ? CookiesInElectron.set('sidebarStatus', 1) : Cookies.set('sidebarStatus', 1)
    } else {
      isInElectron() ? CookiesInElectron.set('sidebarStatus', 0) : Cookies.set('sidebarStatus', 0)
    }
  },
  CLOSE_SIDEBAR: (state, withoutAnimation) => {
    isInElectron() ? CookiesInElectron.set('sidebarStatus', 0) : Cookies.set('sidebarStatus', 0)
    state.sidebar.opened = false
    state.sidebar.withoutAnimation = withoutAnimation
  },
  TOGGLE_DEVICE: (state, device) => {
    state.device = device
  },
  SET_SIZE: (state, size) => {
    state.size = size
    isInElectron() ? CookiesInElectron.set('size', size) : Cookies.set('size', size)
  },
  SET_SIDEBAR_HIDE: (state, status) => {
    state.sidebar.hide = status
  }
}

const actions = {
  toggleSideBar({ commit }) {
    commit('TOGGLE_SIDEBAR')
  },
  closeSideBar({ commit }, { withoutAnimation }) {
    commit('CLOSE_SIDEBAR', withoutAnimation)
  },
  toggleDevice({ commit }, device) {
    commit('TOGGLE_DEVICE', device)
  },
  setSize({ commit }, size) {
    commit('SET_SIZE', size)
  },
  toggleSideBarHide({ commit }, status) {
    commit('SET_SIDEBAR_HIDE', status)
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
