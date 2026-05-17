<template>
  <div>
    <Lights ref="lights" :loop-control="loopControl"/>
    <SelectAllCheckBox ref="selectAllCheckBox" @change="handleChange"/>
    <LightBrightness @change="handleLightBrightnessChange" ref="lightBrightness" :current-brightness="loopLux"/>
    <SwitchButtons @change="handleSwitchChange" ref="switchButton"/>
  </div>
</template>
<script>
import Lights from '@/views/zm/device/dev-control/ZhaoMingKongZhi/lights/index.vue'
import SelectAllCheckBox from '@/views/zm/device/dev-control/ZhaoMingKongZhi/selectAllCheckBox/index.vue'
import LightBrightness from '@/views/zm/device/dev-control/ZhaoMingKongZhi/lightBrightness/index.vue'
import SwitchButtons from '@/views/zm/device/dev-control/ZhaoMingKongZhi/switchButtons/index.vue'
import { loopControlLux, loopControlSwitch } from '@/api/zm/device/dev-control'

export default {
  props: {
    loopControl: {
      type: Array,
      default() {
        return []
      }
    },
    loopLux: {
      type: Number,
      default: 0
    },
    deviceId: undefined
  },
  data() {
    return {
      isRequesting: false
    }
  },
  components: {
    Lights,
    SelectAllCheckBox,
    LightBrightness,
    SwitchButtons
  },
  methods: {
    handleChange(e) {
      if (e) {
        this.$refs.lights.selectAll()
      } else {
        this.$refs.lights.clearAll()
      }
    },
    handleLightBrightnessChange(index) {
      if (!this.$refs.lights.checkedData.length) return this.$message.warning('请先选择一条回路')
      if (this.isRequesting) return

      const brightness = (index + 1) * 10

      this.$confirm('确认设置' + brightness + '%的亮度吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.isRequesting = true
        this.$refs.lightBrightness.setLoading(true)
        return loopControlLux({
          deviceId: this.deviceId,
          lux: brightness,
          loopControl: this.$refs.lights.checkedData
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
        this.$emit('success')
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }).finally(() => {
        this.isRequesting = false
        this.$refs.lightBrightness.setLoading(false)
      })
    },
    handleSwitchChange(type) {
      if (this.isRequesting) return

      this.$confirm(type ? '确认启动吗？' : '确认停止吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.isRequesting = true
        this.$refs.lightBrightness.setLoading(true)
        return loopControlSwitch({
          deviceId: this.deviceId,
          switchStatus: type,
          loopControl: this.$refs.lights.checkedData
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).catch(error => {
        if (error !== 'cancel') {
          this.$message.error('操作失败')
        }
      }).finally(() => {
        this.isRequesting = false
        this.$refs.lightBrightness.setLoading(false)
      })
    }
  }
}
</script>
