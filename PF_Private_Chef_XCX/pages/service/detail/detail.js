const api = require('../../../utils/api.js')

Page({
  data: {
    loading: true,
    error: '',
    item: null,
  },

  async onLoad(query) {
    if (!query.code) {
      this.setData({ loading: false, error: '缺少参数' })
      return
    }
    try {
      const item = await api.getServiceItem(query.code)
      this.setData({ item, loading: false })
      wx.setNavigationBarTitle({ title: item.name })
    } catch (e) {
      this.setData({ loading: false, error: e.message || '加载失败' })
    }
  },

  /** 预约这个服务：把服务类型带给预约页 */
  goBooking() {
    const item = this.data.item
    if (!item) return
    wx.navigateTo({
      url: `/pages/booking/booking?category=${item.code}&categoryName=${encodeURIComponent(item.name)}`,
    })
  },

  callChef() {
    const app = getApp()
    const phone = (app.globalData.chef && app.globalData.chef.phone) || ''
    if (!phone || phone.indexOf('X') >= 0) {
      wx.showToast({ title: '电话待配置', icon: 'none' })
      return
    }
    wx.makePhoneCall({ phoneNumber: phone })
  },

  onShareAppMessage() {
    const item = this.data.item
    return {
      title: item ? `${item.name} · 上门做，吃完收拾干净` : '新谷私厨 · 上门做席',
      path: item ? `/pages/service/detail/detail?code=${item.code}&from=share` : '/pages/index/index',
    }
  },
})
