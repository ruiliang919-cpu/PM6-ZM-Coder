<template>
  <div class="table">
    <!-- {{ getNavibarDeviceValue() }} -->
    <el-form style="background-color: #2280ec" :inline="true">
      <el-form-item style="padding: 10px 20px; margin: 0">
        <template #label>
          <span style="color: #fff">日期:</span>
        </template>
        <el-date-picker
          v-model="date"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="timestamp"
          szie="small"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item style="padding: 10px 20px; margin: 0">
        <el-button type="" style="width: 100px" round @click="getList">搜索</el-button>
      </el-form-item>
    </el-form>

    <div style="height: 20px"></div>
    <div style=" height: 2px; width: 100%"></div>
    <el-table
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="编号" align="center" prop="id">
        <template #default="scope"> #{{ scope.row.id }}</template>
      </el-table-column>

      <el-table-column label="时间" align="center" prop="">
        <template v-slot="scope">
          {{ formatDate(scope.row.stime) }}
        </template>
      </el-table-column>
      <el-table-column label="设备名称" align="center" prop="id">
        <template #default="scope"> #{{ scope.row.name }}</template>
      </el-table-column>
      <el-table-column label="事件" align="center" prop="message"/>
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
import { getRecordOne } from '@/api/zm/device/record'
import { formatDate } from '@/utils'
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
      date: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        startTime: undefined,
        endTime: undefined,
        deviceId: undefined
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
      handler(val) {
        if (val) {
          this.$stopPolling()
          this.queryParams.deviceId = val
          this.getList()
          this.$startPolling()
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList()
  },
  methods: {
    formatDate,
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      this.queryParams.startTime = this.date[0]
      this.queryParams.endTime = this.date[1]
      this._get()
    },
    pollingFetch() {
      this._get()
    },
    _get() {
      getRecordOne(this.queryParams).then((response) => {
        this.demoList = response.rows || response.data.rows || response.data || []
        this.total = response.total || response.data.total || 0

      }).finally(() => {
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
