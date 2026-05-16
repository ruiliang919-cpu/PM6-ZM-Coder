<script>
import Tabs from '@/views/components/tabs/index.vue'
import FenQuZuHeJiSuan from '@/views/zm/history/powerCalculation/components/FenQuZuHeJiSuan/index.vue'
import JiGuiZongHeJiSuan from '@/views/zm/history/powerCalculation/components/JiGuiZongHeJiSuan/index.vue'

export default {
  name: 'PowerCalculation',
  components: { Tabs, FenQuZuHeJiSuan, JiGuiZongHeJiSuan },
  data() {
    return {
      tabs: [
        // '机柜综合计算',
        '分区组合计算'
      ],
      currentIndex: 0
    }
  },
  created() {
    this.currentIndex = this.$route.query.currentIndex ? Number(this.$route.query.currentIndex) : 0
  },
  methods: {
    handleCurrentIndexChange(e) {
      this.$router.replace({
        path: this.$route.fullPath,
        query: {
          currentIndex: this.currentIndex
        }
      })
    }
  }
}
</script>

<template>
  <div class="app-container table">
    <Tabs :list="tabs" v-model="currentIndex" @change="handleCurrentIndexChange"></Tabs>
    <div style=" height: 20px; width: 100%"></div>
<!--    <template v-if="currentIndex === 0">-->
<!--      <JiGuiZongHeJiSuan/>-->
<!--    </template>-->
<!--    <template v-else>-->
<!--      <FenQuZuHeJiSuan/>-->
<!--    </template>-->
    <FenQuZuHeJiSuan/>
  </div>
</template>

<style scoped lang="scss">

</style>
