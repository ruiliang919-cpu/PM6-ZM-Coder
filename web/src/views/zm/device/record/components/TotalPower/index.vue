<script>
import { getOneTotalElectricByDeviceId, getDeviceType, getOneTotalPower } from '@/api/zm/device/record'

export default {
  data() {
    return {
      power: '0.00',
      loss: '0.00',
      loading: false,
      queryParams: {
        deviceId: undefined
      },
      type: 1,
      timer: null
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
      handler(val) {
        console.log('navibarDeviceValue ========>', val)
        this.queryParams.deviceId = val
        if(this.queryParams.deviceId){
          this.getList()
          this.startTimer()
        }
      },
      immediate: true
    }
  },
  methods: {
    getList() {
      this.loading = true
      this._get()
    },
    _get() {
      getDeviceType(this.queryParams).then(response => {
        //类型的编号 电源柜为1 显示总电量 配电柜为 0
        this.type = response.data.typeId
        if (response.data.typeId === 1) {
          return getOneTotalElectricByDeviceId(this.queryParams)
        } else {
          return Promise.resolve(response)
        }
      }).then(response => {
        if (this.type === 1) {
          this.power = response.data.power
        }
        return getOneTotalPower(this.queryParams)
      }).then(response => {
        this.loss = response.data.loss
      }).finally(() => {
        this.loading = false
      })
    },
    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this._get()
      }, 5000)
    }
  },
  beforeDestroy() {
    clearInterval(this.timer)
  }
}
</script>

<template>
  <div class="c">
    <div class="total_use" style="margin-right: 20px" v-if="type === 1">
      <div>总耗电量:</div>
      <div v-loading="loading">
        <div>{{ power === null ? 'N/A' :power }} kWh</div>
      </div>
    </div>
    <div class="total_use">
      <div>总功率:</div>
      <div v-loading="loading">
        <div>{{ loss === null ? 'N/A' : loss }} kW</div>
      </div>
    </div>
  </div>

</template>

<style scoped lang="scss">
.c {
  display: flex;
  flex-wrap: wrap;
}

.total_use {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  & > div {
    padding: 20px 50px;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  & > div:nth-child(1) {
    background-color: #3673bb;
    color: #fff;
    text-align: center;
    width: 180px;
  }

  & > div:nth-child(2) {
    vertical-align: middle;
    border: 1px solid #2280ec;
  }
}
</style>
