<template>
  <div class="checkbox">
    <el-checkbox @change="handleChange" v-model="checked">
      <span style="color: #fff;font-size: 18px">全选</span>
    </el-checkbox>
  </div>
</template>
<script>
export default {
  inject: ['getNavibarDeviceValue'],
  computed: {
    navibarDeviceValue() {
      return this.getNavibarDeviceValue()
    }
  },
  watch: {
    navibarDeviceValue: {
      handler(n) {
        this.checked = false
        this.$emit('change', false)
      },
      immediate: true
    }
  },
  data() {
    return {
      checked: false
    }
  },
  methods: {
    handleChange(e) {
      this.checked = e
      this.$emit('change', e)
    }
  }
}
</script>
<style scoped lang="scss">
.checkbox {
  background-color: #3673bb;
  padding: 15px 10px;
  margin-top: 10px;
}
</style>
