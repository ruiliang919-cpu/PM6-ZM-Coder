<template>
  <CommonContainer
    title="调光控制"
    :contentPadding="0"
    :borderWidth="0"
    style="flex: 1; overflow: hidden;margin-top: 10px"
    v-loading="loading"
  >
<!--    {{currentBrightness}}-->
    <div class="table">
      <div
        class="table_item"
        v-for="(item, index) in 10"
        :key="index"
        :class="{ active: (index * 10 < currentBrightness)  && ((index + 1) * 10 >= currentBrightness) }"
        @click="handleClick(index)"
      >
        {{ (index + 1) * 10 }} %
      </div>
    </div>
  </CommonContainer>

</template>
<script>
import CommonContainer from '@/components/CommonContainer/index.vue'

export default {
  components: { CommonContainer },
  props: {
    currentBrightness: {
      type: Number,
      default: 0
    }
  },
  data() {
    return {
      currentIndex: 0,
      loading: false
    }
  },
  methods: {
    handleClick(index) {
      this.$emit('change', index)
    },
    setLoading(flag) {
      this.loading = flag
    }
  }
}
</script>
<style scoped lang="scss">
.table {
  display: flex;
  flex-wrap: wrap;
  margin-top: 1px;

  .table_item {
    background-color: #e2dbdb;
    color: #84807f;
    flex-basis: 20%;
    overflow: hidden;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 0;
    border-bottom: #fff 1px solid;

    & {
      border-right: #fff 1px solid;
    }

    &:nth-child(5n) {
      border-right: #fff 0 solid;
    }

    cursor: pointer;

    &:hover {
      background-color: #4f4f4f5c;
    }
  }

  .active {
    background-color: #2280ec !important;
    color: #fff !important;
  }
}
</style>
