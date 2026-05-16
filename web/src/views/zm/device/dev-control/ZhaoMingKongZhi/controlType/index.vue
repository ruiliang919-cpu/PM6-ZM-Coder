<script>
import {
  getModuleNow,
  selectHandModule,
  selectIlluminanceModule,
  selectInfraredModule,
  selectTimeModule
} from '@/api/zm/device/dev-control'

export default {
  name: 'index',
  props: {
    deviceId: {
      type: [String, Number],
      default: ''
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
          this.getData()
        }
      },
      immediate: true
    }
  },
  data() {
    return {
      loading: false,
      open01: false,
      open02: false,
      rules: {
        timeModule: [
          {
            required: true,
            trigger: 'blur',
            message: '请先选中一个模式'
          }
        ],
        handModule: [
          {
            required: true,
            trigger: 'blur',
            message: '请先选中一个模式'
          }
        ]
      },
      queryParams: {
        deviceId: undefined
      },
      form: {
        handModule: '',
        illuminanceSensorModule: false,
        infraredSensorModule: false,
        timeModule: ''
      },
      timer: null
    }
  },
  methods: {
    submit01() {
      this.$refs.form01.validate(valid => {
        if (valid) {
          this.loading = true
          selectTimeModule({
            deviceId: this.queryParams.deviceId,
            timeModule: this.form.timeModule
          }).then(response => {
            this.$message.success(response.msg || '操作成功')
            this.open01 = false
            setTimeout(() => {
              this.getData()
            }, 3000)
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    submit02() {
      this.$refs.form02.validate(valid => {
        if (valid) {
          this.loading = true
          selectHandModule({
            deviceId: this.queryParams.deviceId,
            handModule: this.form.handModule
          }).then(response => {
            this.$message.success(response.msg || '操作成功')
            this.open02 = false
            setTimeout(() => {
              this.getData()
            }, 3000)
          }).finally(() => {
            this.loading = false
          })
        }
      })
    },
    getData() {
      this.loading = true
      this._get()
    },
    _get() {
      if (this.open01 || this.open02)  return
      getModuleNow({
        deviceId: this.queryParams.deviceId
      }).then(res => {
        console.log(res)
        this.form = res.data
      }).finally(() => {
        this.loading = false
        this.startTimer()
      })
    },
    startTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this._get()
      }, 5000)
    },
    handleChange01(val) {
      this.loading = true
      selectInfraredModule({
        deviceId: this.queryParams.deviceId,
        enabled: val
      }).then(response => {
        this.$message.success(response.msg || '操作成功')
        // this.getData()
      }).finally(() => {
        this.loading = false
      })
    },
    handleChange02(val) {
      this.loading = true
      selectIlluminanceModule({
        deviceId: this.queryParams.deviceId,
        enabled: val
      }).then(response => {
        this.$message.success(response.msg || '操作成功')
        // this.getData()
      }).finally(() => {
        this.loading = false
      })
    }

  },
  beforeDestroy() {
    clearInterval(this.timer)
  }
}
</script>

<template>
  <div>
    <div style="border: #2280ec 1px solid; padding: 10px;" v-loading="loading">
      <el-form>
        <el-form-item>
          <el-button type="primary" round @click="open01 = true">时控模式选择</el-button>
          <el-button type="primary" round @click="open02 = true">手动模式选择</el-button>
        </el-form-item>
        <el-form-item>
          <!--          // 手动模式选择 loop 回路控制/ group 分组控制 / scene 场景控制-->
          当前时控模式：
          <el-tag v-if="form.timeModule === 'noEnabled'">未开启时控模式</el-tag>
          <el-tag v-if="form.timeModule === 'simple'">普通时控模式</el-tag>
          <el-tag v-if="form.timeModule === 'scene'">场景时控模式</el-tag>

        </el-form-item>
        <el-form-item>
          <!--    // 时控模式 noEnabled 未开启时控模式 / simple 普通时控模式 / scene 场景时控模式      -->
          当前手动模式：
          <el-tag v-if="form.handModule === 'loop'">回路控制</el-tag>
          <el-tag v-if="form.handModule === 'group'">分组控制</el-tag>
          <el-tag v-if="form.handModule === 'scene'">场景控制</el-tag>
        </el-form-item>

        <el-form-item>
          <el-switch inactive-text="红外传感模式" style="margin-right: 10px" v-model="form.infraredSensorModule"
                     @change="handleChange01"
          />
          <el-switch inactive-text="照度传感模式" v-model="form.illuminanceSensorModule" @change="handleChange02"/>
        </el-form-item>
      </el-form>
    </div>

    <el-dialog title="时控模式选择" :visible.sync="open01" width="600px" append-to-body>
      <el-form ref="form01" :rules="rules" :model="{timeModule :form.timeModule}">
        <el-form-item prop="timeModule">
          <el-radio-group v-model="form.timeModule">
            <el-radio label="noEnabled" value="noEnabled">
              未开启时控模式
            </el-radio>
            <el-radio label="simple" value="simple">
              普通时控模式
            </el-radio>
            <el-radio label="scene" value="scene">
              场景时控模式
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="loading" round type="primary" @click="submit01" @keydown.enter="submit01">确 定</el-button>
        <el-button @click="open01 = false" round>取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="手动模式选择" :visible.sync="open02" width="600px" append-to-body>
      <el-form ref="form02" :rules="rules" :model="{handModule : form.handModule}">
        <el-form-item prop="handModule">
          <el-radio-group v-model="form.handModule">
            <el-radio label="loop" value="loop">
              回路控制
            </el-radio>
            <el-radio label="group" value="group">
              分组控制
            </el-radio>
            <el-radio label="scene" value="scene">
              场景控制
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="loading" round type="primary" @click="submit02" @keydown.enter="submit02">确 定</el-button>
        <el-button @click="open02 = false" round>取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">

</style>
