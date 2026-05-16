<template>
  <div class="table" v-loading="loading">
    <div class="table_row">
      <div class="table_col">
        <div class="left">机柜名称:</div>
        <div class="right">
          <div class="input">
            <el-input type="text" v-model="otherMsg.name"/>
          </div>
        </div>
      </div>
    </div>
    <div class="table_row">
      <div class="table_col">
        <div class="left">机柜区域:</div>
        <div class="right">
          <div class="input">
            <!--            <el-input type="text" v-model="otherMsg.area"/>-->
            <el-select v-model="otherMsg.zoneId" style="width: 100%" placeholder="请选择区域" filterable>
              <el-option v-for="item in regionList" :key="item.id" :label="item.name" :value="item.id"/>
            </el-select>
          </div>
        </div>
      </div>
    </div>
    <!--    <div class="table_row">-->
    <!--      <div class="table_col">-->
    <!--        <div class="left">机柜地址:</div>-->
    <!--        <div class="right">-->
    <!--          <div class="input">-->
    <!--            <el-input type="text"/>-->
    <!--          </div>-->
    <!--        </div>-->
    <!--      </div>-->
    <!--    </div>-->
    <div class="table_row">
      <div class="table_col">
        <div style="padding: 10px; text-align: center; flex: 1;">
          <el-button :disabled="!otherMsg.name || !otherMsg.zoneId" type="primary" round style="width: 100px;"
                     @click="saveData"
          >保存
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { deviceSave, otherMsg } from '@/api/zm/device/setting'
import { listBaseRegion, listBaseRegionNoPage } from '@/api/zm/baseRegion'

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
        deviceId: undefined
      },
      loading: false,
      regionList: []
    }
  },
  methods: {
    getData(deviceId) {
      this.loading = true

      otherMsg({ deviceId }).then(res => {
        this.otherMsg = res.data

      }).finally(() => {
        this.loading = false
      })
    },
    getList() {
      listBaseRegionNoPage().then(response => {
        this.regionList = response.data
      })
    },
    saveData() {
      this.$confirm('确认保存机柜信息吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.loading = true
        return deviceSave({
          deviceId: this.otherMsg.deviceId,
          zoneId: this.otherMsg.zoneId,
          deviceName: this.otherMsg.name
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
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
    this.getList()
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
