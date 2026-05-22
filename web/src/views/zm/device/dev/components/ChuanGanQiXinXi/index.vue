<template>
  <div class=" table">
    <div style=" height: 2px; width: 100%"></div>
    <el-table
      v-if="demoList"
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <el-table-column label="直流回路名称" align="center" prop="">
        <template v-slot="scope">
          {{ scope.row.name}}
        </template>
      </el-table-column>
      <el-table-column label="漏电流" align="center" prop="" width="160px">
        <template v-slot="scope">
          {{ scope.row.current }}
        </template>
      </el-table-column>
      <template v-slot:empty>
        <el-empty description="无数据"></el-empty>
      </template>
    </el-table>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
import { leakageGetTable } from '@/api/zm/device/dev'
import polling from '@/mixins/polling'

export default {
  components: {},
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
        pageSize: 10,
        deviceId: -1
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
          this.$stopPolling()
          this.queryParams.deviceId = n
          this.queryParams.pageNum = 1
          this.getList()
          this.$startPolling()
        } else {
          this.$stopPolling()
          this.loading = false
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList()
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
      leakageGetTable(this.queryParams).then((response) => {
        this.demoList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    handleRowStyle(row) {
      //   console.log(row);
      if (row.rowIndex % 2 !== 0) {
        return 'custom-border-color'
      }
      return 'custom-cell-class-name custom-border-color'
    },
    handleHeaderRowStyle(row) {
      console.log(row)
      return {
        backgroundColor: '#2280ec',
        color: '#fff',
        fontSize: '16px',
        padding: '15px 0'
        // border:"1px solid #2280ec !important"
      }
    }
  }
}
</script>
