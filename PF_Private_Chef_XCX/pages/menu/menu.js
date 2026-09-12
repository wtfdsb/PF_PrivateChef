const api = require('../../utils/api.js')

Page({
  data: {
    loading: true,
    packages: [],
    chef: null,
  },

  async onLoad() {
    const [packages, chef] = await Promise.all([api.listPackages(), api.getChef()])
    this.setData({ packages, chef, loading: false })
  },

  goBooking() {
    wx.navigateTo({ url: '/pages/booking/booking' })
  },
})
