Page({
  data: {
    booking: null,
  },

  onLoad() {
    this.setData({ booking: getApp().globalData.lastBooking || null })
  },

  goHome() {
    wx.reLaunch({ url: '/pages/index/index' })
  },

  goMy() {
    wx.redirectTo({ url: '/pages/my/my' })
  },
})
