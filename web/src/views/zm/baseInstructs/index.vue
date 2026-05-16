<template>
  <div class="app-container">

    <el-table :cell-class-name="handleRowStyle"
              :header-cell-style="handleHeaderRowStyle" v-loading="loading" :data="demoList">
      <el-table-column label="编号" align="center" prop="id" >
        <template slot-scope="scope">
          #{{scope.row.id}}
        </template>
      </el-table-column>
      <el-table-column label="ip" align="center" prop="ip" width="120"/>
      <el-table-column label="主机号" align="center" prop="salveId" />
      <el-table-column label="功能码" align="center" prop="code" />
      <el-table-column label="寄存器/位地址" align="center" prop="addr" width="130"/>
      <el-table-column label="写入的寄存器/位地址个数" align="center" prop="addrNum" width="130"/>
      <el-table-column label="写入的值（十进制）" align="center" prop="writeValue" width="180"/>
      <el-table-column label="报文类型" align="center" min-width="100">
        <template v-slot="scope">
          <el-tag v-if="scope.row.type === 0">发送指令</el-tag>
          <el-tag v-else-if="scope.row.type === 1">接收指令</el-tag>
          <el-tag v-else-if="scope.row.type === 2">机柜初始化指令</el-tag>
          <el-tag v-else>未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="反馈" align="center" >
        <template v-slot="scope">
          <el-tag v-if="scope.row.feedback === 0" type="success">正常</el-tag>
          <el-tag v-else-if="scope.row.feedback === 1" type="danger">异常</el-tag>
          <el-tag v-else-if="scope.row.feedback === 2" type="warning">在队列中</el-tag>
          <el-tag v-else>未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="时间戳" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.timestamp, '{y}-{m}-{d} {hh}:{mm}:{ss}') }}</span>
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
  </div>
</template>

<script>
import {getInstructs} from '@/api/zm/device/instructs'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: "Demo",
  components: {
  },
  data() {
    return {

      // 遮罩层
      loading: true,

      // 总条数
      total: 0,
      // 测试单表表格数据
      demoList: [],

      // 是否显示弹出层
      open: false,

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    parseTime,
    /** 查询测试单表列表 */
    getList() {
      this.loading = true;
      getInstructs(this.queryParams).then(response => {
        this.demoList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },


    handleRowStyle(row) {
      //   console.log(row);
      if (row.rowIndex % 2 != 0) {
        return "custom-border-color";
      }
      return "custom-cell-class-name custom-border-color";
    },
    handleHeaderRowStyle(row) {
      console.log(row);
      return {
        backgroundColor: "#2280ec",
        color: "#fff",
        fontSize: "16px",
        padding:"15px 0"
        // border:"1px solid #2280ec !important"
      };
    },
  }
};
</script>
