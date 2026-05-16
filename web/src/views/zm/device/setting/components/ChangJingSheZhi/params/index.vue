<template>
  <div v-loading="loading">
    <template v-if="demoList.length">
      <el-table
        :show-header="false"
        v-loading="loading"
        :data="demoList"
        :cell-class-name="handleRowStyle"
        :header-cell-style="handleHeaderRowStyle"
      >
        <el-table-column width="55" align="center">
          <template v-slot="scope">
            <el-checkbox :value="!!scope.row.selectStatus"
                         @change="$event?scope.row.selectStatus = 1:scope.row.selectStatus = 0"
            />
          </template>
        </el-table-column>
        <el-table-column label="" align="center">
          <template v-slot="scope">
            <el-tooltip effect="dark" :content="scope.row.groupName" placement="top">
              <div style="height: 30px;line-height: 30px;overflow: hidden;text-overflow:ellipsis;white-space:nowrap">{{
                  scope.row.groupName
                }}
              </div>
            </el-tooltip>

          </template>
        </el-table-column>
        <el-table-column label="" align="center">
          <template v-slot="scope">
            <div style="display: flex; align-items: center;">
              <div style="width: 80px;">亮度</div>
              <el-input type="number" :min="0" v-model="scope.row.lux"/>
            </div>
          </template>

        </el-table-column>
        <el-table-column label="" align="center">
          <template v-slot="scope">
            <el-switch active-text="" inactive-text="开关"
                       :value="!!scope.row.btnStatus"
                       @change="$event?scope.row.btnStatus=1:scope.row.btnStatus=0"
            />
          </template>

        </el-table-column>
      </el-table>
      <!-- <pagination
    v-show="total > 0"
    :total="total"
    :page.sync="queryParams.pageNum"
    :limit.sync="queryParams.pageSize"
    @pagination="getList"
  /> -->
    </template>
    <template v-if="!demoList.length">
      <div style="border: #2280ec 1px solid; flex: 1">
        <el-empty description="无数据"></el-empty>
      </div>
    </template>
  </div>
</template>

<script>
import { getSceneParamsList } from '@/api/zm/device/setting'

export default {
  data() {
    return {
      // 遮罩层
      loading: false,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      currentIndex: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      checkedData: []
    }
  },

  created() {
    // this.getList()
  },
  methods: {
    /** 查询测试单表列表 */
    getList(deviceId = undefined, sceneId = undefined) {
      this.checkedData = []
      this.loading = true
      getSceneParamsList({
        ...this.queryParams,
        deviceId,
        sceneId
      }).then((response) => {
        this.demoList = response.rows || []
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
        display: 'none',
        backgroundColor: '#2280ec',
        color: '#fff',
        fontSize: '16px',
        padding: '15px 0'
        // border:"1px solid #2280ec !important"
      }
    },
    handleChange(index) {
      this.currentIndex = index
    },
    clearList() {
      this.demoList = []
      this.checkedData = []
      this.total = 0
    }
  }
}
</script>
