<template>

  <CommonContainer
    title="分组控制"
    :contentPadding="0"
    :borderWidth="1"
    style="flex: 1; overflow: hidden;"
  >

    <div class="group">
      <el-checkbox-group v-model="checkedData" style="width: 100%">

        <Item v-for="data in groupControl" :data="data" :key="data.id + data.name" class="item"></Item>
        <template v-if="!groupControl.length">
          <el-empty description="无数据"></el-empty>
        </template>
      </el-checkbox-group>
    </div>

  </CommonContainer>

</template>
<script>
import Item from './item.vue'
import CommonContainer from '@/components/CommonContainer/index.vue'

export default {
  props: {
    groupControl: {
      type: Array,
      default() {
        return []
      }
    }
  },
  components: {
    CommonContainer,
    Item
  },
  data() {
    return {
      checkedData:[]
    }
  },
  methods:{
    selectAll() {
      this.checkedData = this.groupControl.map(item => item.id)
      console.log(this.checkedData)
    },
    clearAll() {
      this.checkedData = []
    }
  }
}
</script>
<style scoped lang="scss">
.group {
  height: 469px;
  overflow: hidden;
  overflow-y: auto;
}
</style>
