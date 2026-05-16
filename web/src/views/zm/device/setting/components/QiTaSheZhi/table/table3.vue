<template>
  <div class="table">
    <div class="table_row">
      <div class="table_col">
        <div class="left">时间设置:</div>
        <div class="right">
          <el-input class="input" type="text" :value="form.year"/>
          年
          <el-input class="input" type="text" :value="form.month"/>
          月
          <el-input class="input" type="text" :value="form.day"/>
          日
          <el-input type="text" class="input" :value="form.hour"/>
          时
          <el-input class="input" type="text" :value="form.min"/>
          分
          <el-input class="input" type="text" :value="form.second"/>
          秒
        </div>
      </div>
    </div>

    <div class="table_row">
      <div class="table_col">
        <div style="padding: 10px; text-align: center; flex: 1">
          <el-button type="primary" round style="width: 100px" @click="saveData">启动对时</el-button>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { communicateSave, otherMsg, timeSave } from '@/api/zm/device/setting'

export default {
  data() {
    return {
      form: {
        deviceId: undefined,
        year: undefined,
        month: undefined,
        day: undefined,
        hour: undefined,
        min: undefined,
        second: undefined
      },
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
      handler(n) {
        this.form.deviceId = n
      },
      immediate: true
    }
  },
  methods: {
    getLocalTime() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        // console.log(this.form);
        const date = new Date()
        this.form.year = date.getFullYear()
        this.form.month = date.getMonth() + 1
        this.form.day = date.getDate()
        this.form.hour = date.getHours()

        this.form.min = date.getMinutes()
        this.form.second = date.getSeconds()
      }, 500)
    },
    saveData() {
      this.$confirm('确认启动对时吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.loading = true
        return timeSave(this.form)
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).finally(() => {
        this.loading = false
      })
    }
  },
  mounted() {
    this.getLocalTime()
  },
  beforeDestroy() {
    console.log('页面销毁之前')

    clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
::v-deep .input {
  input {
    text-align: center;
    padding: 0 !important;
  }
}

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
