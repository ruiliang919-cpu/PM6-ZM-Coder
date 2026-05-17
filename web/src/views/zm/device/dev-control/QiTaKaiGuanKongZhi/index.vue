<template>
  <div class=" table">
    <CommonContainer
      title="其他开关控制"
      :contentPadding="0"
      :borderWidth="0"
      style="flex: 1; overflow: hidden"
    >
      <div style=" height: 2px; width: 100%"></div>
      <el-row :gutter="20">
        <el-col :span="12">
          <Table :demo-list="demoList1" :loading="loading" :device-id="queryParams.deviceId"/>
        </el-col>
        <el-col :span="12">
          <Table :demo-list="demoList2" :loading="loading" :device-id="queryParams.deviceId"/>
        </el-col>
      </el-row>
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
      <div style="height: 50px; width: 100%"></div>
      <div style="display: flex; justify-content: center;height: 45px" v-if="demoList.length">
        <el-button type="primary" @click="openAllSwitch" style="width: 120px">全部闭合</el-button>
        <el-button type="danger" @click="closeAllSwitch" style="width: 120px">全部断开</el-button>
      </div>
      <div style="height: 50px; width: 100%"></div>
    </CommonContainer>
  </div>
</template>

<script>
import { otherSwitchAllOff, otherSwitchAllOn, otherSwitchGetTable } from '@/api/zm/device/dev-control'
import Table from './components/Table.vue'
import CommonContainer from '@/components/CommonContainer/index.vue'

export default {
  components: {
    CommonContainer,
    Table
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      demoList1: [],
      demoList2: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        deviceId: -1
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
      handler(n) {
        console.log('navibarDeviceValue ========>', n)
        if (n) {
          this.queryParams.deviceId = n
          this.queryParams.pageNum = 1
          this.getList()
          this.startTimer()
        } else {
          this.loading = false
        }
      },
      immediate: true
    }
  },
  created() {
    // this.getList()
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
      otherSwitchGetTable(this.queryParams).then((response) => {
        this.demoList = response.rows || []
        this.demoList1 = []
        this.demoList2 = []
        this.demoList.forEach((item, index) => {
          if (index % 2 === 0) {
            this.demoList1.push(item)
          } else {
            this.demoList2.push(item)
          }
        })

        this.total = response.total || 0
        this.loading = false
      })
    },
    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this._get()
      }, 5000)
    },
    openAllSwitch() {
      this.$confirm(
        '确认全部闭合吗？',
        {
          title: `警告`,
          type: 'warning'
        }
      ).then(async() => {
        return otherSwitchAllOn({
          deviceId: this.queryParams.deviceId
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功')
        this.$emit('success')
      }).catch(e => {
        console.error('操作失败:', e)
        this.$message.error('操作失败，请重试')
      })
    },
    closeAllSwitch() {
      this.$confirm(
        '确认全部断开吗？',
        {
          title: `警告`,
          type: 'warning'
        }
      ).then(async() => {
        return otherSwitchAllOff({
          deviceId: this.queryParams.deviceId
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功')
        this.$emit('success')
      }).catch(e => {
        console.error('操作失败:', e)
        this.$message.error('操作失败，请重试')
      })
    }

  }
}
</script>
