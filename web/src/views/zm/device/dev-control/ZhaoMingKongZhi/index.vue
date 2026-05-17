<template>
  <div class="ZhaoMingKongZhi" v-loading="loading">
    <div>
      <div>
        <CommonContainer
          title="总开关"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Table1 :systemSwitch="lightControlListData.systemControl.systemSwitch" :device-id="queryParams.deviceId"/>
        </CommonContainer>

        <CommonContainer
          title="工作模式"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden; margin-top: 10px"
        >
          <Table2 :workMode="lightControlListData.systemControl.workMode" :device-id="queryParams.deviceId"/>
        </CommonContainer>

        <CommonContainer
          title="场景控制"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden; margin-top: 10px"

        >
          <Table3 @success="getData" :sceneControl="lightControlListData.sceneControl"
                  :deviceId="queryParams.deviceId"
          />
        </CommonContainer>

        <CommonContainer
          title="控制方式"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden; margin-top: 10px"

        >
          <ControlType :device-id="queryParams.deviceId"/>
        </CommonContainer>
      </div>
    </div>
    <div class="right">
      <div>
        <LightsControl @success="getData" :loopControl="lightControlListData.loopControl"
                       :loop-lux="lightControlListData.loopLux"
                       :device-id="queryParams.deviceId"
        />
      </div>
      <div>
        <GroupControl @success="getData" :groupControl="lightControlListData.groupControl"
                      :group-lux="lightControlListData.groupLux"
                      :device-id="queryParams.deviceId"
        />
      </div>
    </div>
  </div>
</template>
<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import Table1 from './table/table1.vue'
import Table2 from './table/table2.vue'
import Table3 from './table/table3.vue'
import LightsControl from '@/views/zm/device/dev-control/ZhaoMingKongZhi/lightsControl/index.vue'
import GroupControl from '@/views/zm/device/dev-control/ZhaoMingKongZhi/groupControl/index.vue'
import { lightControlList } from '@/api/zm/device/dev-control'
import ControlType from '@/views/zm/device/dev-control/ZhaoMingKongZhi/controlType/index.vue'

export default {
  components: {
    CommonContainer,
    Table1,
    Table2,
    Table3,
    LightsControl,
    GroupControl,
    ControlType
  },
  data() {
    return {
      loading: false,
      lightControlListData: {
        // 分组控制列表
        groupControl: [],
        // 回路控制
        loopControl: [],
        // 场景控制列表
        sceneControl: [],
        // 总开关和工作模式
        systemControl: {
          // 总开关 0关1开
          systemSwitch: 0,
          // 工作模式 0自动; 1手动
          workMode: 0
        }
      },
      queryParams: {
        deviceId: undefined
      },
      timer: null
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
        console.log('navibarDeviceValue ========>', n)
        if (n) {
          this.stopTimer()
          this.queryParams.deviceId = n
          this.getData()
        }
      },
      immediate: true
    }
  },
  methods: {
    getData() {
      this.loading = true
      lightControlList(this.queryParams).then(res => {
        console.log(res)
        this.lightControlListData = res.data
      }).finally(() => {
        this.loading = false
        this.startTimer()
      })
    },
    startTimer() {
      this.stopTimer()
      this.timer = setInterval(() => {
        lightControlList(this.queryParams).then(res => {
          this.lightControlListData.systemControl = res.data.systemControl
        })
      }, 5000)
    },
    stopTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
    }
  },
  mounted() {
    // this.getData()
  },
  beforeDestroy() {
    this.stopTimer()
  }
}
</script>
<style lang="scss" scoped>
.ZhaoMingKongZhi {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }
}

@media screen and (max-width: 1200px) {
  .ZhaoMingKongZhi {
    grid-template-columns: 1fr;
  }
}

.right {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }
}

@media screen and (max-width: 1000px) {
  .right {
    grid-template-columns: 1fr;
  }
}
</style>
