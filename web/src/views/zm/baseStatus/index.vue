<template>
  <div class="app-container table">

    <div class="status_cards" v-loading="loading">
      <div v-for="item in demoList" :key="item.id">
        <card :data="item"></card>
      </div>

    </div>
    <div style="text-align:center">
      <el-empty v-if="!demoList.length" description="无数据"></el-empty>
    </div>
    <pagination
      v-show="total > 0 && demoList.length"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>

import { lightList } from '@/api/zm/baseStatus'
import card from './components/card/index.vue'
import polling from '@/mixins/polling'

export default {
  name: 'Status',
  components: {
    card
  },
  mixins: [polling],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 9,
        testKey: undefined,
        value: undefined,
        createTime: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      this._get()
    },
    pollingFetch() {
      this._get()
    },
    _get() {
      this.queryParams.params = {}
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params['beginCreateTime'] =
          this.daterangeCreateTime[0]
        this.queryParams.params['endCreateTime'] = this.daterangeCreateTime[1]
      }
      lightList(this.queryParams).then((response) => {
        this.demoList = response.rows
        this.total = response.total

      }).finally(() => {
        this.loading = false
      })
    },
    handleRowStyle(row) {
      //   console.log(row);
      if (row.rowIndex % 2 != 0) {
        return 'custom-border-color'
      }
      return 'custom-cell-class-name custom-border-color'
    },
    handleHeaderRowStyle(row) {
      console.log(row)
      return {
        backgroundColor: '#2280ec',
        color: '#fff',
        fontSize: '16px'
        // border:"1px solid #2280ec !important"
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.status_cards {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
}

@media screen and (max-width: 1400px) {
  .status_cards {
    grid-template-columns: 1fr 1fr;
  }
}

@media screen and (max-width: 820px) {
  .status_cards {
    grid-template-columns: 1fr;
  }
}
</style>
