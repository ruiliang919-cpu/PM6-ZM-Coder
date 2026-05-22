<template>
  <div v-loading="loading">
    <div style="height: 10px; width: 100%"></div>

    <div v-if="form && Object.values(form).length && Object.values(form).every(item => !!item)" class="muxian">
      <el-row>
        <el-col :span="12">
          <div class="item">
            <div class="item_title_box">母线电压:</div>
            <div class="item_content_box">
              <el-tag type="" effect="plain" style="width: 100px;text-align: center;">{{ form.busVoltage }}</el-tag>
            </div>
            <div class="unit">V</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="item">
            <div class="item_title_box">母线电流:</div>
            <div class="item_content_box">
              <el-tag type="" effect="plain" style="width: 100px;text-align: center;">{{ form.busElectricity }}</el-tag>
            </div>
            <div class="unit">A</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="item">
            <div class="item_title_box">母线正对地电压:</div>
            <div class="item_content_box">
              <el-tag type="" effect="plain" style="width: 100px;text-align: center;">{{
                  form.positivePoleResistance
                }}
              </el-tag>
            </div>
            <div class="unit">V</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="item">
            <div class="item_title_box">环境温度:</div>
            <div class="item_content_box">
              <el-tag type="" effect="plain" style="width: 100px;text-align: center;">{{ form.temperature }}</el-tag>
            </div>
            <div class="unit">℃</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="item">
            <div class="item_title_box">母线负对地电压:</div>
            <div class="item_content_box">
              <el-tag type="" effect="plain" style="width: 100px;text-align: center;">{{
                  form.negativePoleResistance
                }}
              </el-tag>
            </div>
            <div class="unit">V</div>
          </div>
        </el-col>
      </el-row>
    </div>
    <div v-else style="border: 1px solid #2280ec;">
      <el-empty description="配电柜无此数据"></el-empty>
    </div>
    <div style="height: 25px; width: 100%"></div>
  </div>
</template>
<script>
import { busInfo } from '@/api/zm/device/dev'
import polling from '@/mixins/polling'

export default {
  mixins: [polling],
  data() {
    return {
      loading: false,
      queryParams: {
        slaveId: -1
      },
      /**
       * {
       *         // 直流母线电压
       *         'busVoltage': '0.00',
       *         // 直流母线电流
       *         'busElectricity': '0.00',
       *         // 母线正对地电压
       *         'positivePoleResistance': '0.00',
       *         // 母线负对地电流
       *         'negativePoleResistance': '0.00',
       *         // 环境温度
       *         'temperature': '0.00'
       *       }
       */
      form: {}
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
          this.$stopPolling()
          this.queryParams.slaveId = n
          this.getList()
          this.$startPolling()
        }
      },
      immediate: true
    }
  },
  methods: {
    getList() {
      this.loading = true
      this._get()
    },
    pollingFetch() {
      this._get()
    },
    _get() {
      busInfo(this.queryParams)
        .then(res => {
          this.form = res.data
        })
        .catch((error) => {
          console.error('获取数据失败:', error)
        })
        .finally(() => {
          this.loading = false
        })
    }
  }
}
</script>
<style scoped lang="scss">
.muxian {
  border: 1px solid #b9bcb7;
  border-radius: 5px;
  background-color: #fff;
  padding: 50px;

  .item {
    padding: 10px;
    display: flex;
    align-items: center;

    &_title_box {
      width: 150px;
      text-align: right;
      padding-right: 10px;
    }

    .unit {
      padding: 0 10px;
    }
  }
}
</style>
