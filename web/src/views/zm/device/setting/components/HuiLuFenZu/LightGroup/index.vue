<template>
  <div class="group">
    <!-- {{ data }} -->
    <el-checkbox-group v-model="checkedData">
      <Item v-for="(item, index) in data" :rightSide="rightSide" :key="item.no" :data="item"/>
      <template v-if="!data.length">
        <el-empty description="无数据"></el-empty>
      </template>
    </el-checkbox-group>
  </div>
</template>
<script>
import Item from './item.vue'

export default {
  props: {
    rightSide: {
      type: Boolean,
      default: false
    },
    data: {
      type: Array,
      default() {
        return []
      }
    }
  },
  components: {
    Item
  },
  data() {
    return {
      checkedData: []
    }
  },
  methods: {
    clearCheckedData() {
      this.checkedData = []
    }
  }
}
</script>
<style scoped lang="scss">
.group {
  height: 408px;
  overflow: hidden;
  overflow-y: auto;
}
</style>
