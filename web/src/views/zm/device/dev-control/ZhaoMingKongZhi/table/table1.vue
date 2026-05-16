<template>
  <div class="table">
    <div class="table_row">
      <div class="table_col">
        <div class="left">当前状态:</div>
        <div class="right">
          <div class="input">
            <div
              style="
                background-color: #2280ec;
                width: 40px;
                height: 40px;
                border-radius: 100%;
                display: flex;
                justify-content: center;
                align-items: center;
              "
            >
              <template v-if="systemSwitch">
                <i
                  class="el-icon-open"
                  style="color: #13ce66; font-size: 25px"
                ></i>
              </template>
              <template v-else>
                <i
                  class="el-icon-turn-off"
                  style="color: #ff4949; font-size: 25px"
                ></i>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="table_row">
      <div class="table_col">
        <div class="left">开关控制:</div>
        <div class="right">
          <div class="input">
            <el-button round type="success" icon="el-icon-open" @click="enabled(1)">启动</el-button>
            <el-button round type="danger" icon="el-icon-turn-off" @click="enabled(0)">停止</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { systemSwitch } from '@/api/zm/device/dev-control'

export default {
  props: {
    systemSwitch: {
      type: Number,
      default: 1
    },
    deviceId: undefined
  },
  methods: {
    enabled(type) {
      if (!this.deviceId) return this.$message.warning('请先选择设备')
      this.$confirm(type ? '确认启动吗？' : '确认停止吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        return systemSwitch({
          deviceId: this.deviceId,
          systemSwitch: type
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
        setTimeout(() => {
          this.$message.warning('请稍后刷新页面查看状态是否改变')
        }, 3000)
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.table {
  border: 1px solid #2280ec;
  // margin-top: 5px;
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
        width: 110px !important;
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
