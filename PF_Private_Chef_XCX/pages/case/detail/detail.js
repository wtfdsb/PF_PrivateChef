const api = require('../../../utils/api.js')

Page({
  data: {
    loading: true,
    error: '',
    item: null,
    /** 案例图全部加载失败时回退渐变占位 */
    coverErr: false,
  },

  onCoverError() {
    this.setData({ coverErr: true })
  },

  async onLoad(query) {
    if (!query.id) {
      this.setData({ loading: false, error: '缺少参数' })
      return
    }
    try {
      const item = await api.getCase(query.id)
      this.setData({ item, loading: false })
      wx.setNavigationBarTitle({ title: item.title })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '加载失败' })
    }
  },

  goBooking() {
    wx.navigateTo({ url: '/pages/booking/booking' })
  },

  onShareAppMessage() {
    return {
      title: this.data.item ? this.data.item.title : '看看这桌席面',
      path: `/pages/case/detail/detail?id=${this.data.item ? this.data.item.id : ''}&from=share`,
    }
  },
})
