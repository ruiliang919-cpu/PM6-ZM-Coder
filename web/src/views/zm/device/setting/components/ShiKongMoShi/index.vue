<template>
  <div>
    <div class="ShiKongMoShi">
      <div>
        <CommonContainer
          title="普通时控模式参数"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <div style="height: 22px"></div>

          <SelectForm ref="leftSelectForm" :type="0" @change="handleLeftChange" @save="handleSaveLeft" @oneKeySetting="oneKeySettingLeft"/>
          <div class="PuTongShiKongMoShi">
            <div>
              <CommonContainer
                title="时序表"
                :contentPadding="0"
                :borderWidth="0"
                style="flex: 1; overflow: hidden"
              >
                <template v-slot:titleRight>
                  <el-button
                    @click="handleParamsRef(0)"
                    round
                    style="padding: 8px 20px"
                  >参数引用
                  </el-button
                  >
                </template>
                <LeftTable ref="leftTable"/>
              </CommonContainer>
            </div>
            <div>
              <Group ref="leftGroup"/>
            </div>
          </div>
        </CommonContainer>
      </div>
      <div>
        <CommonContainer
          title="场景时控模式参数"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <div style="height: 22px"></div>
          <SelectForm ref="rightSelectForm" :type="1" @change="handleRightChange" @save="handleSaveRight"
                      @oneKeySetting="oneKeySettingRight"
          />
          <CommonContainer
            title="时序表"
            :contentPadding="0"
            :borderWidth="0"
            style="flex: 1; overflow: hidden"
          >
            <template v-slot:titleRight>
              <el-button
                @click="handleParamsRef(1)"
                round
                style="padding: 8px 20px"
              >参数引用
              </el-button
              >
            </template>
            <RightTable ref="rightTable"/>
          </CommonContainer>
        </CommonContainer>
      </div>
    </div>
    <el-dialog :title="title" :visible.sync="open" width="300px" append-to-body>
      <el-form ref="paramsRefFrom" :rules="rules" :model="paramsRefForm">
        <el-form-item v-loading="loadingCabinetList" prop="deviceId">
          <el-select v-model="paramsRefForm.deviceId" @change="handleParamsRefFormChange" style="width: 100%">
            <el-option v-for="item in cabinetList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item v-loading="loadingDemoList" v-if="paramsRefForm.deviceId" prop="controlId">
          <el-select v-model="paramsRefForm.controlId" style="width: 100%">
            <el-option v-for="item in demoList" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="getSimpleTimeScale" :loading="loadingGetSimpleTimeScale" @keydown.enter="loadingGetSimpleTimeScale">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import Group from './group/index.vue'
import LeftTable from './table/leftTable.vue'
import RightTable from './table/rightTable.vue'
import SelectForm from './SelectForm'
import {
  getCabinetList, sceneList,
  sceneListSelect, simpleList,
  simpleListSelect,
  simpleTimeScale, updateSceneControl, updateSceneControlToAll,
  updateSimpleControl, updateSimpleControlToAll
} from '@/api/zm/device/setting'

export default {
  components: {
    CommonContainer,
    LeftTable,
    RightTable,
    Group,
    SelectForm
  },
  data() {
    return {
      value: '',
      leftData: [],
      rightData: [],
      open: false,
      title: '参数引用',
      paramsRefForm: {
        deviceId: undefined,
        controlId: undefined
      },
      cabinetList: [],
      demoList: [],
      loadingDemoList: false,
      loadingCabinetList: false,
      currentType: undefined,
      loadingGetSimpleTimeScale: false,
      rules: {
        deviceId: [{
          required: true,
          trigger: 'blur',
          message: '请选择'
        }],
        controlId: [{
          required: true,
          trigger: 'blur',
          message: '请选择'
        }]
      }
    }
  },
  methods: {
    submitFileForm() {
    },
    handleParamsRef(type) {
      this.open = true
      this.paramsRefForm.deviceId = undefined
      this.paramsRefForm.controlId = undefined
      this.demoList = []
      this.currentType = type
      if (type === 0) {
        this.title = '普通时控参数引用'
      } else {
        this.title = '场景时控参数引用'
      }
      if (!this.cabinetList.length) {
        this.getCabinetList()
      }
    },
    getCabinetList() {
      this.loadingCabinetList = true
      getCabinetList({}).then(res => {
        this.cabinetList = res.data
      }).finally(() => {
        this.loadingCabinetList = false
      })
    },
    handleLeftChange(deviceId, controlId) {
      console.log(deviceId, controlId)
      if (deviceId && controlId) {
        this.$refs.leftTable && this.$refs.leftTable.getList(deviceId, controlId)
        if (this.$refs.leftGroup) {
          this.$refs.leftGroup.reset()
          this.$refs.leftGroup.getData(deviceId, controlId)
        }
      } else {
        this.$refs.leftTable && this.$refs.leftTable.clearList()
        this.$refs.leftGroup && this.$refs.leftGroup.clearList()
      }
    },
    handleRightChange(deviceId, controlId) {
      if (deviceId && controlId) {
        this.$refs.rightTable && this.$refs.rightTable.getList(deviceId, controlId)
      } else {
        this.$refs.rightTable && this.$refs.rightTable.clearList()
      }
    },

    handleParamsRefFormChange() {
      this.loadingDemoList = true
      if (this.currentType === 0) {
        simpleListSelect({
          deviceId: this.paramsRefForm.deviceId
        }).then(res => {
          this.demoList = res.data
        }).finally(() => {
          this.loadingDemoList = false
        })
      } else {
        sceneListSelect({
          deviceId: this.paramsRefForm.deviceId
        }).then(res => {
          this.demoList = res.data
        }).finally(() => {
          this.loadingDemoList = false
        })
      }
    },
    getSimpleTimeScale() {
      this.loadingGetSimpleTimeScale = true
      this.$refs['paramsRefFrom'].validate((valid) => {
        if (valid) {
          // alert('submit!')
          if (this.currentType === 0) {
            simpleList(this.paramsRefForm).then(res => {
              this.$refs.leftTable && this.$refs.leftTable.setList(res.data || res.rows)
              this.$refs.leftSelectForm.getEnabled2(
                this.paramsRefForm.deviceId,this.paramsRefForm.controlId
              )
              this.$refs.leftGroup.getData(
                this.paramsRefForm.deviceId, this.paramsRefForm.controlId
              )
              this.open = false
            }).finally(() => {
              this.loadingGetSimpleTimeScale = false
            })
          } else {
            sceneList(this.paramsRefForm).then(res => {
              this.$refs.rightTable && this.$refs.rightTable.setList(res.data || res.rows)
              this.$refs.rightSelectForm.getEnabled2(
                this.paramsRefForm.deviceId,this.paramsRefForm.controlId
              )
              this.open = false
            }).finally(() => {
              this.loadingGetSimpleTimeScale = false
            })
          }
          // simpleTimeScale(this.paramsRefForm).then(res => {
          //   if (this.currentType === 0) {
          //     this.$refs.leftTable && this.$refs.leftTable.setList(res.data || res.rows)
          //   } else {
          //     this.$refs.rightTable && this.$refs.rightTable.setList(res.data || res.rows)
          //   }
          //   this.open = false
          // }).finally(() => {
          //   this.loadingGetSimpleTimeScale = false
          // })
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },
    handleSaveLeft(deviceId, controlId, enabled) {
      console.log(...arguments)
      this.$confirm('是否保存普通时控模式参数？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        const form = {}
        form['deviceId'] = deviceId
        form['controlId'] = controlId
        form['enabled'] = enabled
        form['groupIds'] = this.$refs.leftGroup.demoList.filter(item => item.selectStatus === 1).map(item => item.groupId)
        form['table'] = this.$refs.leftTable.demoList
        return updateSimpleControl(form).then(res => {
          this.$message.success(res.msg || '操作成功！')
        }).catch(err => {
          console.error('操作失败:', err)
          this.$message.error('操作失败，请重试')
        })
      })
    },
    handleSaveRight(deviceId, controlId, enabled) {
      this.$confirm('是否保存场景时控模式参数？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        const form = {}
        form['deviceId'] = deviceId
        form['controlId'] = controlId
        form['enabled'] = enabled
        form['table'] = this.$refs.rightTable.demoList
        return updateSceneControl(form).then(res => {
          this.$message.success(res.msg || '操作成功！')
        }).catch(err => {
          console.error('操作失败:', err)
          this.$message.error('操作失败，请重试')
        })
      })
    },
    oneKeySettingLeft(deviceId, controlId, enabled) {
      this.$confirm('是否一键设置普通时控模式参数？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        const form = {}
        form['deviceId'] = deviceId
        form['controlId'] = controlId
        form['enabled'] = enabled
        form['groupIds'] = this.$refs.leftGroup.demoList.filter(item => item.selectStatus === 1).map(item => item.groupId)
        form['table'] = this.$refs.leftTable.demoList
        return updateSimpleControlToAll(form).then(res => {
          this.$message.success(res.msg || '操作成功！')
        }).catch(err => {
          console.error('操作失败:', err)
          this.$message.error('操作失败，请重试')
        })
      })
    },
    oneKeySettingRight(deviceId, controlId, enabled) {
      this.$confirm('是否一键设置场景时控模式参数？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        const form = {}
        form['deviceId'] = deviceId
        form['controlId'] = controlId
        form['enabled'] = enabled
        form['table'] = this.$refs.rightTable.demoList
        return updateSceneControlToAll(form).then(res => {
          this.$message.success(res.msg || '操作成功！')
        }).catch(err => {
          console.error('操作失败:', err)
          this.$message.error('操作失败，请重试')
        })
      })
    }
  },

  mounted() {
  }
}
</script>
<style lang="scss" scoped>
.ShiKongMoShi {
  display: grid;
  grid-template-columns: 1.4fr 1fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }

  .PuTongShiKongMoShi {
    display: grid;
    grid-template-columns: 3fr 1fr;
    gap: 10px;

    & > div {
      width: 100%;
      overflow: hidden;
    }
  }

  @media screen and (max-width: 780px) {
    .PuTongShiKongMoShi {
      grid-template-columns:1fr;
    }
  }
}

@media screen and (max-width: 1260px) {
  .ShiKongMoShi {
    grid-template-columns:1fr;
  }
}
</style>
