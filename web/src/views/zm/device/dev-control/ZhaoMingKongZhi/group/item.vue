<template>
    <el-checkbox :label="data.id" style="width: 100%" class="check_item">
      <div class="text">
        <div>{{ data.name || '未命名'}}</div>
      </div>
    </el-checkbox>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default() {
        return {
          id:"",
          name:"名称"
        }
      }
    }
  }
}
</script>
<style scoped lang="scss">
.check_item {
  padding: 15px;
  border: 0 solid #2280ec;
  border-bottom-width: 1px;
  display: flex;
  &:nth-child(2n -1) {
    background-color: #e7f3ff !important;
  }
  &:last-of-type {
    border-bottom: 0 !important;
  }

  .text {
    display: flex;
    width: 100%;

    & > div:nth-child(1) {
      flex: 1;
      overflow: hidden;
    }
  }

  ::v-deep .el-checkbox {
    width: 100%;
    display: flex;

    .el-radio__label {
      flex: 1;
      display: flex;
      align-items: center;
    }

    .el-checkbox__inner {
      width: 20px;
      height: 20px;
      border-radius: 100%;
      border-width: 3px;

      &::after {
        width: 8px;
        height: 8px;
        border-width: 2px;
        // top: 2px;
        // left: 5px;
        // transform: rotate(45deg);
      }
    }
  }
}
</style>
