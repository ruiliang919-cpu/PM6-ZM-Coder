<template>
  <el-radio-group v-model="checked" @change="handleChange" style="width: 100%" v-loading="loading">
    <div style="height: 470px;overflow: hidden; overflow-y: auto; border: #2280ec 1px solid">
      <Item @rename="handleRename" v-for="item in demoList" :data="item" :key="item.groupId"></Item>

      <template v-if="!demoList.length">
        <!--        <div style="border: 1px solid #2280ec;border-top: 0">-->
        <div>
          <el-empty description="无数据"></el-empty>
        </div>
      </template>

    </div>
    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div style="height: 50px"></div>

    <el-dialog title="分组重命名" :visible.sync="openRename" width="600px" append-to-body>
      <el-form ref="renameForm" :rules="renameFormRules" :model="currentRenameItem">
        <el-form-item prop="name">
<!--          {{ currentRenameItemName }}-->
          <!--          <el-input v-model="renameForm.name" placeholder="请输入名称"/>-->
          <el-select :loading="loadingList" placeholder="请选择名称" v-model="currentRenameItemName"
                     @visible-change="visibleChange"
                     @change="currentRenameItemName = $event"
                     style="width: 100%"
          >
            <el-option v-for="item in list" :key="item.name" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="renameFormOptions.loading" round type="primary" @click="submitRenameForm" @keydown.enter="submitRenameForm">确 定</el-button>
        <el-button @click="openRename = false" round>取 消</el-button>
      </div>
    </el-dialog>
  </el-radio-group>
</template>
<script>
import Item from './item.vue'
import { configGroupList, getDistrictNames, groupName } from '@/api/zm/device/setting'

export default {
  props: {
    data: {
      type: Array,
      default() {
        return []
      }
    }
  },
  components: {
    Item
  },
  data() {
    return {
      checked: '',
      // 遮罩层
      loading: true,
      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        deviceId: undefined
      },
      currentSelected: {},
      openRename: false,
      currentRenameItemName: '',
      currentRenameItem: {},
      renameFormOptions: {
        loading: false
      },
      renameFormRules: {
        name: [
          {
            required: true,
            message: '名称不能为空！',
            trigger: 'blur'
          }
        ]
      },
      loadingList: false,
      list: [],
      isRename: false
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
          this.getList()
        }
      },
      immediate: true
    }
  },
  methods: {
    getList(flag = false) {
      this.loading = true
      configGroupList(this.queryParams).then((response) => {
        this.demoList = response.rows
        // this.demoList = [1, 2, 3]
        this.total = response.total
        if (this.total && !flag) {
          this.checked = this.demoList[0].groupId
          this.currentSelected = this.demoList[0]
          this.handleChange(this.checked)
        }
        this.loading = false
      })
    },
    handleChange(e) {
      this.$emit('change', e)
      this.currentSelected = this.demoList.find(item => item.groupId === e)
    },
    handleRename(data) {
      this.openRename = true
      this.currentRenameItem = data
      this.currentRenameItemName = data.name
    },
    visibleChange(e) {
      if (this.list.length) return
      if (e) {
        this.loadingList = true
        getDistrictNames().then(res => {
          console.log(res)
          this.list = res.data
        }).finally(() => {
          this.loadingList = false
        })
      }
    },
    submitRenameForm() {
      if (!this.currentRenameItemName) return this.$message.warning('请先选择一个名称！')
      if (isNaN(this.currentRenameItemName)) return this.$message.warning('请先选择其他的名称！')
      const item = this.list.find(item => item.id === this.currentRenameItemName)
      if(!item) return this.$message.error("出错了！")
      this.renameFormOptions.loading = true
      groupName({
        deviceId: this.queryParams.deviceId,
        groupId: this.currentRenameItem.groupId,
        id: this.currentRenameItemName,
        name: item.name
      }).then(res => {
        this.currentRenameItem.name = item.name
        this.$message.success(res.msg || '操作成功')
        this.openRename = false
        this.$emit('change', this.currentRenameItem.groupId)
      }).finally(() => {
        this.renameFormOptions.loading = false
      })
      //this.openRename = false
    }
  },
  mounted() {
    // this.getList()
  }
}
</script>
<style></style>
