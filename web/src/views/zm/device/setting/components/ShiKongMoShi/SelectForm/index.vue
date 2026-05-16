<script>
import { simpleListSelect, sceneListSelect, getSimpleEnabled, getSceneEnabled } from '@/api/zm/device/setting'
import { enable } from 'core-js/internals/internal-metadata'

export default {
  props: {
    type: Number,
    default: 0
  },
  data() {
    return {
      loading: false,
      queryParams: {
        deviceId: undefined
      },
      demoList: [],
      form: {
        selectValue: undefined
      },
      enabled: false,
      tempEnabled: false
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
        if (n) {
          this.queryParams.deviceId = n
          if (n !== -1) this.getData()
        }
      },
      immediate: true
    }
  },
  methods: {
    getData() {
      this.form.selectValue = undefined
      this.loading = true
      if (this.type === 0) {
        simpleListSelect(this.queryParams).then((response) => {
          this.demoList = response.data || []
          if (this.demoList.length) {
            this.form.selectValue = this.demoList[0].id
            this.handleChange(this.queryParams.deviceId, this.form.selectValue)
            this.getEnabled()
          } else {
            this.handleChange(undefined, undefined)
          }

        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 1) {
        sceneListSelect(this.queryParams).then((response) => {
          this.demoList = response.data

          if (this.demoList.length) {
            this.form.selectValue = this.demoList[0].id
            this.handleChange(this.queryParams.deviceId, this.form.selectValue, this.enabled)
            this.getEnabled()
          } else {
            this.handleChange(undefined, undefined, undefined)
          }
        }).finally(() => {
          this.loading = false
        })
      }
    },
    handleChange(deviceId, controlId) {
      this.$emit('change', deviceId, controlId)
      this.getEnabled()
    },
    getEnabled() {
      if (this.type === 0) {
        getSimpleEnabled({
          deviceId: this.queryParams.deviceId,
          controlId: this.form.selectValue
        }).then(response => {
          console.log(response)
          this.enabled = response.data.enabled
          this.tempEnabled = response.data.enabled
        })
      } else if (this.type === 1) {
        getSceneEnabled({
          deviceId: this.queryParams.deviceId,
          controlId: this.form.selectValue
        }).then(response => {
          this.enabled = response.data.enabled
          this.tempEnabled = response.data.enabled
        })
      }
    },
    getEnabled2(deviceId, controlId) {
      if (this.type === 0) {
        getSimpleEnabled({
          deviceId,
          controlId
        }).then(response => {
          console.log(response)
          this.enabled = response.data.enabled
          this.tempEnabled = response.data.enabled
        })
      } else if (this.type === 1) {
        getSceneEnabled({
          deviceId,
          controlId
        }).then(response => {
          this.enabled = response.data.enabled
          this.tempEnabled = response.data.enabled
        })
      }
    },
    handleSave() {
      console.log(this.enabled)
      this.tempEnabled = this.enabled
      this.$emit('save', this.queryParams.deviceId, this.form.selectValue, this.enabled)
    },
    handleOneKey() {
      this.$emit('oneKeySetting', this.queryParams.deviceId, this.form.selectValue, this.enabled)
    },
    handleEnable(e) {
      //console.log(e, this.tempEnabled)
      if (!this.tempEnabled) this.enabled = e
    }
  }
}
</script>

<template>
  <el-form :inline="true" label-width="68px" v-loading="loading">
    <el-form-item>
      <el-select v-model="form.selectValue" @change="handleChange(queryParams.deviceId,form.selectValue)">
        <el-option v-for="item in demoList" :key="item.id" :value="item.id" :label="item.name"/>
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-checkbox label="启用" :value="enabled" @change="handleEnable">
        <!--        {{enabled}}-->
      </el-checkbox>
    </el-form-item>
    <el-form-item>
      <el-button @click="handleSave" type="primary" round
                 style="width: 100px"
      >保存
      </el-button>
      <el-button @click="handleOneKey" type="primary" round
                 style="width: 100px"
      >一键设置
      </el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped lang="scss">

</style>
