<template>
  <div class="table">
    <div style=" height: 2px; width: 100%"></div>
    <el-form>
      <el-form-item>
        <el-button type="primary" round @click="save">保存</el-button>
      </el-form-item>
    </el-form>
    <el-table
      v-loading="loading"
      :data="demoList"
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <el-table-column label="编号" align="center" prop="id">
        <template #default="scope"> #{{ scope.row.no }}</template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="stime">
        <template v-slot="scope">
          <el-time-select
            style="width: 100px"
            v-model="scope.row.stime"
            :picker-options="{start: '00:00',step: '00:01',end: '24:00'}"
            placeholder="选择时间"
            :clearable="false"
          >
          </el-time-select>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="etime">
        <template v-slot="scope">
          <el-time-select
            style="width: 100px"
            v-model="scope.row.etime"
            :picker-options="{start: '00:00',step: '00:01',end: '24:00'}"
            placeholder="选择时间"
            :clearable="false"
          >
          </el-time-select>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="">
        <template v-slot="scope">
          <!--          <el-tag v-if="!scope.row.enabled">开</el-tag>-->
          <!--          <el-tag v-else>关</el-tag>-->

          <el-switch :value="!scope.row.status" @change="$event?scope.row.status=0:scope.row.status=1"/>
        </template>
      </el-table-column>
      <el-table-column label="使能" align="center" prop="">
        <template v-slot="scope">
          <!--          <el-tag v-if="!scope.row.enabled">开</el-tag>-->
          <!--          <el-tag v-else>关</el-tag>-->
          <el-switch :value="!scope.row.enabled" @change="$event?scope.row.enabled=0:scope.row.enabled=1"/>
<!--          <el-checkbox :checked="!scope.row.enabled" @change="$event?scope.row.enabled=0:scope.row.enabled=1"/>-->
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
import { listDemo, pageDemo } from '@/api/demo/demo'
import { acSwitchControlList, updateAcControl } from '@/api/zm/device/setting'

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
      handler(n) {
        if (n) {
          this.queryParams.deviceId = n
          if (n !== -1) this.getList()
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
      this.queryParams.params = {}
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params['beginCreateTime'] =
          this.daterangeCreateTime[0]
        this.queryParams.params['endCreateTime'] = this.daterangeCreateTime[1]
      }
      acSwitchControlList(this.queryParams).then((response) => {
        this.demoList = response.rows
        this.total = response.total

      }).finally(() => {
        this.loading = false
      })
    },
    /** 自定义分页查询 */
    getPage() {
      this.loading = true
      this.queryParams.params = {}
      if (null != this.daterangeCreateTime && '' != this.daterangeCreateTime) {
        this.queryParams.params['beginCreateTime'] =
          this.daterangeCreateTime[0]
        this.queryParams.params['endCreateTime'] = this.daterangeCreateTime[1]
      }
      pageDemo(this.queryParams).then((response) => {
        this.demoList = response.rows
        this.total = response.total
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
    },
    save() {
      this.$confirm('确定保存吗？', {
        title: '警告',
        type: 'warning'
      }).then(res => {
        console.log(this.demoList)
        return updateAcControl(this.demoList)
      }).then(res => {
        this.$message.success(res.msg || "操作成功！")
      }).catch(e => {
        console.error('操作失败:', e)
        this.$message.error('操作失败，请重试')
      })
    }
  }
}
</script>
