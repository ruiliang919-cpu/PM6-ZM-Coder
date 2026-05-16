<script>
export default {
  data() {
    return {
      x: 0,
      y: 0,
      isEnter: false
    }
  },
  methods: {
    mouseenter(e) {
      this.isEnter = true
    },
    mouseleave(e) {
      this.isEnter = false
    },
    mousemove(e) {
      console.log(e.pageX, e.pageY)
      this.x = e.pageX
      this.y = e.pageY
    }
  }
}
</script>

<template>
  <div class="followMouse" @mouseenter="mouseenter" @mouseleave="mouseleave" @mousemove="mousemove">
    <slot/>
    <div class="circle" :style="{
      transform:`translate3d(-50%, -50%, 0)`,
      left:x + 'px',
      top:y + 'px'
    }"
    ></div>
  </div>
</template>

<style scoped lang="scss">
.followMouse {
  position: relative;
  width: 100%;
  height: 100%;
  .circle {
    position: fixed;
    width: 50px;
    height: 50px;
    background: rebeccapurple;
    border-radius: 50%;
    z-index: 999999999;
    top: 0;
    left: 0;
    pointer-events: none;
  }
}
</style>
