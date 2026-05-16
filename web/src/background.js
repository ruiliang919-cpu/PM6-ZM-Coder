'use strict'

import { app, protocol, BrowserWindow, ipcMain, remote } from 'electron'
import { createProtocol } from 'vue-cli-plugin-electron-builder/lib'
import installExtension, { VUEJS_DEVTOOLS } from 'electron-devtools-installer'

const path = require('path')

const isDevelopment = process.env.NODE_ENV !== 'production'

let canClose = true

// Scheme must be registered before the app is ready
protocol.registerSchemesAsPrivileged([
  { scheme: 'app', privileges: { secure: true, standard: true } }
])

let win
const gotTheLock = app.requestSingleInstanceLock()
if (!gotTheLock) {
  app.quit()
} else {
  app.on('second-instance', () => {
    if (win) {
      if (win.isMinimized()) win.restore()
      win.focus()
    }
  })
}

async function createWindow() {
  // Create the browser window.
  win = new BrowserWindow({
    width: 1280,
    height: 720,
    minWidth: 1280,
    minHeight: 600,
    icon:path.join(__dirname, 'assets/logo/logo.png'),
    // titleBarStyle:"hidden",
    // titleBarOverlay:{
    //   color: '#304157',
    //   height: 'auto',
    //   symbolColor: '#ffffff',
    //   shadowColor: 'rgba(0,0,0,0)',
    // },
    webPreferences: {

      // Use pluginOptions.nodeIntegration, leave this alone
      // See nklayman.github.io/vue-cli-plugin-electron-builder/guide/security.html#node-integration for more info
      nodeIntegration: true,
      contextIsolation: false,
      enableRemoteModule: true,
      preload: path.join(__dirname, 'preload.js')
    },
    closable: true,
    minimizable: true
  })
  // win.webContents.openDevTools()
  win.on('close', e => {
    if(!canClose){
      e.preventDefault()
      console.log("send logout")
      win.webContents.send("LogOut")
    }
  })

  // win.on('minimize', e => {
  //   if(!canClose){
  //     e.preventDefault()
  //     console.log("send logout")
  //     win.webContents.send("LogOut")
  //   }
  // })

  // let template = [
  //   {
  //     label: '工具',
  //     submenu: [
  //       {
  //         label: '打开开发者模式',
  //         click() {
  //           win.webContents.openDevTools()
  //         }
  //       }
  //     ]
  //   }
  // ]
  // const menu = Menu.buildFromTemplate(template)

  //去除菜单栏
  win.setMenu(null)

  ipcMain.on('openDevTools', (e, data) => {
    console.log('收到vue的传信')
    win.webContents.openDevTools()
  })

  //win.webContents.openDevTools()
  if (process.env.WEBPACK_DEV_SERVER_URL) {
    // Load the url of the dev server if in development mode
    await win.loadURL(process.env.WEBPACK_DEV_SERVER_URL)
    if (!process.env.IS_TEST) win.webContents.openDevTools()
  } else {
    createProtocol('app')
    // Load the index.html when not in development
    await win.loadURL('app://./index.html')
  }
}

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  // On macOS it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

ipcMain.on('canClose', () => {
  canClose = true
})

ipcMain.on('cantClose', () => {
  canClose = false
})

app.on('activate', () => {
  // On macOS it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow().then(r => {
    })
  }
})

// This method will be called when Electron has finished
// initialization and is ready to create browser windows.
// Some APIs can only be used after this event occurs.
app.on('ready', async() => {
  if (isDevelopment && !process.env.IS_TEST) {
    // Install Vue Devtools
    try {
      await installExtension(VUEJS_DEVTOOLS)
    } catch (e) {
      console.error('Vue Devtools failed to install:', e.toString())
    }
  }
  await createWindow()
})

// Exit cleanly on request from parent process in development mode.
if (isDevelopment) {
  if (process.platform === 'win32') {
    process.on('message', (data) => {
      if (data === 'graceful-exit') {
        app.quit()
      }
    })
  } else {
    process.on('SIGTERM', () => {
      app.quit()
    })
  }
}
