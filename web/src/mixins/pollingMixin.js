export default {
  data() { return { _pollingTimer: null } },
  methods: {
    _startPolling(fn, interval = 5000) {
      this._stopPolling()
      this._pollingTimer = setInterval(fn, interval)
    },
    _stopPolling() {
      if (this._pollingTimer) {
        clearInterval(this._pollingTimer)
        this._pollingTimer = null
      }
    }
  },
  beforeDestroy() { this._stopPolling() }
}
