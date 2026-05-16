<template>
  <div class="table">
    <!-- {{ getNavibarDeviceValue() }} -->
    <div style=" height: 2px; width: 100%"></div>
    <el-table
      v-loading="loading"
      :data="demoList"

      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="编号" align="center" prop="no">
        <template #default="scope"> #{{ scope.row.no }}</template>
      </el-table-column>
      <el-table-column label="总耗电量(kWh)" align="center" prop="power"/>
      <el-table-column
        label="详细内容"
        align="center"
        prop=""
        fixed="right"
        width="200"
      >
        <template slot-scope="scope">
          <el-button size="mini" @click="handleOpen(scope.row, 1)" type="text"
          >年耗电量
          </el-button
          >
          <el-button size="mini" @click="handleOpen(scope.row, 2)" type="text"
          >月耗电量
          </el-button
          >
          <el-button size="mini" @click="handleOpen(scope.row, 3)" type="text"
          >日耗电量
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div style="height: 50px; width: 100%"></div>

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <PowerConsumption v-if="open" :type="currentType"/>
    </el-dialog>
  </div>
</template>

<script>
import PowerConsumption from '../PowerConsumptionCopy/index.vue'
import { meterTotalEnergy } from '@/api/zm/device/record'

export default {
  components: {
    PowerConsumption
  },
  data() {
    return {
      title: '',
      open: false,
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      // 查询参数
      queryParams: {
        deviceId: undefined,
        pageNum: 1,
        pageSize: 10
      },
      currentType: undefined
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
      handler(n, _) {
        if (n) {
          this.queryParams.deviceId = n
          this.queryParams.pageNum = 1
          this.getList()
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList();
  },
  methods: {
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      meterTotalEnergy(this.queryParams).then((response) => {
        this.demoList = response.rows
        this.total = response.total
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
    },
    handleOpen(row, type) {
      this.open = true
      this.title = `#${row.no}${this.getTypeString(type)}耗电量`
    },
    getTypeString(type) {
      this.currentType = type
      switch (type) {
        case 1:
          return '年'
        case 2:
          return '月'
        case 3:
          return '日'
        default:
          return ''
      }
    }
  }
}
</script>
