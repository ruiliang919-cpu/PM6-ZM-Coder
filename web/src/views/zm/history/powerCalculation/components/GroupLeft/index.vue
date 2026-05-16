<template>
  <div v-loading="loading">
    <div v-if="baseSceneList.length" class="list">
      <el-radio-group v-loading="loading" v-model="checked" style="width: 100%"
                      @change="handleChange($event)"
      >
        <Item v-for="(item,index) in baseSceneList" :data="item" :key="item.zoneId"
        />
        <!--    <pagination-->
        <!--      v-show="total>0"-->
        <!--      :total="total"-->
        <!--      :page.sync="queryParams.pageNum"-->
        <!--      :limit.sync="queryParams.pageSize"-->
        <!--      @pagination="getList"-->
        <!--    />-->
      </el-radio-group>
    </div>
    <div>
      <template v-if="!baseSceneList.length">
        <div style="border: #2280ec 0px solid; flex: 1">
          <el-empty description="无数据"></el-empty>
        </div>
      </template>
    </div>
  </div>

</template>
<script>
import Item from './item.vue'
import { calculationZoneList } from '@/api/zm/history/powerCalculation'

export default {
  components: {
    Item
  },
  data() {
    return {
      checked: undefined,
      // 总条数
      total: 0,
      baseSceneList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: undefined,
        name: undefined
      },
      loading: false,
      currentSelected: {}
    }
  },
  methods: {
    /** 查询场景列表 */
    getList() {
      this.loading = true
      calculationZoneList(this.queryParams).then(response => {
        this.baseSceneList = response.rows || response.data || []
        this.total = response.total || 0
        if (this.baseSceneList.length) {
          this.checked = this.baseSceneList[0].zoneId
          this.currentSelected = this.baseSceneList[0]
          this.handleChange(this.baseSceneList[0].zoneId)
        } else {
          this.handleChange(undefined)
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleChange(zoneId) {
      console.log(zoneId)
      this.currentSelected = this.baseSceneList.find(item => item.zoneId === zoneId)
      this.$emit('change', zoneId)
    }
  },
  mounted() {
    this.getList()
  }
}
</script>
<style lang="scss">
.no-bottom-border {
  border-width: 0 !important;
}

.list {
  height: 300px;
  overflow-x: hidden;
  overflow-y: auto;
}
</style>
