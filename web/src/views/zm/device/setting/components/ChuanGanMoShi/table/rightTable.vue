<template>
  <div v-loading="loading">
    <template v-if="data">
      <div class="table">
        <div class="table_row">
          <div class="table_col">
            <div class="left">当前照度值:</div>
            <div class="right" v-if="!!data.outControlStatus">
              <div class="input">
                <el-input type="number" v-model="data.illuminanceLux" :disabled="!data.outControlStatus"/>
              </div>
            </div>
            <div class="right" v-else>
              <div class="input">
                <el-input type="number" v-model="out"/>
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">恒照值:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.constantIlluminanceLux"/>
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">回差值:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.hysteresisLux"/>
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">调节时间:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.adjustmentTimeSeconds"/>
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">启用外控:</div>
            <div class="right">
              <div class="input">
                <el-checkbox :value="!data.outControlStatus"/>
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">外控通道:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.outControlChannel" :disabled="!!data.outControlStatus"/>
              </div>
            </div>
          </div>
        </div>
        <div class=" table_row
                "
        >
          <div class="table_col">
            <div class="left">外控地址:</div>
            <div class="right">
              <div class="input">
                <el-input type="text" v-model="data.outControlAddr" :disabled="!!data.outControlStatus"/>
              </div>
            </div>
          </div>
        </div>

      </div>
    </template>
    <template v-else>
      <div class="table">
        <el-empty description="无数据"></el-empty>
      </div>

    </template>
  </div>
</template>
<script>
import { getAddress, getIllBaseLux, getIllLux, illuminanceParams, illuminanceEnabled } from '@/api/zm/device/setting'

export default {
  data() {
    return {
      loading: false,
      data: null,
      presetData: {
        // 传感器编号
        sensorId: 1,
        // 启用 0-未启用/1-启用
        enabled: 0,
        // 恒照值 单位Lux
        constantIlluminanceLux: 0,
        // 回差值 单位Lux
        hysteresisLux: 0,
        // 调节时间 单位S
        adjustmentTimeSeconds: 0,
        // 外控使能 0-不启用外控/1-启用外控 05 功能码（外部控制大概是这个）
        externalControlEnabled: 0,
        // 外控状态 0未启用；1启用  01 功能码（暂时应该不需要使用）
        outControlStatus: 0,
        // 外控通道
        outControlChannel: '0.00',
        // 外控地址
        outControlAddr: '0.00'
      },
      out: 0,
      timer: null,
      isSettingOutControl: false,
      deviceId: undefined,
      sensorId: undefined
    }
  },
  methods: {
    getData(deviceId = undefined, sensorId = undefined) {
      this.loading = true
      illuminanceParams({
        deviceId,
        sensorId
      }).then((res) => {
        console.log(res.data)
        this.data = res.data
        this.$emit('enabled', res.data.enabled)
        if (!!this.data.outControlStatus) {
          return getIllBaseLux({
            deviceId: deviceId,
            moduleId: sensorId
          })
        } else {
          return getIllLux({
            moduleId: res.data.outChannel,
            deviceId: res.data.outAddress,
            sourceId: deviceId
          })
        }
      }).then(res => {
        console.log(res)
        if (!!this.data.outControlStatus) {
          this.data.illuminanceLux = res.data
        } else {
          this.out = res.data
        }
      }).catch(e => {
        console.log(e)
        this.data = null
      }).finally(() => {
        this.loading = false
        this.startTimer(deviceId, sensorId)
      })
    },
    clearData() {
      this.data = null
    },
    startTimer(deviceId = undefined, sensorId = undefined) {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        getAddress({
          deviceId,
          sensorId
        }).then((res) => {
          this.data.outControlChannel = res.data.outChannel
          this.data.outControlAddr = res.data.outAddress

          if (!!this.data.outControlStatus) {
            return getIllBaseLux({
              deviceId: deviceId,
              moduleId: sensorId
            })
          } else {
            return getIllLux({
              moduleId: res.data.outChannel,
              deviceId: res.data.outAddress,
              sourceId: deviceId
            })
          }
        }).then(res => {
          if (!!this.data.outControlStatus) {
            this.data.illuminanceLux = res.data
          } else {
            this.out = res.data
          }

          this.getOutControlStatus(deviceId, sensorId)
        }).catch(e => {
          console.error('轮询数据获取失败:', e)
        }).finally(() => {
        })
      }, 5000)
    },
    async getOutControlStatus(deviceId, sensorId) {
      // e?data.outControlStatus=0:data.outControlStatus = 1
      //this.isSettingOutControl = true
      const data = await illuminanceEnabled({
        deviceId: deviceId,
        sensorId: sensorId
      })

      this.data.outControlStatus = data.data
      //console.log('response ========> ', data)
    }
  },
  beforeDestroy() {
    clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
.table {
  border: 1px solid #2280ec;
  margin-top: 10px;

  .table_row {
    display: flex;

    .table_col {
      flex: 1;
      overflow: hidden;
      display: flex;

      .left,
      .right {
        // flex: 1;
        overflow: hidden;
        padding: 10px;
        border-bottom: 1px solid #2280ec;
        border-right: 1px solid #2280ec;
        display: flex;
        align-items: center;

        & > div {
          flex: 1;
          padding: 0 5px;
        }
      }

      .right {
        flex: 1;
      }

      & .right:last-child {
        border-right: 0px solid #2280ec;
      }

      .left {
        background-color: #3673bb;
        color: #fff;
        width: 120px !important;
        justify-content: flex-end;
      }
    }

    &:last-child .table_col {
      .left,
      .right {
        border-bottom: 0px solid #2280ec;
      }
    }
  }
}
</style>
