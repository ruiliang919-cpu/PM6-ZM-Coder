<template>
  <div class="app-container table">
    <Tabs :list="tabs" v-model="currentIndex" @change="handleCurrentIndexChange"></Tabs>
    <div style=" height: 20px; width: 100%"></div>
    <template v-if="currentIndex == 0">
      <el-row>
        <el-col class="JiaoLiuHuiLu">
          <CommonContainer
            title="交流回路信息"
            :contentPadding="0"
            :borderWidth="0"
          >
            <JiaoLiuHuiLu/>
          </CommonContainer>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <CommonContainer
            title="直流回路信息"
            :contentPadding="0"
            :borderWidth="0"
          >
            <ZhiLiuHuiLu/>
          </CommonContainer>
        </el-col>
      </el-row>
    </template>
    <template v-if="currentIndex == 1">
      <el-row>
        <el-col :span="24">
          <CommonContainer
            title="母线信息"
            :contentPadding="0"
            :borderWidth="0"
          >
            <MuXian/>
          </CommonContainer>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <DC/>
        </el-col>
      </el-row>
    </template>
    <template v-if="currentIndex == 2">
      <el-row>
        <el-col :span="24">
          <CommonContainer
            title="母线绝缘 "
            :contentPadding="0"
            :borderWidth="0"
          >
            <JueYuan/>
          </CommonContainer>
        </el-col>
      </el-row>
    </template>
    <template v-if="currentIndex == 3">
      <JiaoLiuXinXi/>
    </template>
    <template v-if="currentIndex == 4">
      <CommonContainer
        title="传感器信息"
        :contentPadding="0"
        :borderWidth="0"
      >
        <ChuanGanQiXinXi/>
      </CommonContainer>

    </template>
    <template v-else></template>
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import JiaoLiuHuiLu from './components/JiaoLiuHuiLu/index.vue'
import ZhiLiuHuiLu from './components/ZhiLiuHuiLu/index.vue'
import DC from './components/DC/index.vue'
import MuXian from './components/MuXian/index.vue'
import JueYuan from './components/JueYuan/index'
import JiaoLiuXinXi from './components/JiaoLiuXinXi/index.vue'
import Tabs from '@/views/components/tabs/index.vue'
import ChuanGanQiXinXi from '@/views/zm/device/dev/components/ChuanGanQiXinXi/index.vue'

export default {
  name: 'Dev',
  components: {
    Tabs,
    CommonContainer,
    JiaoLiuHuiLu,
    ZhiLiuHuiLu,
    DC,
    MuXian,
    JueYuan,
    JiaoLiuXinXi,
    ChuanGanQiXinXi
  },
  data() {
    return {
      currentIndex: 0,
      tabs: ['回路信息', '直流信息', '绝缘信息', '交流信息', '传感器信息']
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
.JiaoLiuHuiLu {
  width: 50%;
}

@media screen and (max-width: 1500px) {
  .JiaoLiuHuiLu {
    width: 60%;
  }
}

@media screen and (max-width: 1300px) {
  .JiaoLiuHuiLu {
    width: 70%;
  }
}

@media screen and (max-width: 1080px) {
  .JiaoLiuHuiLu {
    width: 80%;
  }
}

@media screen and (max-width: 1080px) {
  .JiaoLiuHuiLu {
    width: 100%;
  }
}
</style>
