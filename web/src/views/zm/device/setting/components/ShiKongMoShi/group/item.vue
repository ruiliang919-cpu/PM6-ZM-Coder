<template>
  <div class="check_item">
    <el-checkbox :label="data.groupId" :value="!!data.selectStatus"
                 @change="$event?data.selectStatus = 1 : data.selectStatus = 0"
                 style="width: 100%"
    >
      <div class="text">
        <el-tooltip :content="data.groupName || '未命名分组'" placement="top">
          <div>{{ data.groupName || '未命名分组' }}</div>
        </el-tooltip>
      </div>
    </el-checkbox>
  </div>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default() {
        return {}
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
  overflow: hidden;

  &:nth-child(2n -1) {
    background-color: #e7f3ff !important;
  }

  &:last-of-type {
    border-bottom: 0 !important;
  }

  .text {
    display: flex;
    width: 100%;
    overflow: hidden;

    & > div:nth-child(1) {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  ::v-deep .el-checkbox {
    width: 100%;
    display: flex;
    align-items: center;

    .el-checkbox__label {
      flex: 1;
      display: flex;
      align-items: center;
      overflow: hidden;
    }

    //.el-checkbox__inner {
    //  width: 20px;
    //  height: 20px;
    //  border-radius: 100%;
    //  border-width: 3px;
    //
    //  &::after {
    //    width: 8px;
    //    height: 8px;
    //    border-width: 2px;
    //    // top: 2px;
    //    // left: 5px;
    //    // transform: rotate(45deg);
    //  }
    //}
  }
}
</style>
