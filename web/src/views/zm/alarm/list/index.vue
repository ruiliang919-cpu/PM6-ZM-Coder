<template>
  <div class="app-container">
    <el-form
      :model="queryParams"
      ref="queryForm"
      :inline="true"
    >
      <el-form-item label="起始时间" prop="testKey">
        <el-date-picker
          v-model="queryParams.startTime"
          placeholder="请输入起始时间"
          format="yyyy-MM-dd HH:mm:ss"
          value-format="timestamp"
          clearable
          size="small"
        />
      </el-form-item>
      <el-form-item label="箱柜名称" prop="testKey">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入箱柜名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="告警信息名称" prop="value">
        <el-input
          v-model="queryParams.eventName"
          placeholder="请输入告警信息名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button
          round
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
        >搜索
        </el-button
        >
        <el-button round icon="el-icon-refresh" size="mini" @click="resetQuery"
        >重置
        </el-button
        >
      </el-form-item>
    </el-form>

    <el-table
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
      v-loading="loading"
      :data="demoList"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="编号" align="center" prop="id"/>
      <el-table-column label="开始时间" align="center" prop="stime">
        <template v-slot="scope">
          {{ formatDate(scope.row.stime) }}
        </template>
      </el-table-column>
      <el-table-column
        label="箱柜名称"
        align="center"
        prop="name"
      />
      <el-table-column
        label="告警信息"
        align="center"
        prop="message"
      />
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { getFaultSeven } from '@/api/zm/alarm/list'
import { formatDate } from '@/utils'

export default {
  name: 'List',
  components: {},
  data() {
    return {
      //按钮loading
      buttonLoading: false,
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
        startTime: undefined,
        deviceName: undefined,
        eventName: undefined
      },
      timer: null
    }
  },
  created() {
    this.getList()
    this.startTimer()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    formatDate,
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      this._get()
    },
    _get() {
      getFaultSeven(this.queryParams).then((response) => {
        this.demoList = response.rows || []
        this.total = response.total || 0

      }).finally(() => {
        this.loading = false
      })
    },
    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this._get()
      }, 5000)
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.deviceName = undefined
      this.queryParams.startTime = undefined
      this.queryParams.eventName = undefined
      this.queryParams.pageNum = 1
      this.queryParams.pageSize = 10
      // this.resetForm("queryForm");
      this.handleQuery()
    },
    handleRowStyle(row) {
      //   console.log(row);
      if (row.rowIndex % 2 != 0) {
        return 'custom-border-color'
      }
      return 'custom-cell-class-name custom-border-color'
    },
    handleHeaderRowStyle() {
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
