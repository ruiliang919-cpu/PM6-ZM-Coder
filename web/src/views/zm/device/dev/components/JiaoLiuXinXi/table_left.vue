<template>
  <div class="table">
    <div class="table_col" v-if="data.uab != -1">
      <template v-if="show">
        <div class="left">U</div>
      </template>
      <template v-else>
        <div class="left">Uab</div>
      </template>
      <div class="right">{{ data.uab == -1 ? '' : data.uab }}</div>
    </div>
    <div class="table_col" v-if="data.ia != -1">
      <template v-if="show">
        <div class="left">I</div>
      </template>
      <template v-else>
        <div class="left">Ia</div>
      </template>
      <div class="right">{{ data.ia == -1 ? '' : data.ia }}</div>
    </div>
    <div class="table_col" v-if="data.ubc != -1">
      <div class="left">Ubc</div>
      <div class="right">{{ data.ubc == -1 ? '' : data.ubc }}</div>
    </div>
    <div class="table_col" v-if="data.ib != -1">
      <div class="left">Ib</div>
      <div class="right">{{ data.ib == -1 ? '' : data.ib }}</div>
    </div>
    <div class="table_col" v-if="data.uac != -1 ">
      <div class="left">Uca</div>
      <div class="right">{{ data.uac == -1 ? '' : data.uac }}</div>
    </div>
    <div class="table_col" v-if="data.ic != -1">
      <div class="left">Ic</div>
      <div class="right">{{ data.ic == -1 ? '' : data.ic }}</div>
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
  },
  computed: {
    show() {
      return Object.keys(this.data).every(key => {
        //console.log(key)
        if (key === 'id') return true
        if (key === 'circuitNum') return true
        if (key === 'deviceId') return true
        if (key === 'workStatus') return true
        if (key === 'ia') return true
        if (key === 'uab') return true
        return this.data[key] === '-1'
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.table {
  //border: 1px solid #2280ec;
  margin-top: 10px;
  padding: 5px 10px;
  //.table_row {
  //  display: flex;
  //
  //  .table_col {
  //    flex: 1;
  //    overflow: hidden;
  //    display: flex;
  //    //border: 1px solid #2280ec;
  //    .left,
  //    .right {
  //      flex: 1;
  //      overflow: hidden;
  //      padding: 20px;
  //      //border-bottom: 1px solid #2280ec;
  //      //border-right: 1px solid #2280ec;
  //    }
  //
  //    //& .right:last-child {
  //    //  border-right: 0;
  //    //}
  //
  //    .left {
  //      background-color: #3673bb;
  //      color: #fff;
  //
  //    }
  //  }
  //
  //  //&:last-child .table_col {
  //  //  .left,
  //  //  .right {
  //  //    border-bottom: 0;
  //  //  }
  //  //}
  //}
  .table {
    display: flex;
    flex-wrap: wrap;
    gap: 2%;

    .table_col {
      width: 49%;
      display: flex;
      border: 1px solid #2280ec;
      overflow: hidden;
      margin-bottom: 10px;

      & > div {
        flex-basis: 50%;
        height: 60px;
        line-height: 60px;
        text-align: center;
      }

      .left {
        background: #2280ec;
        color: white;
      }
    }
  }
}
</style>
