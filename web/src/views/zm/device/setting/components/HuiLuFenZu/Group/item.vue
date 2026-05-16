<template>
  <div class="check_item">
    <el-radio :value="data.groupId" :label="data.groupId" style="width: 100%">
      <div class="text">
        <div>{{ data.name || '未命名分组' }}</div>
        <div>回路数({{ data.loopNum || 0 }})</div>
        <div>
          <el-button size="mini" type="text" @click="$emit('rename',data)">重命名</el-button>
        </div>
      </div>
    </el-radio>
  </div>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default() {
        return {
          id: '',
          name: '',
          loopNum: '0'
        }
      }
    }
  }
}
</script>
<style scoped lang="scss">
.check_item {
  padding: 15px;
  //border: 1px solid #2280ec;
  //border-top: 0;
  //border-right: 0;
  //border-left: 0;
  display: flex;

  &:nth-child(2n -1) {
    background-color: #e7f3ff !important;
  }

  .text {
    display: flex;
    width: 100%;

    & > div {
      display: flex;
      align-items: center;

      &:last-child {
        margin-left: 10px;
      }
    }

    & > div:nth-child(1) {
      flex: 1;
      overflow: hidden;
    }
  }

  ::v-deep .el-radio {
    width: 100%;
    display: flex;

    .el-radio__label {
      flex: 1;
      display: flex;
      align-items: center;
    }

    .el-radio__inner {
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
