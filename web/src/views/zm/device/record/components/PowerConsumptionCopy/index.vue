<template>
  <div class="table">
    <div style=" height: 10px; width: 100%"></div>
    <el-table
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="时间" align="center" prop="no">
        <template v-slot="scope">
          <span style="word-break: break-word" v-if="type === 1">{{ getDate(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else-if="type === 2">{{ getWeekNumber(scope.row.timestamp)}}</span>
          <span style="word-break: break-word" v-else-if="type === 3">{{ getMonth(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else-if="type === 4">{{ getQuarterFromApril(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else-if="type === 5">{{ getFullYear(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else>{{ formatDate(scope.row.timestamp) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="耗电量(kWh)" align="center" prop="power"/>
      <template v-slot:empty>
        <el-empty description="无数据"></el-empty>
      </template>
    </el-table>

    <pagination
      v-show="total"
      :total="total"
      layout="sizes,total, prev,  pager, next"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
import {
  meterDayEnergy,
  meterMonthEnergy, meterQuarterEnergy,
  meterTotalEnergy, meterWeekEnergy, meterYearEnergy
} from '@/api/zm/device/record'
import { formatDate, getDate, getFullYear, getMonth, getQuarterFromApril, getWeekNumber } from '@/utils'

export default {
  components: {},
  props: {
    type: {
      type: Number,
      default: 0
    }
  },
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
          this.queryParams.deviceId = val
          this.getList()
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList()
  },
  methods: {
    getFullYear,
    getQuarterFromApril,
    getMonth,
    getWeekNumber,
    getDate,
    formatDate,
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      if (this.type === 1) {
        meterYearEnergy(this.queryParams).then((response) => {
          this.demoList = response.rows
          this.total = response.total
          this.loading = false
        })
      } else if (this.type === 2) {
        meterMonthEnergy(this.queryParams).then((response) => {
          this.demoList = response.rows
          this.total = response.total
          this.loading = false
        })
      } else if (this.type === 3) {
        meterDayEnergy(this.queryParams).then((response) => {
          this.demoList = response.rows
          this.total = response.total
          this.loading = false
        })
      } else if (this.type === 4) {
        meterWeekEnergy(this.queryParams).then((response) => {
          this.demoList = response.rows
          this.total = response.total
          this.loading = false
        })
      } else if (this.type === 5) {
        meterQuarterEnergy(this.queryParams).then((response) => {
          this.demoList = response.rows
          this.total = response.total
          this.loading = false
        })
      }

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
