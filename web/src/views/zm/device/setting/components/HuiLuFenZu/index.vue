<template>
  <div>
    <div class="HuiLuFenZu">
      <div>
        <CommonContainer
          title="分组选择"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <Group @change="handleGroupChange" ref="groupRef"/>
        </CommonContainer>
      </div>
      <div>
        <CommonContainer
          title="分组配置"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <el-row style="margin-top: 10px" type="flex">
            <el-col :span="10">
              <CommonContainer
                title="已分组回路"
                :contentPadding="0"
                :borderWidth="1"
                style="flex: 1; overflow: hidden"
              >
                <LightGroup ref="LeftLightGroupRef" :data="leftData" v-loading="loading0"/>
              </CommonContainer>
            </el-col>
            <el-col :span="4" style="flex: 1">
              <div
                style="
                width: 100%;
                height: 100%;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
              "
              >
                <div>
                  <el-button
                    @click="addItem"
                    style="margin: 0"
                    round
                    type="primary"
                    size="small"
                  >&lt;&lt;添加
                  </el-button
                  >
                </div>
                <div style="height: 10px"></div>
                <div>
                  <el-button
                    @click="deleteItem"
                    style="margin: 0"
                    round
                    type="warning"
                    size="small"
                  >删除&gt;&gt;
                  </el-button
                  >
                </div>
              </div>
            </el-col>
            <el-col :span="10">
              <CommonContainer
                title="待分组回路"
                :contentPadding="0"
                :borderWidth="1"
                style="flex: 1; overflow: hidden"
              >
                <LightGroup
                  ref="RightLightGroupRef"
                  rightSide
                  :data="rightData"
                  v-loading="loading1"
                />
              </CommonContainer>

            </el-col>
          </el-row>
        </CommonContainer>
      </div>
    </div>
    <div style="display: flex; justify-content: center; margin-top: 20px">
      <!--        {{ this.selectGroupId }}-->
      <!--      <div style="text-align: center">-->
      <!--        <el-button-->
      <!--          :disabled="!currentRenameItem.id"-->
      <!--          @click="rename"-->
      <!--          style="margin-top: 20px; width: 100px"-->
      <!--          round-->
      <!--          type="primary"-->
      <!--        >场景命名-->
      <!--        </el-button-->
      <!--        >-->
      <!--      </div>-->
      <SelectForm @rename="rename" :current-rename-item-id="currentRenameItem.id" v-loading="isGetDistrictByGroupId"
                  v-model="zoneId" @save="updateLoop" :updating="updating"
                  v-if="queryParams.slaveId"
      />
    </div>

    <el-dialog title="分组重命名" :visible.sync="openRename" width="600px" append-to-body>
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
import {
  getLoopsByGroup,
  getLoopsByFree,
  getDistrictByGroupId,
  updateLoop,
  getSceneNames, getDistrictNames
} from '@/api/zm/device/setting'
import CommonContainer from '@/components/CommonContainer/index.vue'
import Group from './Group/index.vue'
import LightGroup from './LightGroup/index.vue'
import SelectForm from './SelectForm'

export default {
  components: {
    CommonContainer,
    Group,
    LightGroup,
    SelectForm
  },
  data() {
    return {
      value: undefined,
      leftData: [],
      rightData: [],
      loading0: false,
      loading1: false,
      queryParams: {
        slaveId: -1,
        groupId: -1
      },
      zoneId: undefined,
      updating: false,
      isGetDistrictByGroupId: false,
      openRename: false,
      currentRenameItem: {},
      currentRenameItemName: '',
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
          this.queryParams.slaveId = n
          // if (n !== -1 && this.queryParams.groupId !== -1) this.getLoops()
        }
      },
      immediate: true

    }
  },
  methods: {
    handleGroupChange(e) {
      console.log('handleGroupChange', e, this.queryParams.slaveId)
      this.queryParams.groupId = e
      this.currentRenameItem = this.$refs.groupRef.currentSelected
      if (this.queryParams.slaveId !== -1) {
        this.getLoops()
        this.isGetDistrictByGroupId = true
        getDistrictByGroupId({
          groupId: this.queryParams.groupId,
          deviceId: this.queryParams.slaveId
        }).then(res => {
          this.zoneId = res.data.id
        }).finally(() => {
          this.isGetDistrictByGroupId = false
        })
      }
    },
    getLoops() {
      this.getLoopsByGroup()
      this.getLoopsByFree()
    },
    getLoopsByGroup() {
      this.loading0 = true
      getLoopsByGroup(this.queryParams).then(res => {
        this.leftData = res.data
        const ref = this.$refs.LeftLightGroupRef
        if (!ref) return
        ref.clearCheckedData()
      }).finally(() => {
        this.loading0 = false
      })
    },
    getLoopsByFree() {
      this.loading1 = true
      getLoopsByFree(this.queryParams).then(res => {
        this.rightData = res.data
        const ref = this.$refs.RightLightGroupRef
        if (!ref) return
        ref.clearCheckedData()
      }).finally(() => {
        this.loading1 = false
      })
    },
    addItem() {
      const ref = this.$refs.RightLightGroupRef
      if (!ref) return
      const data = ref.checkedData
      data.forEach(item => {
        const index = this.rightData.findIndex((item1) => item1.no === item)
        if (index !== -1) {
          this.leftData.push(this.rightData[index])
          this.rightData.splice(index, 1)
        }
      })
      this.leftData = this.leftData.sort((a, b) => a.no - b.no)
      ref.clearCheckedData()
    },
    deleteItem() {
      const ref = this.$refs.LeftLightGroupRef
      console.log(ref)

      if (!ref) return

      const data = ref.checkedData
      data.forEach((item) => {
        const index = this.leftData.findIndex((item1) => item1.no === item)
        if (index !== -1) {
          this.rightData.push(this.leftData[index])
          this.leftData.splice(index, 1)
        }
      })
      this.rightData = this.rightData.sort((a, b) => a.no - b.no)
      ref.clearCheckedData()
      console.log(this.leftData)
    },

    updateLoop() {
      console.log('save1')
      const form = {}
      form['groupName'] = this.currentRenameItem?.name
      form['zoneId'] = this.zoneId
      form['deviceId'] = this.queryParams.slaveId
      form['groupId'] = this.queryParams.groupId
      form['loopNo'] = this.leftData.map(item => item.no)

      let flag = true

      for (const key in form) {
        if (Array.isArray(form[key])) {
          continue
        }
        if (form[key] === null || form[key] === undefined) {
          flag = false
          if (key === 'deviceId') {
            this.$message.warning('请选择设备')
          } else if (key === 'groupId') {
            this.$message.warning('请选择分组')
          } else if (key === 'zoneId') {
            this.$message.warning('请选择分区')
          }
          return
        }
      }

      if (flag) {

        this.$confirm('确认保存吗？', {
          type: 'warning',
          title: '警告'
        }).then(res => {
          this.updating = true
          return updateLoop(form)
        }).then(res => {
          this.$message.success(res.msg || '操作成功')
          this.$refs.groupRef.getList(true)
        }).finally(() => {
          this.updating = false
        })
      }
    },
    visibleChange(e) {
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
      this.currentRenameItem.name = this.currentRenameItemName
      this.openRename = false
    },
    rename() {
      console.log(this.$refs.groupRef.currentSelected)
      if (!this.$refs.groupRef.currentSelected.id) return this.$message.warning('请先选择场景，再执行此操作！')

      this.openRename = true
      this.currentRenameItemName = this.$refs.groupRef.currentSelected.name
      // this.$nextTick(() => {
      //   this.$refs.renameForm.resetFields()
      // })
    }
  },

  mounted() {
    // for (let index = 0; index < 16; index++) {
    //   this.leftData.push(index);
    // }
  }
}
</script>
<style lang="scss" scoped>
.HuiLuFenZu {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

@media screen and (max-width: 1180px) {
  .HuiLuFenZu {
    grid-template-columns: 1fr;
  }
}
</style>
