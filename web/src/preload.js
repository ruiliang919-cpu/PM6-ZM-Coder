const { ipcRenderer, remote } = require('electron')

window.ipcRenderer = ipcRenderer

window.openDevTools = function() {
  console.log('执行了openDevTools')
  ipcRenderer.send('openDevTools')
}

window.cantCloseWindow = function() {
  ipcRenderer.send('cantClose')
}

window.canCloseWindow = function() {
  ipcRenderer.send('canClose')
}

let menuContextTemplate = [
  {
    label: '刷新',
    click() {
      console.log('准备刷新')
      window.location.reload()
    }
  },
  {
    label: '开发者工具',
    click() {
      ipcRenderer.send('openDevTools')
    }
  }
]
let menuBuilder = remote.Menu.buildFromTemplate(menuContextTemplate)

window.openMenu = function() {
  // 调用popup方法弹出菜单
  menuBuilder.popup({
    window: remote.getCurrentWindow()
  })
}
