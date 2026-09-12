const api = require('../../utils/api.js')

const STATUS_TEXT = {
  pending: '待联系确认',
  confirmed: '已定档',
  done: '已完成',
  canceled: '已取消',
}

Page({
  data: {
    loading: true,
    list: [],
    statusText: STATUS_TEXT,
  },

  onShow() {
    this.load()
  },

  onPullDownRefresh() {
    this.load().finally(() => wx.stopPullDownRefresh())
  },

  async load() {
    try {
      const list = await api.listMyBookings()
      this.setData({ list, loading: false })
    } catch (e) {
      this.setData({ loading: false })
      wx.showToast({ title: e.message || '加载失败', icon: 'none' })
    }
  },

  goBooking() {
    wx.navigateTo({ url: '/pages/booking/booking' })
  },
})
