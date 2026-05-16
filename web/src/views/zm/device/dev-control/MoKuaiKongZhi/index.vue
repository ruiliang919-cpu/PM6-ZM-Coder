<template>
  <div v-loading="loading">
    <div class="MoKuaiKongZhi">
      <div>
        <CommonContainer
          title="AC/DC输出电压"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Table1 @update="data.acDcVoltage = $event" :ac-dc-voltage="data.acDcVoltage" :device-id="queryParams.deviceId"/>
        </CommonContainer>
      </div>
      <div>
        <CommonContainer
          title="DC/DC输出电压"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Table2 @update="data.dcDcVoltage = $event" :dc-dc-voltage="data.dcDcVoltage" :device-id="queryParams.deviceId"/>
        </CommonContainer>
      </div>
    </div>

  </div>
</template>
<script>
import { moduleControlList } from '@/api/zm/device/dev-control'
import CommonContainer from '@/components/CommonContainer/index.vue'
import Table1 from './table/table1.vue'
import Table2 from './table/table2.vue'

export default {
  components: {
    CommonContainer,
    Table1,
    Table2
  },
  data() {
    return {
      loading: false,
      queryParams: {
        deviceId: undefined
      },
      data: {
        acDcVoltage: null,
        dcDcVoltage: null
      }
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
      moduleControlList(this.queryParams).then(res => {
        this.data = res.data
      }).finally(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.MoKuaiKongZhi {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }
}
@media screen and (max-width: 760px) {
  .MoKuaiKongZhi {
    grid-template-columns: 1fr;
  }
}
</style>
