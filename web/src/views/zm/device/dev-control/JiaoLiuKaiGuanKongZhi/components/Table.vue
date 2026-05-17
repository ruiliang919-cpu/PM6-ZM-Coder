<script>
import { acsOn, acsOff } from '@/api/zm/device/dev-control'

export default {
  name: 'Table',
  props: {
    deviceId: {
      type: [String, Number],
      required: true,
      default: null
    },
    demoList: {
      type: Array,
      default() {
        return []
      }
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  methods: {
    handleRowStyle(row) {
      //   console.log(row);
      if (row.rowIndex % 2 !== 0) {
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
    },
    openSwitch(e) {
      this.$confirm(
        '确认闭合吗？',
        {
          title: `编号${e}`,
          type: 'warning'
        }
      ).then(async() => {
        return acsOn({
          deviceId: this.deviceId,
          onNo: e
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功')
        this.$emit('success')
      }).catch(e => {
        console.error('操作失败:', e)
        this.$message.error('操作失败，请重试')
      })
    },
    closeSwitch(e) {
      this.$confirm(
        '确认断开吗？',
        {
          title: `编号${e}`,
          type: 'warning'
        }
      ).then(async() => {
        return acsOff({
          deviceId: this.deviceId,
          offNo: e
        })
      }).then(res => {
        this.$message.success(res.msg || '操作成功')
        this.$emit('success')
      }).catch(e => {
        console.error('操作失败:', e)
        this.$message.error('操作失败，请重试')
      })
    }
  }
}
</script>

<template>
  <el-table
    v-if="demoList"
    v-loading="loading"
    :data="demoList"
    :cell-class-name="handleRowStyle"
    :header-cell-style="handleHeaderRowStyle"
  >
    <el-table-column label="编号" align="center" prop="">
      <template v-slot="scope">
        {{ scope.row }}
      </template>
    </el-table-column>
    <el-table-column label="操作" align="center" prop="">
      <template v-slot="scope">
        <el-button type="primary" @click="openSwitch(scope.row)">
          闭合
        </el-button>
      </template>
    </el-table-column>
    <el-table-column label="操作" align="center" prop="">
      <template v-slot="scope">
        <el-button type="danger" @click="closeSwitch(scope.row)">
          断开
        </el-button>
      </template>
    </el-table-column>
    <template v-slot:empty>
      <el-empty description="无数据"></el-empty>
    </template>
  </el-table>
</template>

<style scoped lang="scss">

</style>
