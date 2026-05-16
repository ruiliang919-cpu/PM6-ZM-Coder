<template>
  <div class="app-container table">
    <Tabs :list="tabs" v-model="currentIndex" @change="handleCurrentIndexChange"></Tabs>
    <div style=" height: 20px; width: 100%"></div>
    <template v-if="currentIndex === 0">

      <Record></Record>
    </template>
    <template v-if="currentIndex === 1">
      <!-- #3673bb -->
      <el-form inline>
        <el-form-item>
          <el-button
            style="margin-left: 20px"
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
          >导出耗电统计
          </el-button>
        </el-form-item>
      </el-form>

      <TotalPower/>
      <div class="HaoDianTongJi">
        <CommonContainer
          title="年耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumption :type="4"/>
        </CommonContainer>
        <CommonContainer
          title="季耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumption :type="3"/>
        </CommonContainer>
        <CommonContainer
          title="月耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumption :type="2"/>
        </CommonContainer>
        <CommonContainer
          title="周耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumption :type="1"/>
        </CommonContainer>
        <CommonContainer
          title="日耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumption :type="0"/>
        </CommonContainer>
      </div>
    </template>
    <template v-if="currentIndex === 2">
<!--      <ElectricityMeterConsumption/>-->
      <div class="HuiLuHaoDianJiLu">
<!--        <CommonContainer-->
<!--          title="总功率"-->
<!--          :contentPadding="0"-->
<!--          :borderWidth="0"-->
<!--          class="CommonContainer"-->
<!--        >-->
<!--          <PowerConsumptionCopyCopy :type="6"/>-->
<!--        </CommonContainer>-->
        <CommonContainer
          title="年耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumptionCopyCopy :type="5"/>
        </CommonContainer>
        <CommonContainer
          title="季耗电量"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <PowerConsumptionCopyCopy :type="4"/>
        </CommonContainer>
        <CommonContainer
          title="月耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumptionCopyCopy :type="3"/>
        </CommonContainer>
        <CommonContainer
          title="周耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumptionCopyCopy :type="2"/>
        </CommonContainer>
        <CommonContainer
          title="日耗电量"
          :contentPadding="0"
          :borderWidth="0"
          class="CommonContainer"
        >
          <PowerConsumptionCopyCopy :type="1"/>
        </CommonContainer>
      </div>
    </template>
    <template v-else></template>
    <div style="height: 50px; width: 100%"></div>

  </div>
</template>

<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import Record from './components/Record/index.vue'
import PowerConsumption from './components/PowerConsumption/index.vue'
import PowerConsumptionCopyCopy from './components/PowerConsumptionCopyCopy/index.vue'
import ElectricityMeterConsumption from './components/16ElectricityMeterConsumption/index.vue'
import Tabs from '@/views/components/tabs/index.vue'
import TotalPower from '@/views/zm/device/record/components/TotalPower/index.vue'

export default {
  name: 'DeviceRecord',
  components: {
    Tabs,
    CommonContainer,
    Record,
    PowerConsumption,
    ElectricityMeterConsumption,
    TotalPower,
    PowerConsumptionCopyCopy
  },
  inject: ['getNavibarDeviceValue'],
  computed: {
    navibarDeviceValue() {
      return this.getNavibarDeviceValue()
    }
  },
  watch: {
    navibarDeviceValue: {
      handler(val) {
        this.queryParams.deviceId = val
      },
      immediate: true
    }
  },
  data() {
    return {
      currentIndex: 0,
      tabs: [
        '事件记录',
        '耗电统计',
        '40回路耗电量'
      ],
      date: [],
      queryParams: {
        deviceId: undefined
      }
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
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('zm/powerRecord/exportExcel', {
        ...this.queryParams
      }, `耗电统计_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
<style lang="scss" scoped>
.HaoDianTongJi {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;

  .CommonContainer {
    width: 100%;
    overflow: hidden;
  }
}

//@media screen and (max-width: 1272px){
//  .HaoDianTongJi {
//    grid-template-columns: 1fr 1fr 1fr 1fr;
//  }
//}
//@media screen and (max-width: 1045px){
//  .HaoDianTongJi {
//    grid-template-columns: 1fr 1fr 1fr;
//  }
//}
@media screen and (max-width: 1150px) {
  .HaoDianTongJi {
    grid-template-columns: 1fr 1fr;
  }

}

@media screen and (max-width: 750px) {
  .HaoDianTongJi {
    grid-template-columns: 1fr;
  }
}

.HuiLuHaoDianJiLu {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;

  .CommonContainer {
    width: 100%;
    overflow: hidden;
  }
}

@media screen and (max-width: 760px) {
  .HuiLuHaoDianJiLu {
    grid-template-columns: 1fr 1fr;
  }

}

@media screen and (max-width: 512px) {
  .HuiLuHaoDianJiLu {
    grid-template-columns: 1fr;
  }

}
</style>
