<template>
  <CommonContainer
    title="开关控制"
    :contentPadding="0"
    :borderWidth="1"
    style="flex: 1; overflow: hidden;margin-top: 10px"
    v-loading="loading"
  >
    <div class="btns">
      <el-button round type="success" icon="el-icon-open" @click="handleClick(1)">开</el-button>
      <el-button round type="danger" icon="el-icon-turn-off" @click="handleClick(0)">关</el-button>
    </div>
  </CommonContainer>
</template>
<script>
import CommonContainer from '@/components/CommonContainer/index.vue'

export default {
  components: { CommonContainer },
  data() {
    return {
      loading: false
    }
  },
  methods: {
    handleClick(type) {
      this.$emit('change', type)
    },
    setLoading(flag) {
      this.loading = flag
    }
  }
}
</script>
<style scoped lang="scss">
.btns {
  padding: 20px 0;
  display: flex;
  justify-content: space-evenly;
}
</style>
