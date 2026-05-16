<template>
  <div class="app-container table">
    <Tabs :list="tabs" v-model="currentIndex" @change="handleCurrentIndexChange"></Tabs>
    <div style=" height: 20px; width: 100%"></div>
    <template v-if="currentIndex === 0">
      <HuiLuFenZu/>
    </template>
    <template v-else-if="currentIndex === 1">
      <!--      <template v-if="currentIndex === 0">-->
      <!-- #3673bb -->
      <ChangJingSheZhi/>
    </template>
    <template v-else-if="currentIndex === 2">
      <!--      <template v-else-if="currentIndex === 1">-->
      <ShiKongMoShi/>
    </template>
    <template v-else-if="currentIndex === 3">
      <!--      <template v-else-if="currentIndex === 2">-->
      <ChuanGanMoShi/>
    </template>
    <template v-else-if="currentIndex === 4">
      <!--      <template v-else-if="currentIndex === 3">-->
      <JiaoLiuKaiGuan/>
    </template>
    <template v-else>
      <QiTaSheZhi/>
    </template>
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
//回路分组
import HuiLuFenZu from './components/HuiLuFenZu/index.vue'
//场景模式
import ChangJingSheZhi from './components/ChangJingSheZhi/index.vue'
//时空模式
import ShiKongMoShi from './components/ShiKongMoShi/index.vue'
//传感模式
import ChuanGanMoShi from './components/ChuanGanMoShi/index.vue'
//交流开关
import JiaoLiuKaiGuan from './components/JiaoLiuKaiGuan/index.vue'
//其他设置
import QiTaSheZhi from './components/QiTaSheZhi/index.vue'
import Tabs from '@/views/components/tabs/index.vue'
import { getStatus, setStatus } from '@/api/zm/device/setting'

export default {
  name: 'Setting',
  components: {
    JiaoLiuKaiGuan,
    ShiKongMoShi,
    ChangJingSheZhi,
    HuiLuFenZu,
    ChuanGanMoShi,
    QiTaSheZhi,
    Tabs
  },
  inject: ['getNavibarDeviceValue'],
  computed: {
    navibarDeviceValue() {
      return this.getNavibarDeviceValue()
    }
  },
  watch: {
    navibarDeviceValue: {
      handler(n) {
        if (n) {
          this.queryParams.deviceId = n
          // if (n !== -1 && this.queryParams.groupId !== -1) this.getLoops()
          this.startRefresh()
        }
      },
      immediate: true

    }
  },
  data() {
    return {
      currentIndex: 0,
      tabs: [
        '回路分组',
        '场景设置',
        '时控模式',
        '传感器模式',
        '交流开关',
        '其他设置'
      ],
      date: [],
      queryParams: {
        deviceId: undefined
      },
      timer: null,
      timer2: null
    }
  },
  created() {
    this.currentIndex = this.$route.query.currentIndex ? Number(this.$route.query.currentIndex) : 0
    this.startTimer()
  },
  beforeDestroy() {
    if (this.timer) clearTimeout(this.timer)
    if (this.timer2) clearTimeout(this.timer2)
  },
  methods: {
    handleCurrentIndexChange(e) {
      this.$router.replace({
        path: this.$route.fullPath,
        query: {
          currentIndex: this.currentIndex
        }
      })
    },
    startRefresh() {
      if (localStorage.getItem('isRefresh') && JSON.parse(localStorage.getItem('isRefresh'))) {
        setStatus(this.queryParams).then(res => {
          this.$message.success(res.msg || '操作成功')
        }).finally(() => {
          localStorage.setItem('isRefresh', JSON.stringify(false))
        })
      }
    },
    startTimer() {
      if (this.timer) clearTimeout(this.timer)
      if (this.queryParams.deviceId) {

        this.timer = setTimeout(() => {
          getStatus(this.queryParams).then(res => {
            if (res.data) {
              this.$confirm('页面有变动，是否刷新？(点击取消5秒后重新弹窗。)', {
                title: '警告',
                type: 'warning'
              }).then(res => {
                this.$router.go(0)
                localStorage.setItem('isRefresh', JSON.stringify(true))
              }).catch(e => {
                this.startTimer()
              })
            } else {
              this.startTimer()
            }
          }).catch(e => {
            this.startTimer()
          })
        }, 15000)
      } else {
        if (this.timer2) clearTimeout(this.timer2)
        this.timer2 = setTimeout(() => {
          this.startTimer()
        }, 15000)
      }
    }

  }
}
</script>
<style lang="scss" scoped>
.total_use {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  & > div {
    padding: 20px 50px;
  }

  & > div:nth-child(1) {
    background-color: #3673bb;
    color: #fff;
  }

  & > div:nth-child(2) {
    vertical-align: middle;
    border: 1px solid #2280ec;
  }
}
</style>
