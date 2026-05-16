<template>
  <div class="row" :style="{ border: `${borderWidth}px solid #2280ec` }">
    <div class="title_box">
      <h3>{{ title }}</h3>
      <div>
        <slot name="titleRight"></slot>
      </div>
    </div>

    <div class="content_box" :style="{ padding: contentPadding + 'px' }">
      <slot name="default"></slot>
    </div>
  </div>
</template>
<script>

export default {
  name: 'CommonContainer',
  props: {
    title: {
      type: String,
      default: '标题'
    },
    contentPadding: {
      type: Number,
      default: 10
    },
    borderWidth: {
      type: Number,
      default: 1
    }
  },
  data() {
    return {}
  }
}
</script>
<style scoped lang="scss">
* {
  margin: 0;
  padding: 0;
}

$c_padding: 0px 20px;
.row {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  // height: 100px;
  // border: 1px solid #2280ec;
  .title_box {
    padding: $c_padding;
    background-color: #2280ec;
    color: #fff !important;
    display: flex;
    min-height: 50px;
    align-items: center;

    h3 {
      flex: 1;
      overflow: hidden;
      text-overflow: ellipsis;
      font-size: 20px;
      font-weight: 500;
      letter-spacing: 2px;
    }
  }

   .content_box {
     //padding: $c_padding;
     flex: 1;
   }
}
</style>
