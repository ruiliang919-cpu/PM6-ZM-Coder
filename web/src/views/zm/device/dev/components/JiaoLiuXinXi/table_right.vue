<template>
  <div class="table">
    <div class="table_row">
      <div class="table_col">
        <div class="left">Uab:</div>
        <div class="right">{{ data.uab }}</div>
      </div>
      <div class="table_col">
        <div class="left">Ia:</div>
        <div class="right">{{ data.ia }}</div>
      </div>
    </div>
    <div class="table_row">
      <div class="table_col">
        <div class="left">Ubc:</div>
        <div class="right">{{ data.ubc }}</div>
      </div>
      <div class="table_col">
        <div class="left">Ib:</div>
        <div class="right">{{ data.ib }}</div>
      </div>
    </div>
    <div class="table_row">
      <div class="table_col">
        <div class="left">Uca:</div>
        <div class="right">{{ data.uac }}</div>
      </div>
      <div class="table_col">
        <div class="left">Ic:</div>
        <div class="right">{{ data.ic }}</div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default() {
        return {
          // 交流1路ID
          'id': 0,
          // 交流1路设备ID
          'deviceId': 0,
          // 交流回路路数 1表示交流1路
          'circuitNum': 0,
          // 交流1路UAB
          'uab': 0,
          // 交流1路UBC
          'ubc': 0,
          // 交流1路UAC
          'uac': 0,
          // 交流1路IA
          'ia': 0,
          // 交流1路IB
          'ib': 0,
          // 交流1路IC
          'ic': 0
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
.table {
  border: 1px solid #2280ec;
  margin-top: 10px;

  .table_row {
    display: flex;

    .table_col {
      flex: 1;
      overflow: hidden;
      display: flex;
      //border: 1px solid #2280ec;

      .left,
      .right {
        flex: 1;
        overflow: hidden;
        padding: 20px;
        border-bottom: 1px solid #2280ec;
        border-right: 1px solid #2280ec;
      }

      & .right:last-child {
        border-right: 0;
      }

      .left {
        background-color: #3673bb;
        color: #fff;

      }
    }

    &:last-child .table_col {
      .left,
      .right {
        border-bottom: 0;
      }
    }
  }
}
</style>
