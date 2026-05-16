<template>
  <div class="app-container table">
    <Tabs :list="tabs" v-model="currentIndex" @change="handleCurrentIndexChange"></Tabs>
    <div style=" height: 20px; width: 100%"></div>
    <template v-if="currentIndex === 0">
      <FenQu/>
    </template>
    <template v-else-if="currentIndex === 1">
      <ChangJing/>
    </template>
    <template v-else-if="currentIndex === 2">
      <LightsOnOrOff/>
    </template>
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
import Tabs from '@/views/components/tabs/index.vue'
import FenQu from './components/fenqu/index.vue'
import ChangJing from './components/changjing/index.vue'
import LightsOnOrOff from '@/views/zm/baseControl/components/lightsOnOrOff/index.vue'
export default {
  name: 'Control',
  components: {
    Tabs,
    FenQu,
    ChangJing,
    LightsOnOrOff
  },
  data() {
    return {
      currentIndex: 0,
      tabs: [
        '分区控制',
        '场景控制',
        '全亮/全灭'
      ]
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
