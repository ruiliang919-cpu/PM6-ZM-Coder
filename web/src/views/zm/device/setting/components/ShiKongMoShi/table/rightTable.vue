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
        <template #default="scope"> #{{ scope.row.timeFrameId }}</template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="">
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
      <el-table-column label="结束时间" align="center" prop="">
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
      <el-table-column label="场景" align="center" prop="">
        <template v-slot="scope">
          <el-select :placeholder="null" v-model="scope.row.sceneSelect" @change="handleChange(scope.row)">
            <el-option v-for="item in selectList" :key="item.sceneId + item.name" :label="item.name" :value="item.sceneId"/>
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="使能" align="center" prop="" width="60">
        <template v-slot="scope">
          <el-checkbox :value="!!scope.row.enabledStatus"
                       @change="$event?scope.row.enabledStatus = 1:scope.row.enabledStatus = 0"
          />
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      layout="prev, next"
      prev-text="上一页"
      next-text="下一页"
      @pagination="getList"
    />
    <div style="height: 50px; width: 100%"></div>
  </div>
</template>

<script>
import { timeControlSceneList, acSwitchControlList, getSceneList, getModuleSceneList } from '@/api/zm/device/setting'
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
      selectList: [],
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
      getModuleSceneList({
        deviceId
      }).then(response => {
        this.selectList = response.data

        return timeControlSceneList(this.queryParams)
      }).then((response) => {
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
        padding: '20px 0'
        // border:"1px solid #2280ec !important"
      }
    },
    handleEnabledStatus(e, row) {
      console.log(e)
      e ? row.enabledStatus = 1 : row.enabledStatus = 0
    },
    setList(list) {
      this.demoList = list
    },
    clearList() {
      this.demoList = []
      this.total = 0
    },
    handleChange(e) {
      // console.log(this.selectList)
      const index = this.selectList.findIndex(item => item.sceneId === e.sceneSelect)
      // console.log(index)
      if (index !== -1) {
        e.sceneName = this.selectList[index].name
      }
    }
  }
}
</script>
