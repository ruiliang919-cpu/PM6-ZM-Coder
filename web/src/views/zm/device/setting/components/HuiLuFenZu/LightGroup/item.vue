<template>
  <div class="item">
    <el-checkbox :label="data.no">
      <span class="icon" :class="{light:!rightSide}"><i class="el-icon-s-opportunity"></i></span>
      <span class="text">回路{{ data.no }}</span>
    </el-checkbox>
  </div>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default() {
        return {
          no: ''
        }
      }
    },

    rightSide: {
      type: Boolean,
      default: false
    }
  }
}
</script>
<style scoped lang="scss">
.item {
  padding: 10px;
  font-size: 15px;

  .icon, .text {
    font-size: 16px;
  }
}

.checked {
  border: 1px solid #2280ec;
}

.light {
  color: #ea9617;
}
</style>
