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

  /** 复制订单号，方便发给客户/记录 */
  copyNo() {
    const no = this.data.booking && this.data.booking.bookingNo
    if (!no) return
    wx.setClipboardData({
      data: no,
      success: () => wx.showToast({ title: '订单号已复制', icon: 'success' }),
    })
  },
})
