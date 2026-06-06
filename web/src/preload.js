// preload script for electron
const { ipcRenderer } = require('electron')

window.ipcRenderer = ipcRenderer

// user.js 中调用的全局函数
window.cantCloseWindow = () => ipcRenderer.send('cantClose')
window.canCloseWindow = () => ipcRenderer.send('canClose')
