const api = require('../../utils/api.js')

Page({
  data: { chef: null },

  async onLoad() {
    const chef = await api.getChef()
    this.setData({ chef })
  },

  callChef() {
    const phone = (this.data.chef && this.data.chef.phone) || ''
    if (!phone) {
      wx.showToast({ title: '电话待配置', icon: 'none' })
      return
    }
    wx.makePhoneCall({ phoneNumber: phone })
  },
})
