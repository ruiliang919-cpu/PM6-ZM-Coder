<template>
  <div v-loading="loading">
    <div style="height: 10px; width: 100%"></div>

    <div v-if="form && Object.values(form).length && Object.values(form).every(item => !!item)" class="jueyuan">
      <div class="item">
        <div class="icon">
          <i class="el-icon-turn-off"></i>
        </div>
        <div class="item_title_box">母线正极绝缘电阻值:</div>
        <div class="item_content_box">
          <el-tag
            type=""
            effect="plain"
            style="width: 100px; text-align: center"
          >
            {{ form.positivePoleResistance }}
          </el-tag
          >
        </div>
        <div class="unit">KΩ</div>
      </div>
      <div class="item">
        <div class="icon">
          <i class="el-icon-turn-off"></i>
        </div>
        <div class="item_title_box">母线负极绝缘电阻值:</div>
        <div class="item_content_box">
          <el-tag
            type=""
            effect="plain"
            style="width: 100px; text-align: center"
          >
            {{ form.negativePoleResistance }}
          </el-tag
          >
        </div>
        <div class="unit">KΩ</div>
      </div>
    </div>
    <div style="border: 1px solid #2280ec;" v-else>
      <el-empty description="配电柜无此数据"></el-empty>
    </div>
    <div style="height: 25px; width: 100%"></div>
  </div>
</template>
<script>
import { getBusInsulation } from '@/api/zm/device/dev'
import polling from '@/mixins/polling'
import { subscribeCabinetData, unsubscribe, isWebSocketConnected } from '@/utils/websocket'

export default {
  mixins: [polling],
  data() {
    return {
      loading: false,
      queryParams: {
        slaveId: -1
      },
      form: {},
      wsSubId: null
    }
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
        console.log('navibarDeviceValue ========>', n)
        if (n) {
          this.$stopPolling()
          this.queryParams.slaveId = n
          this.queryParams.pageNum = 1
          this.getList()
          this.$startPolling()
          this.subscribeWebSocket(n)
        } else {
          this.$stopPolling()
          this.unsubscribeWebSocket()
          this.loading = false
        }
      },
      immediate: true
    }
  },
  beforeDestroy() {
    this.unsubscribeWebSocket()
  },
  methods: {
    getList() {
      this.loading = true
      this._get()
    },
    pollingFetch() {
      // D-3: WebSocket 连接正常时跳过轮询
      if (isWebSocketConnected() && this.wsSubId) {
        return
      }
      this._get()
    },
    _get() {
      getBusInsulation(this.queryParams)
        .then(res => {
          this.form = res.data
        })
        .catch((error) => {
          console.error('获取数据失败:', error)
        })
        .finally(() => {
          this.loading = false
        })
    },
    subscribeWebSocket(deviceNo) {
      this.unsubscribeWebSocket()
      if (!isWebSocketConnected()) return
      this.wsSubId = subscribeCabinetData(deviceNo, (payload) => {
        if (payload.data && payload.data.insulation) {
          this.form = payload.data.insulation
        }
      })
    },
    unsubscribeWebSocket() {
      if (this.wsSubId) {
        unsubscribe(this.wsSubId)
        this.wsSubId = null
      }
    }
  }
}
</script>
<style scoped lang="scss">
.jueyuan {
  //   border:1px solid #b9bcb7;
  border-radius: 5px;
  //   background-color: #fff;
  //   padding: 50px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .item {
    padding: 30px 20px;
    display: flex;
    align-items: center;
    background-color: #1599d1;
    border-radius: 5px;
    color: #fff;

    &:nth-child(2) {
      background-color: #ec9f03;
    }

    .icon {
      padding: 0 10px;
    }

    &_title_box {
      //   width: 150px;
      //   text-align: right;
      padding-right: 10px;
    }

    .unit {
      padding: 0 10px;
    }
  }
}
</style>
