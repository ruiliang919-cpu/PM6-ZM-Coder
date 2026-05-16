<template>
  <div class="app-container">
    <!-- <el-form
      :model="queryParams"
      ref="queryForm"
      size="small"
      :inline="true"
      v-show="showSearch"
      label-width="150px"
    >
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备通讯编号" prop="deviceNo">
        <el-input
          v-model="queryParams.deviceNo"
          placeholder="请输入设备通讯编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备所在区域" prop="regionId">
        <el-input
          v-model="queryParams.regionId"
          placeholder="请输入设备所在区域"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="运行模式" prop="runMode">
        <el-input
          v-model="queryParams.runMode"
          placeholder="请输入运行模式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="直流模块个数" prop="dcModuleNum">
        <el-input
          v-model="queryParams.dcModuleNum"
          placeholder="请输入直流模块个数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="调光回路数" prop="dimmerNum">
        <el-input
          v-model="queryParams.dimmerNum"
          placeholder="请输入调光回路数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="母线正极绝缘阻值" prop="positivePoleResistance">
        <el-input
          v-model="queryParams.positivePoleResistance"
          placeholder="请输入母线正极绝缘阻值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="母线负极绝缘阻值" prop="negativePoleResistance">
        <el-input
          v-model="queryParams.negativePoleResistance"
          placeholder="请输入母线负极绝缘阻值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="母线交窜直电压" prop="busbarCrossoverVoltage">
        <el-input
          v-model="queryParams.busbarCrossoverVoltage"
          placeholder="请输入母线交窜直电压"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="母线交窜直电流" prop="busbarCrossoverCurrent">
        <el-input
          v-model="queryParams.busbarCrossoverCurrent"
          placeholder="请输入母线交窜直电流"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最后一次心跳时间" prop="lastTime">
        <el-date-picker
          clearable
          v-model="queryParams.lastTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择最后一次心跳时间"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button
          type="primary"
          icon="el-icon-search"
          size="mini"
          @click="handleQuery"
          >搜索</el-button
        >
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery"
          >重置</el-button
        >
      </el-form-item>
    </el-form> -->

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          size="mini"
          plain
          icon="el-icon-plus"
          @click="handleAdd"
          v-hasPermi="['zm:baseDevice:add']"
        >新增
        </el-button
        >
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="recoverIssued"
          :loading="sendingDirective"
        >迁移设备数据
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-edit"
          size="mini"
          @click="recoverRead"
          :loading="sendingDirective"
        >读取设备最新数据
        </el-button>
      </el-col>
      <!-- <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['zm:baseDevice:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zm:baseDevice:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zm:baseDevice:export']"
        >导出</el-button>
      </el-col> -->
      <!-- <right-toolbar
        :showSearch.sync="showSearch"
        @queryTable="getList"
      ></right-toolbar> -->
    </el-row>

    <el-table
      :cell-class-name="handleRowStyle"
      :header-cell-style="handleHeaderRowStyle"
      v-loading="loading"
      :data="baseDeviceList"
      @selection-change="handleSelectionChange"
    >
      <!-- <el-table-column type="selection" width="55" align="center" /> -->
      <!--      <el-table-column label="编号" align="center" prop="id" v-if="true"/>-->
      <el-table-column label="序号" align="center" prop="orderNo"/>
      <el-table-column label="机柜名称" align="center" prop="name"/>
      <!-- <el-table-column label="设备通讯编号" align="center" prop="deviceNo" /> -->
      <el-table-column label="所属区域" align="center" prop="area"/>
      <el-table-column label="ip" align="center" prop="ip"/>
      <el-table-column label="port" align="center" prop="port"/>
      <el-table-column label="主机号" align="center" prop="deviceId"/>
      <!-- <el-table-column label="通信状态" align="center" prop="onlineStatus" /> -->
      <!-- <el-table-column label="设备状态" align="center" prop="deviceStatus" /> -->
      <!-- <el-table-column label="运行开关" align="center" prop="runStatus" /> -->
      <el-table-column label="运行模式" align="center" prop="runMode">
        <template v-slot="scope">
          <el-tag v-if="scope.row.runMode== 0">自动</el-tag>
          <el-tag type="warning" v-else-if="scope.row.runMode == 1">手动</el-tag>
        </template>
      </el-table-column>
      <!--      <el-table-column label="运行模式类型" align="center" prop="runModeType">-->
      <!--        &lt;!&ndash; 1，普通时控；2，场景时控；3，红外传感；4，照度传感； &ndash;&gt;-->
      <!--        <template #default="scope">-->
      <!--          <el-tag v-if="scope.row.runModeType == 1">普通时控</el-tag>-->
      <!--          <el-tag v-else-if="scope.row.runModeType == 2">场景时控</el-tag>-->
      <!--          <el-tag v-else-if="scope.row.runModeType == 3">红外传感</el-tag>-->
      <!--          <el-tag v-else-if="scope.row.runModeType == 4">照度传感</el-tag>-->
      <!--        </template>-->
      <!--      </el-table-column>-->
      <el-table-column label="直流模块(个)" align="center" prop="dcModuleNum"/>
      <el-table-column label="调光回路数(个)" align="center" prop="dimmerNum"/>
      <!-- <el-table-column label="母线正极绝缘阻值" align="center" prop="positivePoleResistance" /> -->
      <!-- <el-table-column label="母线负极绝缘阻值" align="center" prop="negativePoleResistance" /> -->
      <!-- <el-table-column label="母线交窜直电压" align="center" prop="busbarCrossoverVoltage" /> -->
      <!-- <el-table-column label="母线交窜直电流" align="center" prop="busbarCrossoverCurrent" /> -->
      <!-- <el-table-column label="最后一次心跳时间" align="center" prop="lastTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column> -->
      <el-table-column
        width="200"
        label="操作"
        align="center"
        class-name="small-padding fixed-width"
        fixed="right"
      >
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleInit(scope.row)"
          >初始化
          </el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zm:baseDevice:edit']"
          >修改
          </el-button
          >
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zm:baseDevice:remove']"
          >删除
          </el-button
          >
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改机柜对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="150px">
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入设备名称"/>
        </el-form-item>
        <!-- <el-form-item label="设备通讯编号" prop="deviceNo">
          <el-input v-model="form.deviceNo" placeholder="请输入设备通讯编号" />
        </el-form-item> -->
        <el-form-item label="设备所在区域" prop="regionId">
          <!--          <el-input v-model="form.regionId" placeholder="请输入设备所在区域"/>-->
          <el-select v-model="form.regionId" @visible-change="getNoPageList">
            <el-option v-for="item in noPageList" :key="item.id" :value="item.id" :label="item.name"/>
          </el-select>
        </el-form-item>
        <el-form-item label="ip" prop="ip">
          <el-input v-model="form.ip" placeholder="请输入ip地址"/>
        </el-form-item>
        <el-form-item label="port" prop="port">
          <el-input v-model="form.port" placeholder="请输入port"/>
        </el-form-item>
        <el-form-item label="主机号" prop="deviceId">
          <el-input v-model="form.deviceId" placeholder="请输入主机号"/>
        </el-form-item>
        <!--        <el-form-item label="运行模式" prop="runMode">-->
        <!--          &lt;!&ndash;          <el-input v-model="form.runMode" placeholder="请输入运行模式" />&ndash;&gt;-->
        <!--          <el-select v-model="form.runMode" placeholder="请选择运行模式">-->
        <!--            <el-option :value="0" label="自动"/>-->
        <!--            <el-option :value="1" label="手动"/>-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="类型" prop="type">-->
        <!--          &lt;!&ndash;          <el-input v-model="form.runMode" placeholder="请输入运行模式" />&ndash;&gt;-->
        <!--          <el-select v-model="form.type" placeholder="请选择类型">-->
        <!--            <el-option :value="1" label="电源柜"/>-->
        <!--            <el-option :value="0" label="配电柜"/>-->
        <!--          </el-select>-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="直流模块个数" prop="dcModuleNum">-->
        <!--          <el-input-->
        <!--            v-model="form.dcModuleNum"-->
        <!--            placeholder="请输入直流模块个数"-->
        <!--          />-->
        <!--        </el-form-item>-->
        <!--        <el-form-item label="调光回路数" prop="dimmerNum">-->
        <!--          <el-input v-model="form.dimmerNum" placeholder="请输入调光回路数"/>-->
        <!--        </el-form-item>-->

        <!-- <el-form-item label="母线正极绝缘阻值" prop="positivePoleResistance">
          <el-input
            v-model="form.positivePoleResistance"
            placeholder="请输入母线正极绝缘阻值"
          />
        </el-form-item>
        <el-form-item label="母线负极绝缘阻值" prop="negativePoleResistance">
          <el-input
            v-model="form.negativePoleResistance"
            placeholder="请输入母线负极绝缘阻值"
          />
        </el-form-item>
        <el-form-item label="母线交窜直电压" prop="busbarCrossoverVoltage">
          <el-input
            v-model="form.busbarCrossoverVoltage"
            placeholder="请输入母线交窜直电压"
          />
        </el-form-item>
        <el-form-item label="母线交窜直电流" prop="busbarCrossoverCurrent">
          <el-input
            v-model="form.busbarCrossoverCurrent"
            placeholder="请输入母线交窜直电流"
          />
        </el-form-item>
        <el-form-item label="最后一次心跳时间" prop="lastTime">
          <el-date-picker
            clearable
            v-model="form.lastTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="请选择最后一次心跳时间"
          >
          </el-date-picker>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button
          round
          :loading="buttonLoading"
          type="primary"
          @click="submitForm"
          @keydown.enter="submitForm"
        >确 定
        </el-button
        >
        <el-button round @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listBaseDevice,
  getBaseDevice,
  delBaseDevice,
  addBaseDevice,
  updateBaseDevice, noPageList, baseDeviceInit, recoverIssued, recoverRead, recoverPassword
} from '@/api/zm/baseDevice'

export default {
  name: 'BaseDevice',
  data() {
    return {
      // 按钮loading
      buttonLoading: false,
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 机柜表格数据
      baseDeviceList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      noPageList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceName: undefined,
        deviceNo: undefined,
        regionId: undefined,
        onlineStatus: undefined,
        deviceStatus: undefined,
        runStatus: undefined,
        runMode: undefined,
        runModeType: undefined,
        dcModuleNum: undefined,
        dimmerNum: undefined,
        positivePoleResistance: undefined,
        negativePoleResistance: undefined,
        busbarCrossoverVoltage: undefined,
        busbarCrossoverCurrent: undefined,
        lastTime: undefined
      },
      // 表单参数
      form: {
        runMode: 0
      },
      // 表单校验
      rules: {
        id: [{ required: true, message: '系统编号不能为空', trigger: 'blur' }],
        type: [{ required: true, message: '机柜类型不能为空', trigger: 'blur' }],
        ip: [{ required: true, message: 'ip不能为空', trigger: 'blur' }],
        port: [{ required: true, message: 'port不能为空', trigger: 'blur' }],
        deviceId: [{ required: true, message: '主机号不能为空', trigger: 'blur' }],
        name: [
          { required: true, message: '设备名称不能为空', trigger: 'blur' }
        ],
        deviceNo: [
          { required: true, message: '设备通讯编号不能为空', trigger: 'blur' }
        ],
        regionId: [
          { required: true, message: '设备所在区域不能为空', trigger: 'blur' }
        ],
        onlineStatus: [
          { required: true, message: '通信状态不能为空', trigger: 'change' }
        ],
        deviceStatus: [
          { required: true, message: '设备状态不能为空', trigger: 'change' }
        ],
        runStatus: [
          { required: true, message: '运行开关不能为空', trigger: 'change' }
        ],
        runMode: [
          { required: true, message: '运行模式不能为空', trigger: 'blur' }
        ],
        runModeType: [
          {
            required: true,
            message:
              '运行模式类型1，普通时控；2，场景时控；3，红外传感；4，照度传感；不能为空',
            trigger: 'change'
          }
        ],
        dcModuleNum: [
          { required: true, message: '直流模块个数不能为空', trigger: 'blur' }
        ],
        dimmerNum: [
          { required: true, message: '调光回路数不能为空', trigger: 'blur' }
        ],
        positivePoleResistance: [
          {
            required: true,
            message: '母线正极绝缘阻值不能为空',
            trigger: 'blur'
          }
        ],
        negativePoleResistance: [
          {
            required: true,
            message: '母线负极绝缘阻值不能为空',
            trigger: 'blur'
          }
        ],
        busbarCrossoverVoltage: [
          {
            required: true,
            message: '母线交窜直电压不能为空',
            trigger: 'blur'
          }
        ],
        busbarCrossoverCurrent: [
          {
            required: true,
            message: '母线交窜直电流不能为空',
            trigger: 'blur'
          }
        ],
        lastTime: [
          {
            required: true,
            message: '最后一次心跳时间不能为空',
            trigger: 'blur'
          }
        ]
      },
      sendingDirective: false
    }
  },
  created() {
    this._noPageList()
    this.getList()
  },
  methods: {
    recoverRead() {
      this.$prompt(
        '请输入密码',
        {
          title: '提示',
          type: 'warning'
        }
      ).then(res => {
        console.log(res)
        if (!res.value) {
          this.$message.error('请输入正确的密码！')
          return Promise.reject('请输入正确的密码！')
        }
        this.sendingDirective = true
        return recoverPassword({
          password: res.value
        })
      }).then(res => {
        if (!res.data) {
          this.$message.error('请输入正确的密码！')
          return Promise.reject('请输入正确的密码！')
        }
        return recoverRead()
      }).then(response => {
        this.$message.success(response.msg || '操作成功')
      }).catch(err => {
        console.log(err)
      }).finally(() => {
        this.sendingDirective = false
      })
    },
    recoverIssued() {
      this.$prompt(
        '请输入密码',
        {
          title: '提示',
          type: 'warning'
        }
      ).then(res => {
        console.log(res)
        if (!res.value) {
          this.$message.error('请输入正确的密码！')
          return Promise.reject('请输入正确的密码！')
        }
        this.sendingDirective = true
        return recoverPassword({
          password: res.value
        })
      }).then(res => {
        if (!res.data) {
          this.$message.error('请输入正确的密码！')
          return Promise.reject('请输入正确的密码！')
        }
        return recoverIssued()
      }).then(response => {
        this.$message.success(response.msg || '操作成功')
      }).catch(err => {
        console.log(err)
      }).finally(() => {
        this.sendingDirective = false
      })
    },
    /** 查询机柜列表 */
    getList() {
      this.loading = true
      listBaseDevice(this.queryParams).then((response) => {
        console.log(response)
        this.baseDeviceList = response.data.rows || response.rows || response.data
        this.total = response.data.total || response.total || 0

      }).finally(() => {
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: undefined,
        name: undefined,
        // deviceNo: undefined,
        regionId: undefined,
        // onlineStatus: undefined,
        // deviceStatus: undefined,
        // runStatus: undefined,
        runMode: 1,
        type: 0,
        // runModeType: 0,
        dcModuleNum: undefined,
        dimmerNum: undefined,
        // positivePoleResistance: undefined,
        // negativePoleResistance: undefined,
        // busbarCrossoverVoltage: undefined,
        // busbarCrossoverCurrent: undefined,
        // lastTime: undefined,
        // delFlag: undefined,
        // createBy: undefined,
        // createTime: undefined,
        // updateBy: undefined,
        // updateTime: undefined,
        ip: undefined,
        deviceId: undefined,
        port: undefined
      }
      this.resetForm('form')
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map((item) => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加机柜'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true
      this.reset()
      const id = row.id || this.ids
      getBaseDevice(id).then((response) => {
        console.log(response)

        this.loading = false

        Object.assign(this.form, response.data || {})
        this.form.name = response.data.deviceName
        this.form.deviceId = response.data.deviceNo
        this.open = true
        this.title = '修改机柜'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          this.buttonLoading = true
          if (this.form.id != null) {
            updateBaseDevice(this.form)
              .then((response) => {
                this.$modal.msgSuccess('修改成功')
                this.open = false
                this.getList()
              })
              .finally(() => {
                this.buttonLoading = false
              })
          } else {
            addBaseDevice(this.form)
              .then((response) => {
                this.$modal.msgSuccess('新增成功')
                this.open = false
                this.getList()
              })
              .finally(() => {
                this.buttonLoading = false
              })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal
        .confirm('是否确认删除机柜编号为"' + ids + '"的数据项？')
        .then(() => {
          this.loading = true
          return delBaseDevice(ids)
        })
        .then(() => {
          this.loading = false
          this.getList()
          this.$modal.msgSuccess('删除成功')
        })
        .catch(() => {
        })
        .finally(() => {
          this.loading = false
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download(
        'zm/baseDevice/export',
        {
          ...this.queryParams
        },
        `baseDevice_${new Date().getTime()}.xlsx`
      )
    },
    handleRowStyle(row) {
      //   console.log(row);
      if (row.rowIndex % 2 != 0) {
        return 'custom-border-color'
      }
      return 'custom-cell-class-name custom-border-color'
    },
    handleHeaderRowStyle(row) {
      console.log(row)
      return {
        backgroundColor: '#2280ec',
        color: '#fff',
        fontSize: '16px'
        // border:"1px solid #2280ec !important"
      }
    },
    _noPageList() {
      noPageList().then(res => {
        console.log(res)
        if (!this.noPageList.length) {
          this.noPageList = res.data
        }
      })
    },
    getNoPageList(e) {
      console.log(e)
      if (e) {
        this._noPageList()
      }
    },
    handleInit(row) {
      this.$confirm(`确认初始化?`, row.name || '警告', {
        type: 'warning'
      }).then(res => {
        return baseDeviceInit({
          deviceId: row.deviceId
        })
      }).then(res => {
        console.log(res)
        this.$message({
          type: 'warning',
          message: res.msg
        })
      }).catch(e => {

      })
    }
  }
}
</script>
