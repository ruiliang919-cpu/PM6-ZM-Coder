<template>
  <div class="check_item">
    <el-checkbox style="width: 100%" :value="data.zoneId" :label="data.zoneId "
    >
      <div class="text">
        <!--        <el-tooltip :content="data.groupName || '未设置'" placement="top">-->
        <div>{{ data.name || '未命名' }}</div>
        <!--        </el-tooltip>-->
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
    },
    id: {
      type: [String, Number],
      default: 0
    },
    label: {
      type: [String, Number],
      default: 0
    }
  }
}
</script>
<style scoped lang="scss">
.check_item {
  padding: 15px;
  //border: 1px solid #2280ec;
  border-top-width: 0;
  display: flex;

  &:nth-child(2n -1) {
    background-color: #e7f3ff !important;
  }

  //&:last-of-type {
  //  border-bottom: 0px !important;
  //}

  .text {
    display: flex;
    width: 100%;

    & > div:nth-child(1) {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }

  ::v-deep .el-checkbox {
    width: 100%;
    display: flex;

    .el-checkbox__label {
      flex: 1;
      display: flex;
      align-items: center;
      overflow: hidden;
    }

    //.el-radio__inner {
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
