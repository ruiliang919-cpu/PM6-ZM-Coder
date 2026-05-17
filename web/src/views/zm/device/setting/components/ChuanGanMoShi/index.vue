<template>
  <div>
    <div class="ChuanGanMoShi">
      <div>
        <CommonContainer
          title="红外传感器"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <div style="height: 22px"></div>
          <SelectForm @change="handleLeftTableChange" :type="0" @save="handleSaveLeft"
                      :enabled="leftEnabled"
          />

          <el-row :gutter="10">
            <el-col :span="16">
              <CommonContainer
                title="照明参数"
                :contentPadding="0"
                :borderWidth="0"
                style="flex: 1; overflow: hidden"
              >
                <LeftTable ref="leftTable" @enabled="handleLeftEnabled"/>
              </CommonContainer>
            </el-col>
            <el-col :span="8">
              <CommonContainer
                title="分组选择"
                :contentPadding="0"
                :borderWidth="0"
                style="flex: 1; overflow: hidden"
              >
                <Group ref="leftGroup"/>
              </CommonContainer>
            </el-col>
          </el-row>
        </CommonContainer>
      </div>
      <div>
        <CommonContainer
          title="照度传感器"
          :contentPadding="0"
          :borderWidth="0"
          style="flex: 1; overflow: hidden"
        >
          <div style="height: 22px"></div>

          <SelectForm :type="1" @change="handleRightTable" @save="handleSaveRight" :enabled="rightEnabled"/>
          <el-row :gutter="10">
            <el-col :span="16">
              <CommonContainer
                title="照明参数"
                :contentPadding="0"
                :borderWidth="0"
                style="flex: 1; overflow: hidden"
              >
                <RightTable ref="rightTable" @enabled="handleRightEnabled"/>
              </CommonContainer>
            </el-col>
            <el-col :span="8">
              <CommonContainer
                title="分组选择"
                :contentPadding="0"
                :borderWidth="0"
                style="flex: 1; overflow: hidden"
              >
                <Group ref="rightGroup"/>
              </CommonContainer>
            </el-col>
          </el-row>
        </CommonContainer>
      </div>
    </div>
  </div>
</template>
<script>
import CommonContainer from '@/components/CommonContainer/index.vue'
import Group from './group/index.vue'
import LeftTable from './table/leftTable.vue'
import RightTable from './table/rightTable.vue'
import SelectForm from '@/views/zm/device/setting/components/ChuanGanMoShi/SelectForm/index.vue'
import { updateIlluminanceParams, updateInfraredParams } from '@/api/zm/device/setting'

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
      queryParams: {
        deviceId: undefined,
        sensorId: undefined
      },
      //this.$emit('enabled', res.data.enabled)
      leftEnabled: { enabled: false },
      rightEnabled: { enabled: false }
    }
  },
  methods: {
    handleLeftEnabled(e) {
      this.leftEnabled = { enabled: !!e }
    },
    handleRightEnabled(e) {
      console.log('handleRightEnabled', e)
      this.rightEnabled = { enabled: !!e }
    },
    submitFileForm() {
    },
    handleParamsRef() {
      this.open = true
    },
    handleLeftTableChange(deviceId, sensorId) {
      console.log('deviceId ==> ', deviceId, 'sensorId ==> ', sensorId)
      if (deviceId && sensorId) {
        this.$refs.leftTable.getData(deviceId, sensorId)
        this.$refs.leftGroup.setData(0, deviceId, sensorId)
        this.$refs.leftGroup.getList()
      } else {
        this.$refs.leftTable.clearData()
        this.$refs.leftGroup.clearList()
      }
    },
    handleRightTable(deviceId, sensorId) {
      console.log('deviceId ==> ', deviceId, 'sensorId ==> ', sensorId)
      if (deviceId && sensorId) {
        this.$refs.rightTable.getData(deviceId, sensorId)
        this.$refs.rightGroup.setData(1, deviceId, sensorId)
        this.$refs.rightGroup.getList()
      } else {
        this.$refs.rightTable.clearData()
        this.$refs.rightGroup.clearList()
      }
    },
    handleSaveLeft(deviceId, sensorId, enabled) {
      this.$confirm('是否保存红外传感器参数？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        const form = {}
        form['deviceId'] = deviceId
        form['sensorId'] = sensorId
        form['enabled'] = enabled
        form['groupIds'] = this.$refs.leftGroup.demoList.filter(item => item.selectStatus === 1).map(item => item.groupId)
        form['table'] = this.$refs.leftTable.data
        return updateInfraredParams(form)
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).catch(err => {
        console.error('操作失败:', err)
        this.$message.error('操作失败，请重试')
      })
    },
    handleSaveRight(deviceId, sensorId, enabled) {
      this.$confirm('是否保存照度传感器参数？', {
        type: 'warning',
        title: '警告'
      }).then(res => {
        const form = {}
        form['deviceId'] = deviceId
        form['sensorId'] = sensorId
        form['enabled'] = enabled
        form['groupIds'] = this.$refs.rightGroup.demoList.filter(item => item.selectStatus === 1).map(item => item.groupId)
        form['table'] = Object.assign(this.$refs.rightTable.data, {
          illuminanceLux: !this.$refs.rightTable.data.outControlStatus ? this.$refs.rightTable.out : this.$refs.rightTable.data.illuminanceLux
        })
        return updateIlluminanceParams(form)
      }).then(res => {
        this.$message.success(res.msg || '操作成功！')
      }).catch(err => {
        console.error('操作失败:', err)
        this.$message.error('操作失败，请重试')
      })
    }
  },

  mounted() {
  }
}
</script>
<style lang="scss" scoped>
.ChuanGanMoShi {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;

  & > div {
    width: 100%;
    overflow: hidden;
  }

}

@media screen and (max-width: 1260px) {
  .ChuanGanMoShi {
    grid-template-columns:1fr;
  }
}
</style>
