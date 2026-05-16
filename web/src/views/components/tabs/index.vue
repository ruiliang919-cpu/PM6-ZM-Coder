<template>
  <div class="tabs">
    <div
      class="tab_item"
      v-for="(item, index) in list"
      :key="item"
      @click="handleClick(index)"
      :class="{ active: currentIndex === index }"
    >
      {{ item }}
    </div>
  </div>
</template>
<script>
export default {
  model: {
    prop: 'currentIndex',
    event: 'change'
  },
  props: {
    list: {
      type: Array,
      default() {
        return ['tab1', 'tab2']
      }
    },
    currentIndex: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      timer: null
    }
  },
  methods: {
    handleClick(index) {
      this.$emit('change', index)
    },
    handleDblclick(index) {
      if (this.timer) return
      this.$emit('change', undefined)
      this.timer = setTimeout(() => {
        this.$emit('change', index)
        this.timer = null
      }, 10)
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearTimeout(this.timer)
    }
  }
}
</script>
<style scoped lang="scss">
.tabs {
  display: flex;

  .tab_item {
    background-color: #e5dede;
    color: #999594;
    padding: 10px 25px;
    cursor: pointer;
  }
}

.active {
  background-color: #2280ec !important;
  color: #fff !important;
}
</style>
