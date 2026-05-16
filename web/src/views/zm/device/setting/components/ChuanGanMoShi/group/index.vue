<template>
  <div style="width: 100%" v-loading="loading" >
    <Item v-for="item in demoList" :data="item" class="item" :key="item.groupId"/>
    <template v-if="!demoList.length">
      <div style="border:1px solid #2280ec;border-top: 0;">
        <el-empty description="无数据"></el-empty>
      </div>

    </template>
    <pagination
      v-show="total>0"
      :total="total"
      layout="prev, next"
      prev-text="上一页"
      next-text="下一页"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div style="height: 50px"></div>
  </div>
</template>
<script>
import { infraredGroupSelect, illuminanceGroupSelect } from '@/api/zm/device/setting'
import Item from './item.vue'

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
      if (this.type === 0) {
        infraredGroupSelect(this.queryParams).then((response) => {
          this.demoList = response.data || response.rows || response.data.rows
          // this.total = response.total || response.data.total || 0
        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 1) {
        illuminanceGroupSelect(this.queryParams).then((response) => {
          this.demoList = response.data || response.rows || response.data.rows
          // this.total = response.total || response.data.total || 0
        }).finally(() => {
          this.loading = false
        })
      }
    },
    clearList() {
      this.demoList = []
      this.total = 0
    }
  }
}
</script>
<style></style>
