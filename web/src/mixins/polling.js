/**
 * PollingMixin - 统一轮询逻辑
 *
 * 使用方式:
 * 1. import polling from '@/mixins/polling'
 * 2. mixins: [polling]
 * 3. 定义 pollingConfig 配置选项（可选）
 * 4. 实现 pollingFetch() 方法作为轮询回调
 *
 * 配置选项:
 * - interval: 轮询间隔(ms)，默认 5000
 * - enabled: 是否启用轮询，默认 true
 * - waitForNavibar: 是否等待 navibarDeviceValue 变化后才启动，默认 false
 * - useGetFlag: 是否使用 getFlag 模式（轮询时调用 getFlag 而非 pollingFetch），默认 false
 * - dynamicInterval: 动态间隔计算函数，返回间隔毫秒数，优先级高于 interval
 */
export default {
  data() {
    return {
      $pollingTimer: null,
      $pollingPaused: false
    }
  },

  computed: {
    $pollingInterval() {
      if (this.pollingConfig?.dynamicInterval) {
        return this.pollingConfig.dynamicInterval.call(this)
      }
      return this.pollingConfig?.interval || 5000
    },
    $pollingEnabled() {
      return this.pollingConfig?.enabled !== false
    },
    $pollingWaitForNavibar() {
      return this.pollingConfig?.waitForNavibar || false
    },
    $pollingUseGetFlag() {
      return this.pollingConfig?.useGetFlag || false
    }
  },

  created() {
    if (this.$pollingEnabled && !this.$pollingWaitForNavibar) {
      this.$startPolling()
    }
  },

  beforeDestroy() {
    this.$stopPolling()
  },

  methods: {
    /**
     * 启动轮询
     * 会先停止现有轮询，避免重复
     */
    $startPolling() {
      this.$stopPolling()
      if (this.$pollingPaused) return
      this.$pollingTimer = setInterval(() => {
        if (this.$pollingUseGetFlag) {
          this.getFlag?.()
        } else {
          this.pollingFetch?.()
        }
      }, this.$pollingInterval)
    },

    /**
     * 停止轮询
     * 安全清理 timer，防止内存泄漏
     */
    $stopPolling() {
      if (this.$pollingTimer) {
        clearInterval(this.$pollingTimer)
        this.$pollingTimer = null
      }
    },

    /**
     * 暂停轮询
     * 暂停后需调用 $resumePolling() 恢复
     */
    $pausePolling() {
      this.$pollingPaused = true
      this.$stopPolling()
    },

    /**
     * 恢复轮询
     */
    $resumePolling() {
      this.$pollingPaused = false
      this.$startPolling()
    },

    /**
     * 轮询回调方法
     * 使用 mixin 的组件必须实现此方法
     * 或使用 useGetFlag 模式时实现 getFlag() 方法
     */
    pollingFetch() {
      console.warn('[PollingMixin] pollingFetch() not implemented in component:', this.$options.name)
    }
  }
}
