<template>
  <div v-loading="loading">
    <template v-if="data">
      <div class="table">
        <div class="table_row">
          <div class="table_col">
            <div class="left">感应亮度:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.inductiveLux"/>
              </div>
              <div class="switch">
                <el-switch inactive-text="开关" :value="!!data.inductiveSwitchStatus"
                           @change="$event?data.inductiveSwitchStatus = 1 : data.inductiveSwitchStatus = 0"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">非感应亮度:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.uninductionLux"/>
              </div>
              <div class="switch">
                <el-switch inactive-text="开关" :value="!data.uninductionSwitchStatus"
                           @change="$event?data.uninductionSwitchStatus = 0:data.uninductionSwitchStatus=1"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="table_row">
          <div class="table_col">
            <div class="left">感应延时:</div>
            <div class="right">
              <div class="input">
                <el-input type="number" v-model="data.delayedTime"/>
              </div>
              <div class="switch">
                秒
                <!--            <el-switch inactive-text="开关"/>-->
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
import { infraredParams } from '@/api/zm/device/setting'

export default {
  data() {
    return {
      loading: false,
      data: null,
      presetData: {
        'enabled': 0,
        'inductiveLux': 0,
        'inductiveSwitchStatus': 0,
        'uninductionLux': 0,
        'uninductionSwitchStatus': 0,
        'delayedTime': 0
      }
    }
  },
  methods: {
    getData(deviceId = undefined, sensorId = undefined) {
      this.loading = true
      infraredParams({
        deviceId,
        sensorId
      }).then((res) => {
        console.log(res.data)
        this.data = res.data
        this.$emit('enabled', res.data.enabled)
      }).catch(e => {
        console.log(e)
        this.data = null
      }).finally(() => {
        this.loading = false
      })
    },
    clearData() {
      this.data = null
    }
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
        width: 140px !important;
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

  .input {
    ::v-deep input {
      text-align: center;
      padding: 0;
    }
  }
}
</style>
