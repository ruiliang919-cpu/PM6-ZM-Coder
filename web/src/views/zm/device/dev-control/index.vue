<template>
  <div class="app-container table">
    <Tabs :list="tabs" v-model="currentIndex" @change="handleCurrentIndexChange"></Tabs>
    <div style="height: 20px; width: 100%"></div>
    <template v-if="currentIndex === 0">
      <ZhaoMingSheZhi/>
    </template>
    <template v-else-if="currentIndex === 1">
      <!-- #3673bb -->
      <MoKuaiKongZhi/>
    </template>
    <template v-else-if="currentIndex === 2">
      <QiTaKaiGuanKongZhi/>
    </template>
    <template v-else>
      <JiaoLiuKaiGuanKongZhi/>
    </template>
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>

//照明设置
import ZhaoMingSheZhi from './ZhaoMingKongZhi/index.vue'
import MoKuaiKongZhi from '@/views/zm/device/dev-control/MoKuaiKongZhi/index.vue'
import Tabs from '@/views/components/tabs/index.vue'
import CommonContainer from '@/components/CommonContainer/index.vue'
import JiaoLiuKaiGuanKongZhi from '@/views/zm/device/dev-control/JiaoLiuKaiGuanKongZhi/index.vue'
import QiTaKaiGuanKongZhi from '@/views/zm/device/dev-control/QiTaKaiGuanKongZhi/index.vue'

export default {
  name: 'Dev-control',
  components: {
    CommonContainer,
    ZhaoMingSheZhi,
    MoKuaiKongZhi,
    Tabs,
    JiaoLiuKaiGuanKongZhi,
    QiTaKaiGuanKongZhi
  },
  data() {
    return {
      currentIndex: 0,
      tabs: ['照明控制', '模块控制', '其他开关控制', '交流开关控制'],
      date: []
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
<style lang="scss" scoped>
.total_use {
  display: flex;
  align-items: center;
  margin-bottom: 20px;

  & > div {
    padding: 20px 50px;
  }

  & > div:nth-child(1) {
    background-color: #3673bb;
    color: #fff;
  }

  & > div:nth-child(2) {
    vertical-align: middle;
    border: 1px solid #2280ec;
  }
}
</style>
