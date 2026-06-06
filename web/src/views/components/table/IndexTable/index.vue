<template>
  <div class="app-container table">
    <div style=" height: 2px; width: 100%"></div>
    <el-table
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!--      <el-table-column label="编号" align="center" prop="deviceId"/>-->
      <el-table-column label="编号" align="center" prop="orderNo"/>
      <el-table-column label="机柜名称" align="center" prop="deviceName"/>
      <el-table-column label="所属区域" align="center" prop="area"/>
      <el-table-column label="系统开关" align="center" prop="runStatus">
        <template v-slot="scope">
          <el-tag type="success" v-if="scope.row.runStatus">开</el-tag>
          <el-tag type="danger" v-if="!scope.row.runStatus">关</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="告警状态" align="center" prop="deviceStatus">
        <template v-slot="scope">
          <el-tag type="success" v-if="!scope.row.deviceStatus">正常</el-tag>
          <el-tag type="danger" v-if="scope.row.deviceStatus">故障/告警</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="通讯状态" align="center" prop="onlineStatus">
        <template v-slot="scope">
          <el-tag type="success" v-if="scope.row.onlineStatus">在线</el-tag>
          <el-tag type="danger" v-if="!scope.row.onlineStatus">离线</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="工作模式" align="center" prop="runStatus">
        <template v-slot="scope">
          <el-tag type="primary" v-if="!scope.row.runMode">自动</el-tag>
          <el-tag type="primary" v-if="scope.row.runMode">手动</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="母线电压" align="center" prop="">
        <template v-slot="scope">
          {{ scope.row.dcBusVoltage ? scope.row.dcBusVoltage + 'V' : '未读取' }}
          <!--          <div style="padding: 0 0 5px 0">-->
          <!--            <el-tag type="primary" style="width: 170px">母线正对地电压:{{scope.row.busDirectVoltageToEarth}}V</el-tag>-->
          <!--          </div>-->
          <!--          <div style="padding: 0 0 5px 0">-->
          <!--            <el-tag type="primary" style="width: 170px">母线负对地电压:{{scope.row.busNegativeVoltageToEarth}}V</el-tag>-->
          <!--          </div>-->
          <!--          <div>-->
          <!--            <el-tag type="primary" style="width: 170px">母线交窜直电压:{{scope.row.busbarCrossoverVoltage}}V</el-tag>-->
          <!--          </div>-->
        </template>
      </el-table-column>
      <el-table-column label="调光回路" align="center" prop="dimmerNum">
        <template v-slot="scope">
          {{ scope.row.dimmerNum === null ? 'N/A' : scope.row.dimmerNum + '条'}}
        </template>
      </el-table-column>
      <el-table-column label="直流模块" align="center" prop="dcModuleNum">
        <template v-slot="scope">
          {{ scope.row.dcModuleNum === null ? 'N/A' : scope.row.dcModuleNum + '个' }}
        </template>
      </el-table-column>
      <el-table-column label="交流开关" align="center" prop="acSwitchNum">
        <template v-slot="scope">
          {{ scope.row.acSwitchNum === null ? 'N/A' : scope.row.acSwitchNum + '个' }}
        </template>
      </el-table-column>
      <el-table-column label="主监控版本" align="center" prop="version">
        <template v-slot="scope">
          {{ scope.row.version === null ? 'N/A' : scope.row.version }}
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
  </div>
</template>

<script>
// import { listDemo, pageDemo } from '@/api/demo/demo'
import { cabinetList } from '@/api/index'

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
        pageSize: 10
      },
      timer: null
    }
  },
  created() {
    this.getList()
    this.timer = setInterval(() => {
      this._get()
    }, 5000)
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
      cabinetList(this.queryParams).then((response) => {
        this.demoList = response.rows
        this.total = response.total
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


