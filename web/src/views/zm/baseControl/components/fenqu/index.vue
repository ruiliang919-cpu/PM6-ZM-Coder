<template>
  <div class="table">

    <div style="height: 20px"></div>
    <div class="table_container">
      <div class="table_item">
        <div>
          <el-table
            v-loading="loading"
            :data="leftList"
            :cell-class-name="handleRowStyle"
            :header-cell-style="handleHeaderRowStyle"
          >
            <!-- <el-table-column type="selection" width="55" align="center" /> -->
            <el-table-column label="分区名称" align="center" prop="name"/>
            <el-table-column label="亮度" align="center">
              <template v-slot="scope">
                <el-input type="number" :max="100" min="0" v-model="scope.row.lux"/>
              </template>
            </el-table-column>
            <el-table-column label="状态" align="center">
              <template v-slot="scope">
                <el-tag round :type="!scope.row.switchStatus ? 'success' : 'danger'">
                  {{ !scope.row.switchStatus ? '开' : '关' }}
                </el-tag>
              </template>

            </el-table-column>
            <el-table-column label="开关" align="center">
              <template v-slot="scope">
                <el-button round type="success" icon="el-icon-open" @click="handleSwitchStatus(scope.row,true)"
                           style="width: 100px;"
                >
                  开
                </el-button>
                <div style="height: 10px"></div>
                <el-button round type="danger" icon="el-icon-turn-off" @click="handleSwitchStatus(scope.row,false)"
                           style="width: 100px;"
                >
                  关
                </el-button>
              </template>

            </el-table-column>
            <el-table-column label="调光" align="center">
              <template v-slot="scope">
                <el-button round type="primary" @click="handleLux(scope.row)" style="width: 100px"
                >调光
                </el-button>
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
        </div>
      </div>
      <div class="table_item" v-show="rightList.length">
        <div>
          <el-table
            v-loading="loading"
            :data="rightList"
            :cell-class-name="handleRowStyle"
            :header-cell-style="handleHeaderRowStyle"
          >
            <!-- <el-table-column type="selection" width="55" align="center" /> -->
            <el-table-column label="分区名称" align="center" prop="name"/>
            <el-table-column label="亮度" align="center">
              <template v-slot="scope">
                <el-input type="number" :max="100" min="0" v-model="scope.row.lux"/>
              </template>
            </el-table-column>
            <el-table-column label="状态" align="center">
              <template v-slot="scope">
                <el-tag round :type="!scope.row.switchStatus ? 'success' : 'danger'">
                  {{ !scope.row.switchStatus ? '开' : '关' }}
                </el-tag>
              </template>

            </el-table-column>
            <el-table-column label="开关" align="center">
              <template v-slot="scope">
                <el-button round type="success" icon="el-icon-open" @click="handleSwitchStatus(scope.row,true)"
                           style="width: 100px;"
                >
                  开
                </el-button>
                <div style="height: 10px"></div>
                <el-button round type="danger" icon="el-icon-turn-off" @click="handleSwitchStatus(scope.row,false)"
                           style="width: 100px;"
                >
                  关
                </el-button>
              </template>

            </el-table-column>
            <el-table-column label="调光" align="center">
              <template v-slot="scope">
                <el-button round type="primary" @click="handleLux(scope.row)" style="width: 100px"
                >调光
                </el-button>
              </template>
            </el-table-column>
          </el-table>


        </div>
      </div>
    </div>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listDemo } from '@/api/demo/demo'
import {
  clearZoneStatus,
  getZoneStatus,
  updateZoneLightLux,
  updateZoneLightSwitch,
  zoneControlList
} from '@/api/zm/baseControl'
import GroupRightCopy from '@/views/zm/history/powerCalculation/components/GroupRightCopy/index.vue'
import CommonContainer from '@/components/CommonContainer/index.vue'
import GroupLeftCopy from '@/views/zm/history/powerCalculation/components/GroupLeftCopy/index.vue'
import { lightZoneSet } from '@/api/zm/history/powerCalculation'

export default {
  components: { GroupLeftCopy, CommonContainer, GroupRightCopy },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      leftList: [],
      rightList: [],
      currentIndex: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        testKey: undefined,
        value: undefined,
        createTime: undefined
      },
      timer: null,
      flag: false

    }
  },
  mounted() {
    this.getList()
    this.getFlag()
    this.startTimer()
  },
  methods: {

    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this.getFlag()
      }, 5000)
    },
    getFlag() {
      getZoneStatus()
        .then(res => {
          if (res.data) {
            clearZoneStatus().then(res => {
              this._get()
            }).catch(err => {
            })
          }
        })
    },
    /** 查询测试单表列表 */
    getList() {
      this.loading = true
      this._get()
    },
    _get() {
      zoneControlList(this.queryParams).then((response) => {
        this.demoList = response.rows || []
        const leftList = []
        const rightList = []
        this.demoList.map((item, index) => {
          if (index % 2 === 0) {
            leftList.push(item)
          } else {
            rightList.push(item)
          }
        })
        this.leftList = leftList
        this.rightList = rightList
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
    handleChange(index) {
      this.currentIndex = index
    },
    handleSwitchStatus(row, flag) {
      this.$confirm(!flag ? '确认关闭吗？' : '确认开启吗?', {
        title: row.name,
        type: 'warning'
      }).then(res => {
        return updateZoneLightSwitch({
          zoneId: row.orderNo,
          swStatus: flag ? 0 : 1
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
        this.getList()
      }).catch(err => {
        console.log(err)
      })

    },
    handleLux(row) {
      this.$confirm('确认调光吗？', {
        title: row.name,
        type: 'warning'
      }).then(res => {
        return updateZoneLightLux({
          zoneId: row.orderNo,
          lux: row.lux
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
        this.getList()
      })
    }
  },
  beforeDestroy() {
    clearInterval(this.timer)
  }
}
</script>
<style lang="scss" scoped>
.table_container {
  width: 100%;
  overflow: hidden;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;

  .table_item {
    overflow: hidden;
    width: 100%;
    height: 100%;
  }
}

@media screen and (max-width: 1080px) {
  .table_container {
    grid-template-columns: 1fr;
  }
}
</style>
