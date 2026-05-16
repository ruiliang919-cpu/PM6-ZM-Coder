<template>
  <div v-loading="loading">
    <div v-if="demoList.length" class="list">
      <el-checkbox-group v-loading="loading" v-model="checkedData" style="width: 100%"
      >
        <Item v-for="(item,index) in demoList" :data="item" :key="item.zoneId"
        />
        <!--    <pagination-->
        <!--      v-show="total>0"-->
        <!--      :total="total"-->
        <!--      :page.sync="queryParams.pageNum"-->
        <!--      :limit.sync="queryParams.pageSize"-->
        <!--      @pagination="getList"-->
        <!--    />-->
      </el-checkbox-group>
    </div>
    <div>
      <template v-if="!demoList.length">
        <div style="border: #2280ec 0px solid; flex: 1">
          <el-empty description="无数据"></el-empty>
        </div>
      </template>
    </div>
  </div>
</template>
<script>
import Item from './item.vue'
import { calculationZoneZoneList } from '@/api/zm/history/powerCalculation'

export default {
  components: {
    Item
  },
  data() {
    return {
      total: 0,
      type: 0,
      loading: false,
      demoList: [],
      queryParams: {
        sensorId: undefined,
        deviceId: undefined,
        pageNum: 1,
        pageSize: 10
      },
      checkedData: []
    }
  },
  methods: {
    setData(type = 0, deviceId, sensorId) {
      this.type = type
      this.queryParams.deviceId = deviceId
      this.queryParams.sensorId = sensorId
      this.queryParams.pageNum = 1
      this.queryParams.pageSize = 10
    },
    getList() {
      this.loading = true
      calculationZoneZoneList(this.queryParams).then((response) => {
        this.demoList = response.data || response.rows || response.data.rows
        // this.total = response.total || response.data.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    clearList() {
      this.demoList = []
      this.total = 0
    },
    clearCheckDate() {
      this.checkedData = []
    },
    setCheckedData(data) {
      this.checkedData = data
    }
  },
  mounted() {
    this.getList()
  }
}
</script>
<style lang="scss" scoped>
.list {
  height: 300px;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
