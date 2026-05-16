<script>
import { getDistrictList } from '@/api/zm/device/setting'

export default {
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    value: {
      type: [Number, String],
      default: ''
    },
    currentRenameItemId: {
      type: [Number, String],
      default: ''
    },
    updating:{
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      demoList: []
    }
  },
  methods: {
    getData() {
      this.loading = true
      getDistrictList({}).then(res => {
        this.demoList = res.data
      }).finally(() => {
        this.loading = false
      })
    },
    handleChange(e) {
      this.$emit('change', e)
    },
    save(e) {
      this.$emit('save', e)
      console.log('save')
    }
  },
  mounted() {
    this.getData()
  }
}
</script>

<template>
  <el-form v-loading="loading" inline>
<!--    <el-form-item>-->
<!--      <el-button-->
<!--        :disabled="!currentRenameItemId"-->
<!--        @click="$emit('rename')"-->
<!--        style="margin-right: 20px; width: 100px"-->
<!--        round-->
<!--        type="primary"-->
<!--      >分组命名-->
<!--      </el-button-->
<!--      >-->
<!--    </el-form-item>-->
    <el-form-item label="分组关联分区：">
      <el-select :value="value" @change="handleChange" placeholder="请选择" clearable>
        <el-option :key="item.id" :value="item.id" v-for="item in demoList" :label="item.name"/>
      </el-select>
    </el-form-item>
    <el-form-item>
      <el-button
        @click="save"
        round
        type="primary"
        :loading="updating"
      >保存
      </el-button>
    </el-form-item>
  </el-form>
</template>

<style scoped lang="scss">

</style>
