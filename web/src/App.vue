<template>
  <!--  <FollowMouse>-->
  <div id="app">
    <router-view class="router-view"/>
    <theme-picker/>
  </div>
  <!--  </FollowMouse>-->
</template>

<script>
import FollowMouse from '@/views/components/followMouse/index.vue'
import ThemePicker from '@/components/ThemePicker'
import { isInElectron } from '@/utils/ruoyi'
export default {
  name: 'App',
  components: { ThemePicker, FollowMouse },
  metaInfo() {
    return {
      title:
        this.$store.state.settings.dynamicTitle &&
        this.$store.state.settings.title,
      titleTemplate: (title) => {
        return title
          ? `${title} - ${process.env.VUE_APP_TITLE}`
          : process.env.VUE_APP_TITLE
      }
    }
  },
  data() {
    return {
      navibarDeviceValue: '',
      navibarDeviceType: '',
      waitingLogout: false
    }
  },
  provide() {
    const _this = this
    return {
      getNavibarDeviceValue() {
        return _this.navibarDeviceValue
      },
      getNavibarDeviceType() {
        return _this.navibarDeviceType
      }
    }
  },
  methods: {
    setNavibarDeviceValue(v) {
      this.navibarDeviceValue = v
    },
    handleOpenContextMenu() {
      window.openMenu()
    },
    async handleLogOut() {
      if(this.waitingLogout) return
      this.waitingLogout = true
      this.$confirm('退出或者最小化程序前，需要退出登录，继续操作吗？', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          this.$router.replace('/login')
          this.waitingLogout = false
        })
      }).catch(() => {
        this.waitingLogout = false
        this.$message({
          message: '退出登录时出错了'
        })
      })
    }
  },
  mounted() {
    if (isInElectron()) {
      console.log('//////////////////////////////////')
      window.addEventListener('contextmenu', this.handleOpenContextMenu, false)
    }

    window.ipcRenderer.on('LogOut',this.handleLogOut)
  },
  beforeDestroy() {
    if (isInElectron()) {
      window.removeEventListener('contextmenu', this.handleOpenContextMenu, false)
    }
    window.ipcRenderer.off('LogOut',this.handleLogOut)
  }
}
</script>
<style scoped>
#app {
  /* background-color: #c6d1db; */
  display: flex;
  flex-direction: column;

  .router-view {
    flex: 1;
    overflow: hidden;
    overflow-y: auto;
  }
}

#app .theme-picker {
  display: none;
}
</style>
<style lang="scss">
.el-table {
  border: 1px solid #2280ec !important;

  .el-table__header-wrapper {
    background-color: #2280ec !important;
  }

}

.el-table thead tr:first-child th {
  border-bottom: 0 solid #2280ec !important;
}

.el-table__row:first-child td {
  border-top: 1px solid #2280ec !important;
}

.custom-cell-class-name {
  background-color: #e7f3ff !important;
}

.custom-border-color {
  border-bottom: 1px solid #2280ec !important;
  border-right: 1px solid #2280ec !important;

  &:last-child {
    border-right: 0 !important;
  }
}
</style>
