<template>
  <div class="table">
    <div style="height: 2px; width: 100%"></div>
    <el-table
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="时段" align="center" prop="id" width="60">
        <template #default="scope"> #{{ scope.row.frameId }}</template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="">
        <template v-slot="scope">
          <el-time-select
            style="width: 80px"
            v-model="scope.row.stime"
            :picker-options="{start: '00:00',step: '00:01',end: '24:00'}"
            placeholder="选择时间"
            :clearable="false"
            class="picker"
          >
          </el-time-select>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="">
        <template v-slot="scope">
          <el-time-select
            style="width: 80px"
            v-model="scope.row.etime"
            :picker-options="{start: '00:00',step: '00:01',end: '24:00'}"
            placeholder="选择时间"
            :clearable="false"
            class="picker"
          >
          </el-time-select>
        </template>
      </el-table-column>
      <el-table-column label="亮度" align="center" prop="lux">
        <template v-slot="scope">
          <el-input class="input" type="number" v-model="scope.row.lux" style="padding: 0"/>
        </template>
      </el-table-column>
      <el-table-column label="开关" align="center" prop="" width="70">
        <template v-slot="scope">
          <el-switch @change="$event?scope.row.switchStatus =1:scope.row.switchStatus = 0"
                     :value="!!scope.row.switchStatus"
          />
        </template>
      </el-table-column>
      <el-table-column label="使能" align="center" prop="" width="60">
        <template v-slot="scope">
          <el-checkbox @change="$event?scope.row.enabledStatus =1:scope.row.enabledStatus = 0"
                       :value="!!scope.row.enabledStatus"
          />
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      layout="prev, next"
      prev-text="上一页"
      next-text="下一页"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
import { timeControlSimpleList } from '@/api/zm/device/setting'
import { formatDate } from '@/utils'

export default {
  components: {},
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: undefined,
        controlId: undefined
      }
    }
  },
  created() {
    // this.getList()
  },
  methods: {
    formatDate,
    /** 查询测试单表列表 */
    getList(deviceId, controlId) {
      this.queryParams.deviceId = deviceId
      this.queryParams.controlId = controlId
      this.loading = true
      timeControlSimpleList(this.queryParams).then((response) => {
        this.demoList = response.data
        // this.total = response.total

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
        padding: '20px 0'
        // border:"1px solid #2280ec !important"
      }
    },
    setList(list) {
      this.demoList = list
    },
    clearList() {
      this.demoList = []
      this.total = 0
    }
  }
}
</script>
<style scoped lang="scss">
::v-deep .picker {
  .el-input__inner{
    padding: 0 0 0 30px;
  }
}

::v-deep .input {
  .el-input__inner{
    padding: 0 5px;
  }
}
</style>
