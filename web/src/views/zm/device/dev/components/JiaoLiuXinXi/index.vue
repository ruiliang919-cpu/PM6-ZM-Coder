<template>
  <div v-loading="loading">
    <div v-if="!form || !form.length || form.length < 2" style="border: 1px solid #2280ec;">
      <el-empty description="配电柜无此数据"></el-empty>
    </div>
    <el-row :gutter="20" v-else>
      <el-col :span="12">
        <CommonContainer :title="`一路(${form[0].workStatus})`" :contentPadding="0" :borderWidth="1">
          <Table :data="form[0]" :type="1"></Table>
        </CommonContainer>
      </el-col>
      <el-col :span="12" v-show="show2">
        <CommonContainer :title="`二路(${form[1].workStatus})`" :contentPadding="0" :borderWidth="1">
          <Table :data="form[1]" :type="2"></Table>
        </CommonContainer>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import { getAlternating } from '@/api/zm/device/dev'
import CommonContainer from '@/components/CommonContainer/index.vue'
import Table from './table_left.vue'
import Table1 from './table_right.vue'
import polling from '@/mixins/polling'
import { subscribeCabinetData, unsubscribe, isWebSocketConnected } from '@/utils/websocket'

export default {
  components: {
    CommonContainer,
    Table,
    Table1
  },
  mixins: [polling],
  data() {
    return {
      loading: true,
      queryParams: {
        slaveId: -1
      },
      form: null,
      wsSubId: null
    }
  },
  inject: ['getNavibarDeviceValue'],
  computed: {
    navibarDeviceValue() {
      return this.getNavibarDeviceValue()
    },
    show2() {

      const v = this.form && Array.isArray(this.form) && Object.keys(this.form[1]).some(key => {
        //console.log(key)
        if (key === 'id') return false
        if (key === 'circuitNum') return false
        if (key === 'deviceId') return false
        if (key === 'workStatus') return false
        return this.form[1][key] !== '-1'
      })
      //console.log(v)
      return v
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
      if (isWebSocketConnected() && this.wsSubId) {
        return
      }
      this._get()
    },
    _get() {
      getAlternating(this.queryParams).then(res => {
        this.form = res.data
      }).finally(() => {
        this.loading = false
      })
    },
    subscribeWebSocket(deviceNo) {
      this.unsubscribeWebSocket()
      if (!isWebSocketConnected()) return
      this.wsSubId = subscribeCabinetData(deviceNo, (payload) => {
        if (payload.data && payload.data.alternating) {
          this.form = payload.data.alternating
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
