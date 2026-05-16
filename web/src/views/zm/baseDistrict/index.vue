<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button @click="openSetting1" type="primary" plain size="mini">分区控制组合设置</el-button>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['zm:baseDistrict:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['zm:baseDistrict:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['zm:baseDistrict:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['zm:baseDistrict:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table :cell-class-name="handleRowStyle"
              :header-cell-style="handleHeaderRowStyle" v-loading="loading" :data="baseDistrictList"
              @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="序号" align="center" prop="orderNo"/>
      <el-table-column label="分区码" align="center" prop="id"/>
      <el-table-column label="名称" align="center" prop="name"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['zm:baseDistrict:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['zm:baseDistrict:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改控制分区对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules">
        <el-form-item label="分区码(id)" prop="id">
          <el-input v-model="form.id" placeholder="请输入分区码"/>
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入名称"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm" @keydown.enter="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
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
            <GroupLeftCopy ref="groupLeft" @change="handleChange1" v-if="open1"/>
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

<script>
import {
  listBaseDistrict,
  getBaseDistrict,
  delBaseDistrict,
  addBaseDistrict,
  updateBaseDistrict
} from '@/api/zm/baseDistrict'
import { lightZoneSet } from '@/api/zm/history/powerCalculation'
import GroupRightCopy from '@/views/zm/history/powerCalculation/components/GroupRightCopy/index.vue'
import CommonContainer from '@/components/CommonContainer/index.vue'
import GroupLeftCopy from '@/views/zm/history/powerCalculation/components/GroupLeftCopy/index.vue'

export default {
  name: 'BaseDistrict',
  components: { GroupLeftCopy, CommonContainer, GroupRightCopy },
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
      // 控制分区表格数据
      baseDistrictList: [],
      // 弹出层标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: ''
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        id: [
          { required: true, message: '分区码不能为空', trigger: 'blur' }
        ],
        name: [
          { required: true, message: '名称不能为空', trigger: 'blur' }
        ]
      },
      open1: false,
      title1: '',
      loading1: false
    }
  },
  created() {
    this.getList()
  },
  methods: {
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
    handleChange1(e) {
      if (e) {
        let checkedDate = this.$refs.groupLeft.currentSelected.zoneList.map(item => Number(item))
        console.log(checkedDate)
        this.$refs.groupRight.setCheckedData(checkedDate)
      } else {
        this.$refs.groupRight.clearCheckDate()
      }
    },
    openSetting1() {
      this.open1 = true
    },
    /** 查询控制分区列表 */
    getList() {
      this.loading = true
      listBaseDistrict(this.queryParams).then(response => {
        this.baseDistrictList = response.rows
        this.total = response.total
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
        delFlag: undefined,
        createBy: undefined,
        createTime: undefined,
        updateBy: undefined,
        updateTime: undefined
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
      // this.ids = selection.map(item => item.id)
      this.ids = selection.map(item => item.orderNo)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '添加控制分区'
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.loading = true
      this.reset()
      // const id = row.id || this.ids
      const id = row.orderNo || this.ids
      getBaseDistrict(id).then(response => {
        this.loading = false
        this.form = response.data || { name: '' }
        this.open = true
        this.title = '修改控制分区'
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (valid) {
          this.buttonLoading = true
          // if (this.form.id != null) {
          if (this.form.orderNo != null) {
            updateBaseDistrict(this.form).then(response => {
              this.$modal.msgSuccess('修改成功')
              this.open = false
              this.getList()
            }).finally(() => {
              this.buttonLoading = false
            })
          } else {
            addBaseDistrict(this.form).then(response => {
              this.$modal.msgSuccess('新增成功')
              this.open = false
              this.getList()
            }).finally(() => {
              this.buttonLoading = false
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.orderNo || this.ids
      this.$modal.confirm('是否确认删除控制分区编号为"' + ids + '"的数据项？').then(() => {
        this.loading = true
        return delBaseDistrict(ids)
      }).then(() => {
        this.loading = false
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {
      }).finally(() => {
        this.loading = false
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('zm/baseDistrict/export', {
        ...this.queryParams
      }, `baseDistrict_${new Date().getTime()}.xlsx`)
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
        fontSize: '16px',
        padding: '15px 0'
        // border:"1px solid #2280ec !important"
      }
    }
  }
}
</script>
