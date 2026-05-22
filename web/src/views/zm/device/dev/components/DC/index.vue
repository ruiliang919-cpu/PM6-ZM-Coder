<template>
  <CommonContainer
    :title="cabinetType === 1?'AC/DC信息':'DC/DC信息'"
    :contentPadding="0"
    :borderWidth="0"
  >
    <div class="app-container table">
      <div style=" height: 2px; width: 100%"></div>
      <el-table
        v-loading="loading"
        :data="demoList"
        :cell-class-name="handleRowStyle"
        :header-cell-style="handleHeaderRowStyle"
      >
        <!-- <el-table-column type="selection" width="55" align="center" /> -->
        <el-table-column label="编号" align="center" prop="id">
          <template #default="scope">
            #{{ scope.row.id }}
          </template>
        </el-table-column>
        <el-table-column label="运行状态" align="center" prop="">
          <template v-slot="scope">
            <el-tag v-if="!scope.row.moduleType" type="success">正常</el-tag>
            <el-tag v-if="!!scope.row.moduleType" type="danger">故障</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="输出电压" align="center" prop="voltage"/>
        <el-table-column label="输出电流" align="center" prop="electricity"/>
        <el-table-column label="通讯状态" align="center" prop="">
          <template v-slot="scope">
            <el-tag v-if="!scope.row.onlineType" type="success">正常</el-tag>
            <el-tag v-if="!!scope.row.onlineType" type="danger">故障</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="开关机状态" align="center" width="300" prop="" v-if="cabinetType === 1">
          <template v-slot="scope">
            <el-tag v-if="!!scope.row.switchStatus" type="success">开</el-tag>
            <el-tag v-if="!scope.row.switchStatus" type="danger">关</el-tag>
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
  </CommonContainer>
</template>

<script>
import { dcAc } from '@/api/zm/device/dev'
import CommonContainer from '@/components/CommonContainer/index.vue'
import polling from '@/mixins/polling'

export default {

  components: { CommonContainer },
  mixins: [polling],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      type: 0,
      cabinetType: undefined,
      // 测试单表表格数据
      demoList: [
        // {
        //   // 主键ID
        //   id: 11,
        //   // 设备ID
        //   deviceId: 1,
        //   // 整流模块编号
        //   no: 1,
        //   // 整流模块故障 0正常 1故障
        //   moduleType: 0,
        //   // 整流模块通讯故障 0正常 1故障
        //   onlineType: 0,
        //   // 整流模块输出电压
        //   voltage: '0.00',
        //   // 整流模块输出电流
        //   electricity: '0.14',
        //   /**
        //    * 1.机柜类型为 1 则为电源柜，显示 AC/DC 的列表（显示开关机状态）；
        //    * 2.机柜类型为 0 则为配电柜，显示 DC/DC 的列表（没有开关机状态）；
        //    */
        //   // 整流模块的开关机状态
        //   switchStatus: 0
        // }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        slaveId: -1
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
          this.queryParams.slaveId = n
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
    // this.getList();
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
      dcAc(this.queryParams)
        .then((response) => {
          const data = response?.data?.data || {}
          this.demoList = Array.isArray(data.rows) ? data.rows : (Array.isArray(data) ? data : [])
          this.total = typeof data.total === 'number' ? data.total : 0
          this.cabinetType = response?.data?.cabinetType || undefined
        })
        .catch((error) => {
          console.error('获取直流数据失败:', error)
          this.demoList = []
          this.total = 0
        })
        .finally(() => {
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
        fontSize: '16px',
        padding: '20px 0'
        // border:"1px solid #2280ec !important"
      }
    }
  }
}
</script>


