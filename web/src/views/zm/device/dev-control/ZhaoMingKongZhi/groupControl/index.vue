<template>
  <div>
    <Group :group-control="groupControl" ref="group"/>
    <!--    {{ groupControl }}-->
    <SelectAllCheckBox @change="handleChange"/>
    <LightBrightness :current-brightness="groupLux" @change="handleLightBrightnessChange" ref="lightBrightness"/>
    <SwitchButtons @change="handleSwitchChange" ref="switchButton"/>
  </div>
</template>
<script>
// import Lights from '@/views/zm/device/dev-control/ZhaoMingKongZhi/lights/index.vue'
import Group from '@/views/zm/device/dev-control/ZhaoMingKongZhi/group/index.vue'
import SelectAllCheckBox from '@/views/zm/device/dev-control/ZhaoMingKongZhi/selectAllCheckBox/index.vue'
import LightBrightness from '@/views/zm/device/dev-control/ZhaoMingKongZhi/lightBrightness/index.vue'
import SwitchButtons from '@/views/zm/device/dev-control/ZhaoMingKongZhi/switchButtons/index.vue'
import { groupControlLux, groupControlSwitch, loopControlLux, loopControlSwitch } from '@/api/zm/device/dev-control'

export default {
  props: {
    groupControl: {
      type: Array,
      default() {
        return []
      }
    },
    groupLux: {
      type: Number,
      default: 0
    },
    deviceId: undefined
  },
  components: {
    // Lights,
    Group,
    SelectAllCheckBox,
    LightBrightness,
    SwitchButtons
  },
  methods: {
    handleChange(checked) {
      if (checked) {
        this.$refs.group.selectAll()
      } else {
        this.$refs.group.clearAll()
      }
    },
    handleLightBrightnessChange(index) {
      if(!this.$refs.group.checkedData.length) return this.$message.warning("请先选择一个分组")
      const brightness = (index + 1) * 10

      this.$confirm('确认设置' + brightness + '%的亮度吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.$refs.lightBrightness.setLoading(true)
        return groupControlLux({
          deviceId: this.deviceId,
          lux: brightness,
          groupSelectArr: this.$refs.group.checkedData
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).finally(() => {
        this.$refs.lightBrightness.setLoading(false)
      })
    },
    handleSwitchChange(type) {
      this.$confirm(type ? '确认启动吗？' : '确认停止吗？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        this.$refs.lightBrightness.setLoading(true)
        return groupControlSwitch({
          deviceId: this.deviceId,
          switchStatus: type,
          groupSelectArr: this.$refs.group.checkedData
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).finally(() => {
        this.$refs.lightBrightness.setLoading(false)
      })
    }
  }
}
</script>
