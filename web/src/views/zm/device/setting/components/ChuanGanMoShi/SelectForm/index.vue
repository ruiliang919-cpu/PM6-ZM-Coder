<script>
import { illuminanceSelectList, infraredSelectList, sceneListSelect } from '@/api/zm/device/setting'

export default {
  props: {
    type: {
      type: Number,
      default: 0
    },
    enabled: {
      type: Object,
      default: false
    }
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
      localEnabled: false
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
    },
    enabled(n) {
      this.localEnabled = n.enabled
    }
  },
  methods: {
    getData() {
      this.form.selectValue = undefined
      this.loading = true
      if (this.type === 0) {
        infraredSelectList(this.queryParams).then((response) => {
          this.demoList = response.data || []
          if (this.demoList.length) {
            this.form.selectValue = this.demoList[0].id || this.demoList[0].sensorId
            this.handleChange(this.queryParams.deviceId, this.form.selectValue)
          } else {
            this.handleChange(undefined, undefined)
          }

        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 1) {
        illuminanceSelectList(this.queryParams).then((response) => {
          this.demoList = response.data || []

          if (this.demoList.length) {
            this.form.selectValue = this.demoList[0].id || this.demoList[0].sensorId
            this.handleChange(this.queryParams.deviceId, this.form.selectValue)
          } else {
            this.handleChange(undefined, undefined)
          }
        }).finally(() => {
          this.loading = false
        })
      }
    },
    handleChange(v1, v2) {
      this.$emit('change', v1, v2)
    },
    save() {
      this.$emit('save', this.queryParams.deviceId, this.form.selectValue, this.localEnabled)
    }
  }
}
</script>

<template>
  <el-form :inline="true" label-width="68px" v-loading="loading">
    <el-form-item>
      <el-select v-model="form.selectValue" @change="handleChange(queryParams.deviceId,$event)">
        <el-option v-for="item in demoList"
                   :key="item.id || item.sensorId"
                   :value="item.id || item.sensorId"
                   :label="item.name || item.sensorName"
        />
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-checkbox label="启用" :value="localEnabled" @change="localEnabled = $event">
<!--        {{ localEnabled }}-->
      </el-checkbox>
    </el-form-item>
    <el-form-item>
      <el-button @click="save" type="primary" round style="width: 100px"
      >保存
      </el-button
      >
    </el-form-item>
  </el-form>
</template>

<style scoped lang="scss">

</style>
