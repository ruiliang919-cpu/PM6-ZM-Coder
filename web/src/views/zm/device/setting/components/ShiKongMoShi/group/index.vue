<template>
  <div style="width: 100%" v-loading="loading">
    <CommonContainer
      title="分组选择"
      :contentPadding="0"
      :borderWidth="1"
      style="flex: 1; overflow: hidden"
    >

      <Item v-for="item in demoList" :data="item" class="item" :key="item.groupId"></Item>
      <template v-if="!demoList.length">
        <div style="height: 200px;display: flex; align-items: center;justify-content: center;">
          <el-empty :image-size="60" description="无数据"></el-empty>
        </div>
      </template>
    </CommonContainer>

    <pagination
      v-show="total > 0"
      :total="total"
      layout="prev, next"
      prev-text="上一页"
      next-text="下一页"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getData"
    />
  </div>
</template>
<script>
import { simpleGroupList } from '@/api/zm/device/setting'
import Item from './item.vue'
import CommonContainer from '@/components/CommonContainer/index.vue'

export default {
  props: {
    data: {
      type: Array,
      default() {
        return []
      }
    }
  },
  watch: {
    data: {
      immediate: true,
      deep: true,
      handler(newVal, oldVal) {
        this.checkedList = newVal.filter(item => {
          if (item.selectStatus === 1) return item.id
        })
      }
    }
  },
  components: {
    CommonContainer,
    Item
  },
  data() {
    return {
      checkedData: [],
      queryParams: {
        pageSize: 10,
        pageNum: 1,
        deviceId: undefined,
        controlId: undefined
      },
      total: 0,
      demoList: [],
      loading: false
    }
  },

  methods: {
    getData(deviceId, controlId) {
      this.queryParams.deviceId = deviceId
      this.queryParams.controlId = controlId
      this.loading = true
      simpleGroupList(this.queryParams).then(res => {
        this.demoList = res.rows || []
        // this.total = res.total
      }).finally(() => {
        this.loading = false
      })
    },
    reset() {
      this.queryParams = {
        pageSize: 10,
        pageNum: 1,
        deviceId: undefined,
        controlId: undefined
      }
    },
    setList(list) {
      this.demoList = list
    },
    clearList() {
      this.demoList = []
    }
  }
}
</script>
<style></style>
