<template>
  <div class="table">
    <!-- {{ getNavibarDeviceValue() }} -->
    <div style=" height: 10px; width: 100%"></div>
    <el-table
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
      style="flex: 1"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="时间" align="center" prop="id">
        <template v-slot="scope">
          <span style="word-break: break-word" v-if="type === 0">{{ getDate(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else-if="type === 1">{{ getWeekNumber(scope.row.timestamp)}}</span>
          <span style="word-break: break-word" v-else-if="type === 2">{{ getMonth(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else-if="type === 3">{{ getQuarterFromApril(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else-if="type === 4">{{ getFullYear(scope.row.timestamp) }}</span>
          <span style="word-break: break-word" v-else>{{ formatDate(scope.row.timestamp) }}</span>
        </template>
      </el-table-column>
      <!--      <el-table-column label="分区名称" align="center" prop="name"/>-->
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
  powerRecordGetDay,
  powerRecordGetMonth,
  powerRecordGetQuarter,
  powerRecordGetWeek,
  powerRecordGetYear
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
      handler(val) {
        if (val) {
          if (this.timer) {
            clearInterval(this.timer)
            this.timer = null
          }
          this.queryParams.deviceId = val
          this.getList()
          this.startTimer()
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList()
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
      this.timer = null
    }
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
      this._get()
    },
    _get() {
      if (this.type === 0) {
        powerRecordGetDay(this.queryParams).then((response) => {
          this.demoList = response.rows || []
          this.total = response.total || 0
        }).catch((error) => {
          console.error('获取能耗数据失败:', error)
          this.demoList = []
          this.total = 0
        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 1) {
        powerRecordGetWeek(this.queryParams).then((response) => {
          this.demoList = response.rows || []
          this.total = response.total || 0
        }).catch((error) => {
          console.error('获取能耗数据失败:', error)
          this.demoList = []
          this.total = 0
        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 2) {
        powerRecordGetMonth(this.queryParams).then((response) => {
          this.demoList = response.rows || []
          this.total = response.total || 0
        }).catch((error) => {
          console.error('获取能耗数据失败:', error)
          this.demoList = []
          this.total = 0
        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 3) {
        powerRecordGetQuarter(this.queryParams).then((response) => {
          this.demoList = response.rows || []
          this.total = response.total || 0
        }).catch((error) => {
          console.error('获取能耗数据失败:', error)
          this.demoList = []
          this.total = 0
        }).finally(() => {
          this.loading = false
        })
      } else if (this.type === 4) {
        powerRecordGetYear(this.queryParams).then((response) => {
          this.demoList = response.rows || []
          this.total = response.total || 0
        }).catch((error) => {
          console.error('获取能耗数据失败:', error)
          this.demoList = []
          this.total = 0
        }).finally(() => {
          this.loading = false
        })
      }
    },
    startTimer() {
      if (this.timer) {
        clearInterval(this.timer)
        this.timer = null
      }
      // 基准间隔根据type确定，加2秒随机抖动
      const baseIntervals = { 0: 10000, 1: 20000, 2: 30000, 3: 40000, 4: 50000 }
      const interval = (baseIntervals[this.type] || 30000) + Math.floor(Math.random() * 2000)
      this.timer = setInterval(() => {
        this._get()
      }, interval)
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
<style lang="scss" scoped>
.table {
  display: flex;
  flex-direction: column;
  height: 100%;
}
</style>
