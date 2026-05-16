<script>
//分区组合计算
import CommonContainer from '@/components/CommonContainer/index.vue'
import PowerConsumption from '@/views/zm/history/powerCalculation/components/PowerConsumptionCopy/index.vue'
import GroupLeftCopy from '@/views/zm/history/powerCalculation/components/GroupLeftCopy/index.vue'
import GroupLeft from '@/views/zm/history/powerCalculation/components/GroupLeft/index.vue'
import GroupRightCopy from '@/views/zm/history/powerCalculation/components/GroupRightCopy/index.vue'
import GroupRight from '@/views/zm/history/powerCalculation/components/GroupRight/index.vue'
import { calculationZoneSet, lightZoneSet } from '@/api/zm/history/powerCalculation'

export default {
  components: { PowerConsumption, CommonContainer, GroupLeft, GroupRight, GroupRightCopy, GroupLeftCopy },
  data() {
    return {
      open: false,
      open1: false,
      title: '',
      title1: '',
      loading: false,
      loading1: false
    }
  },
  methods: {
    openSetting() {
      this.open = true
    },
    openSetting1() {
      this.open1 = true
    },
    save() {
      this.loading = true
      calculationZoneSet({
        ...this.$refs.groupLeft.currentSelected,
        zones: this.$refs.groupRight.checkedData
      }).then((response) => {
        this.$message.success(response.msg || '操作成功')
        this.open = false
      }).finally(() => {
        this.loading = false
      })
    },
    save1() {
      this.loading1 = true
      lightZoneSet({
        ...this.$refs.groupLeft.currentSelected,
        zones: this.$refs.groupRight.checkedData
      }).then((response) => {
        this.$message.success(response.msg || '操作成功')
        this.open1 = false
      }).finally(() => {
        this.loading1 = false
      })
    },
    handleChange(e) {
      if (e) {
        let checkedDate = this.$refs.groupLeft.currentSelected.zoneList.map(item => Number(item))
        console.log(checkedDate)
        this.$refs.groupRight.setCheckedData(checkedDate)
      } else {
        this.$refs.groupRight.clearCheckDate()
      }
    }
  }
}
</script>

<template>
  <div>
    <el-form>
      <el-form-item>
        <el-button @click="openSetting" type="primary" round>分区组合设置</el-button>
<!--        <el-button @click="openSetting1" type="primary" round>分区控制组合设置</el-button>-->
      </el-form-item>
    </el-form>
    <div class="FenQuZuHeJiSuan">
      <CommonContainer
        title="年耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="4"/>
      </CommonContainer>
      <CommonContainer
        title="季耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="3"/>
      </CommonContainer>
      <CommonContainer
        title="月耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="2"/>
      </CommonContainer>
      <CommonContainer
        title="周耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="1"/>
      </CommonContainer>
      <CommonContainer
        title="日耗电量"
        :contentPadding="0"
        :borderWidth="0"
        class="CommonContainer"
      >
        <PowerConsumption :type="0"/>
      </CommonContainer>
    </div>
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>

      <el-row :gutter="20">
        <el-col :span="12">
          <CommonContainer
            title="分区组合选择"
            :contentPadding="0"
            :borderWidth="1"
            class="CommonContainer"
          >
            <GroupLeft ref="groupLeft" @change="handleChange" v-if="open"/>
          </CommonContainer>
        </el-col>
        <el-col :span="12">
          <CommonContainer
            title="分区选择"
            :contentPadding="0"
            :borderWidth="1"
            class="CommonContainer"
          >
            <GroupRight ref="groupRight" v-if="open"/>
          </CommonContainer>
        </el-col>
        <el-col :span="24">
          <div style="text-align: center;margin-top: 20px">
            <el-button type="primary" @click="save" round :loading="loading">
              保存
            </el-button>
          </div>
        </el-col>
      </el-row>


    </el-dialog>
    <el-dialog :title="title1" :visible.sync="open1" width="600px" append-to-body>

      <el-row :gutter="20">
        <el-col :span="12">
          <CommonContainer
            title="分区组合选择"
            :contentPadding="0"
            :borderWidth="1"
            class="CommonContainer"
          >
            <GroupLeftCopy ref="groupLeft" @change="handleChange" v-if="open1"/>
          </CommonContainer>
        </el-col>
        <el-col :span="12">
          <CommonContainer
            title="分区选择"
            :contentPadding="0"
            :borderWidth="1"
            class="CommonContainer"
          >
            <GroupRightCopy ref="groupRight" v-if="open1"/>
          </CommonContainer>
        </el-col>
        <el-col :span="24">
          <div style="text-align: center;margin-top: 20px">
            <el-button type="primary" @click="save1" round :loading="loading1">
              保存
            </el-button>
          </div>
        </el-col>
      </el-row>


    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.FenQuZuHeJiSuan {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;

  .CommonContainer {
    width: 100%;
    overflow: hidden;
  }

  @media screen and (max-width: 1150px) {
    .FenQuZuHeJiSuan {
      grid-template-columns: 1fr 1fr;
    }

  }

  @media screen and (max-width: 750px) {
    .FenQuZuHeJiSuan {
      grid-template-columns: 1fr;
    }
  }
}
</style>
