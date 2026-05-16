<template>
  <div v-loading="loading">
    <div v-if="baseSceneList.length">
      <el-radio-group v-loading="loading" v-model="checked" style="width: 100%"
                      @change="handleChange($event,queryParams.deviceId)"
      >
        <Item @rename="handleRename" v-for="(item,index) in baseSceneList" :data="item" :key="item.id"
        />
        <!--    <pagination-->
        <!--      v-show="total>0"-->
        <!--      :total="total"-->
        <!--      :page.sync="queryParams.pageNum"-->
        <!--      :limit.sync="queryParams.pageSize"-->
        <!--      @pagination="getList"-->
        <!--    />-->
      </el-radio-group>
    </div>
    <div>
      <template v-if="!baseSceneList.length">
        <div style="border: #2280ec 1px solid; flex: 1">
          <el-empty description="无数据"></el-empty>
        </div>
      </template>
    </div>

    <el-dialog title="场景重命名" :visible.sync="openRename" width="600px" append-to-body>
      <el-form ref="renameForm" :rules="renameFormRules" :model="currentRenameItem">
        <el-form-item prop="name">
          <!--          <el-input v-model="renameForm.name" placeholder="请输入名称"/>-->
          <el-select :loading="loadingList" placeholder="请选择名称" v-model="currentRenameItemName"
                     @visible-change="visibleChange"
                     @change="currentRenameItemName = $event"
                     style="width: 100%"
          >
            <el-option v-for="item in list" :key="item.name" :label="item.name" :value="item.name"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="renameFormOptions.loading" round type="primary" @click="submitRenameForm" @keydown.enter="submitRenameForm">确 定</el-button>
        <el-button @click="openRename = false" round>取 消</el-button>
      </div>
    </el-dialog>
  </div>

</template>
<script>
import Item from './item.vue'
import { getSceneSelectList, listBaseScene } from '@/api/zm/baseScene'
import { getDistrictNames, getSceneNames, groupName, sceneName } from '@/api/zm/device/setting'

export default {
  // props:{
  //   data:{
  //     type:Array,
  //     default(){
  //       return []
  //     }
  //   }
  // },
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
  components: {
    Item
  },
  data() {
    return {
      checked: undefined,
      // 总条数
      total: 0,
      baseSceneList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceId: undefined,
        name: undefined
      },
      loading: false,
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
      list: []
    }
  },
  methods: {
    /** 查询场景列表 */
    getList() {
      this.loading = true
      getSceneSelectList(this.queryParams).then(response => {
        this.baseSceneList = response.rows || response.data || []
        this.total = response.total || 0
        if (this.baseSceneList.length) {
          this.checked = this.baseSceneList[0].id
          this.currentSelected = this.baseSceneList[0]
          this.handleChange(this.baseSceneList[0].id, this.queryParams.deviceId)
        } else {
          this.handleChange(undefined, undefined)
        }
      }).finally(() => {
        this.loading = false
      })
    },
    handleChange(sceneId, deviceId) {
      console.log(sceneId, deviceId)
      this.currentSelected = this.baseSceneList.find(item => item.id === sceneId)
      this.$emit('change', sceneId, deviceId)
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
        getSceneNames().then(res => {
          console.log(res)
          this.list = res.data
        }).finally(() => {
          this.loadingList = false
        })
      }
    },
    submitRenameForm() {
      if (!this.currentRenameItemName) return this.$message.warning('请先选择一个名称！')

      //this.openRename = false
      this.renameFormOptions.loading = true
      sceneName({
        deviceId: this.queryParams.deviceId,
        sceneId: this.currentRenameItem.id,
        name: this.currentRenameItemName
      }).then(res => {
        this.currentRenameItem.name = this.currentRenameItemName
        this.$message.success(res.msg || '操作成功')
        this.openRename = false
      }).finally(() => {
        this.renameFormOptions.loading = false
      })
    }

  },
  mounted() {

  }
}
</script>
<style lang="scss">
.no-bottom-border {
  border-width: 0 !important;
}
</style>
