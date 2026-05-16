<template>
  <div class="ChangJinSheZhi">
    <div>
      <CommonContainer
        title="场景选择"
        :contentPadding="0"
        :borderWidth="0"
        style="flex: 1; overflow: hidden"
      >
        <Group ref="groupRef" @change="handleChange"/>

        <!--        <div style="text-align: center">-->
        <!--          <el-button-->
        <!--            :disabled="!currentRenameItem.id"-->
        <!--            @click="rename"-->
        <!--            style="margin-top: 20px; width: 100px"-->
        <!--            round-->
        <!--            type="primary"-->
        <!--          >场景命名-->
        <!--          </el-button-->
        <!--          >-->
        <!--        </div>-->
      </CommonContainer>

    </div>
    <div>
      <CommonContainer
        title="场景参数"
        :contentPadding="0"
        :borderWidth="0"
        style="flex: 1; overflow: hidden"
      >
        <Params ref="params"/>
        <!--        <div style="text-align: center">-->
        <!--          <el-button-->
        <!--            @click="save"-->
        <!--            style="margin-top: 20px; width: 100px"-->
        <!--            round-->
        <!--            type="primary"-->
        <!--          >保存-->
        <!--          </el-button>-->
        <!--        </div>-->
        <template #titleRight>
          <el-button
            @click="save"
            style="width: 100px"
            round
            size="small"
          >保存
          </el-button>
        </template>
      </CommonContainer>

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
import CommonContainer from '@/components/CommonContainer/index.vue'
import Group from './group/index.vue'
import Params from './params/index.vue'
import PowerConsumption from '@/views/zm/device/record/components/PowerConsumption/index.vue'
import { getSceneNames, updateSceneParams } from '@/api/zm/device/setting'

export default {
  components: {
    PowerConsumption,
    CommonContainer,
    Group,
    Params
  },
  data() {
    return {
      value: '',
      deviceId: undefined,
      leftData: [],
      rightData: [],
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

  methods: {
    handleChange(sceneId, deviceId) {
      if (sceneId && deviceId) {
        // this.renameForm.sceneId = sceneId
        this.currentRenameItem = this.$refs.groupRef.currentSelected
        console.log('sceneId ==> ', sceneId, 'this.deviceId ==> ', deviceId)
        this.$refs.params && this.$refs.params.getList(deviceId, sceneId)
      } else {
        // this.renameForm.sceneId = undefined
        this.currentRenameItem = {}
        this.$refs.params && this.$refs.params.clearList()
      }
    },
    save() {
      if (!this.$refs) return this.$message.error('无法进行此操作！')
      if (!this.$refs.params.demoList.length) return this.$message.error('无法进行此操作！')
      console.log(this.$refs.params.demoList)
      this.$confirm('确认保存设置吗？',
        {
          type: 'warning',
          title: '警告'
        }).then(res => {
        const form = {}
        form['deviceId'] = this.$refs.groupRef.queryParams.deviceId
        form['sceneId'] = this.$refs.groupRef.currentSelected.id
        form['name'] = this.currentRenameItem.name
        form['selectGroupIdArr'] = []
        form['selectLuxArr'] = []
        form['selectSwitchArr'] = []
        this.$refs.params.demoList.forEach(item => {
          if(item.selectStatus){
            form.selectGroupIdArr.push(item.groupId)
          }
          form.selectLuxArr.push(Number(item.lux))
          form.selectSwitchArr.push(item.btnStatus)
        })
        return updateSceneParams(form)
      }).then(response => {
        this.$message.success(response.msg || '操作成功')
      }).catch(err => {
        console.log(err)
      })
    },
    rename() {
      console.log(this.$refs.groupRef.currentSelected)
      if (!this.$refs.groupRef.currentSelected.id) return this.$message.warning('请先选择场景，再执行此操作！')

      this.openRename = true
      this.currentRenameItemName = this.$refs.groupRef.currentSelected.name
      // this.$nextTick(() => {
      //   this.$refs.renameForm.resetFields()
      // })
    },
    submitRenameForm() {
      if (!this.currentRenameItemName) return this.$message.warning('请先选择一个名称！')
      this.currentRenameItem.name = this.currentRenameItemName
      this.openRename = false
    },

    visibleChange(e) {
      if (e) {
        this.loadingList = true
        getSceneNames().then(res => {
          console.log(res)
          this.list = res.data
        }).finally(() => {
          this.loadingList = false
        })
      }
    }
  },

  mounted() {

  }
}
</script>
<style lang="scss" scoped>
.ChangJinSheZhi {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }
}

@media screen and (max-width: 1120px) {
  .ChangJinSheZhi {
    grid-template-columns: 1fr;
  }
}
</style>
