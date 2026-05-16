<template>
  <div class="table">
    <div class="table_row">
      <div class="table_col">
        <div class="left">ip地址:</div>
        <div class="right">
          <div class="input">
            <el-input type="text" v-model="otherMsg.ip"/>
          </div>
        </div>
      </div>
    </div>
<!--    <div class="table_row">-->
<!--      <div class="table_col">-->
<!--        <div class="left">主机号:</div>-->
<!--        <div class="right">-->
<!--          <div class="input">-->
<!--            <el-input type="number" v-model="otherMsg.deviceId"/>-->
<!--          </div>-->
<!--        </div>-->
<!--      </div>-->
<!--    </div>-->
    <div class="table_row">
      <div class="table_col">
        <div style="padding: 10px; text-align: center; flex: 1;">
          <el-button :disabled="!otherMsg.ip || !otherMsg.deviceId" type="primary" round style="width: 100px;"
                     @click="saveData"
          >保存
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { communicateSave, deviceSave, otherMsg } from '@/api/zm/device/setting'

export default {
  data() {
    return {
      otherMsg: {
        // 机柜名称
        name: '',
        // 机柜区域
        area: '',
        // IP地址
        ip: '',
        // 主机号
        deviceId: undefined,
        oldDeviceId: undefined
      },
      loading: false
    }
  },
  methods: {
    getData(deviceId) {
      this.loading = true
      otherMsg({ deviceId }).then(res => {
        this.otherMsg = res.data
        this.otherMsg.oldDeviceId = this.otherMsg.deviceId
      }).finally(() => {
        this.loading = false
      })
    },
    saveData() {
      this.$confirm('确认保存通讯参数吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.loading = true
        return communicateSave({
          deviceNo: Number(this.otherMsg.oldDeviceId),
          ip: this.otherMsg.ip,
          deviceId: Number(this.otherMsg.deviceId)
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).catch(e=>{
        // this.$message.error(e)
      }).finally(() => {
        this.loading = false
      })
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
        if (n) this.getData(n)
      },
      immediate: true
    }
  },
  created() {
    // this.getData()
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
