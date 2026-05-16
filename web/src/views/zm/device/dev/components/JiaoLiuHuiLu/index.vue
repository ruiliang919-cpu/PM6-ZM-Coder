<template>
  <div class=" table">
    <!-- {{ getNavibarDeviceValue() }} -->
    <div style=" height: 2px; width: 100%"></div>
    <el-table
      v-if="demoList"
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="编号" align="center" prop="id">
        <template #default="scope"> #{{ scope.row.id }}</template>
      </el-table-column>
      <el-table-column label="开关设定" align="center" prop="">
        <template v-slot="scope">
          <el-tag v-if="scope.row.switchSetting" type="success">开</el-tag>
          <el-tag v-if="!scope.row.switchSetting" type="danger">关</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开关反馈" align="center" prop="">
        <template v-slot="scope">
          <el-tag v-if="scope.row.switchFeedback" type="success">开</el-tag>
          <el-tag v-if="!scope.row.switchFeedback" type="danger">关</el-tag>
        </template>
      </el-table-column>
      <template v-slot:empty>
        <el-empty description="无数据"></el-empty>
      </template>
    </el-table>
    <div v-else style="border: 1px solid #2280ec">
      <el-empty description="电源柜无此数据"></el-empty>
    </div>
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
import { listDemo } from '@/api/demo/demo'
import { acList } from '@/api/zm/device/dev'

export default {
  components: {},
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
        slaveId: -1
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
          this.queryParams.slaveId = n
          this.queryParams.pageNum = 1
          this.getList()
          this.startTimer()
        } else {
          this.loading = false
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList()
  },
  beforeDestroy() {
    clearInterval(this.timer)
  },
  methods: {
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      this._get()
    },
    _get() {
      acList(this.queryParams).then((response) => {
        this.demoList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this._get()
      }, 5000)
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
