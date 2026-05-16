<template>
  <div class="table">
    <div class="table_row">
      <div class="table_col">
        <div class="left">当前电压:</div>
        <div class="right">
          <div class="input">
            {{ dcDcVoltage || 0 }}V
          </div>
          <div>
            <el-button round type="primary" @click="openForm">更改设置</el-button>
          </div>
        </div>
      </div>
    </div>
    <el-dialog title="设置DC/DC输出电压" :visible.sync="openSetValue" width="600px" append-to-body>
      <el-form ref="form" :rules="formRules" :model="form">
        <el-form-item prop="value">
          <el-input type="number" v-model="form.value" placeholder="请输入DC/DC输出电压" @keydown.enter.native="submitForm"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="formOptions.loading" round type="primary" @click="submitForm" >确 定</el-button>
        <el-button @click="openSetValue = false" round>取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { updateAcDc, updateDcDc } from '@/api/zm/device/dev-control'

export default {
  props: {
    dcDcVoltage: '',
    deviceId: undefined
  },
  data() {
    return {
      openSetValue: false,
      formRules: {
        value: [
          {
            required: true,
            message: '值不能为空！',
            trigger: 'blur'
          }
        ]
      },
      form: {
        deviceId: undefined,
        value: undefined
      },
      formOptions: {
        loading: false
      }
    }
  },
  methods: {
    openForm() {
      this.form.deviceId = this.deviceId
      this.form.value = this.acDcVoltage
      this.openSetValue = true
    },
    submitForm() {
      if (!this.form.deviceId) return this.$message.warning('请先选择设备，再执行此操作！')
      console.log(this.form)
      this.$refs.form.validate(valid => {
        if (valid) {
          this.formOptions.loading = true
          updateDcDc(this.form).then(res => {
            this.$message.success(res.msg || '操作成功！')
            this.openSetValue = false
            this.$emit("update", this.form.value)
          }).finally(() => {
            this.formOptions.loading = false
          })
        } else {
          console.warn('检验不通过')
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.table {
  border: 1px solid #2280ec;
  margin-top: 5px;

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

