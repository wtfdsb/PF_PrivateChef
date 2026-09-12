const api = require('../../utils/api.js')

const BUDGETS = ['1000-2000', '2000-3500', '3500-5000', '5000-8000', '8000 以上', '还没定']

/** 特殊需求快捷标签（点击自动填入） */
const NEED_TAGS = ['摆盘仪式', '酒水代办', '餐后收拾', '代采购食材']

Page({
  data: {
    submitting: false,
    /** 可选档期（只展示可约的） */
    slots: [],
    dateOptions: [],
    dateIndex: 0,
    mealOptions: ['午宴', '晚宴'],
    mealIndex: 0,
    budgetOptions: BUDGETS,
    budgetIndex: 1,
    needTags: NEED_TAGS,

    form: {
      name: '',
      phone: '',
      people: '',
      address: '',
      taste: '',
      needs: '',
      remark: '',
      source: '',
    },
  },

  async onLoad(query) {
    const slots = (await api.listSlots()).filter((s) => s.status === 'open')

    const seen = new Set()
    const dateOptions = []
    slots.forEach((s) => {
      if (!seen.has(s.date)) {
        seen.add(s.date)
        dateOptions.push(s.date)
      }
    })

    let dateIndex = 0
    let mealIndex = 0
    if (query.date) {
      const i = dateOptions.indexOf(query.date)
      if (i >= 0) dateIndex = i
    }
    if (query.meal) {
      const j = this.data.mealOptions.indexOf(decodeURIComponent(query.meal))
      if (j >= 0) mealIndex = j
    }

    this.setData({ slots, dateOptions, dateIndex, mealIndex })
  },

  onInput(e) {
    const key = e.currentTarget.dataset.key
    this.setData({ [`form.${key}`]: e.detail.value })
  },

  onDateChange(e) {
    this.setData({ dateIndex: Number(e.detail.value) })
  },

  onMealChange(e) {
    this.setData({ mealIndex: Number(e.detail.value) })
  },

  onBudgetChange(e) {
    this.setData({ budgetIndex: Number(e.detail.value) })
  },

  /** 点击特殊需求快捷标签：追加进 needs */
  onNeedTag(e) {
    const tag = e.currentTarget.dataset.tag
    const cur = (this.data.form.needs || '').trim()
    let next
    if (cur.includes(tag)) {
      // 已选过则移除
      next = cur
        .split(/[、,，\s]+/)
        .filter((x) => x && x !== tag)
        .join('、')
    } else {
      next = cur ? cur + '、' + tag : tag
    }
    this.setData({ 'form.needs': next })
  },

  /** 提交预约 */
  async onSubmit() {
    if (this.data.submitting) return

    const { form, dateOptions, dateIndex, mealOptions, mealIndex, budgetOptions, budgetIndex } = this.data

    const date = dateOptions[dateIndex]
    const meal = mealOptions[mealIndex]
    const clash = this.data.slots.find((s) => s.date === date && s.meal === meal)
    if (!clash) {
      wx.showToast({ title: '该档期已约满，换一个吧', icon: 'none' })
      return
    }

    this.setData({ submitting: true })
    try {
      const booking = await api.submitBooking({
        ...form,
        date,
        meal,
        budget: budgetOptions[budgetIndex],
      })
      getApp().globalData.lastBooking = booking

      // 订阅消息：预约成功后立刻引导授权，用于后续「档期确认 / 出发提醒」
      // TODO(上线前)：在微信公众平台申请模板后填入 tmplIds
      // wx.requestSubscribeMessage({ tmplIds: [] })

      wx.redirectTo({ url: '/pages/booking/result/result' })
    } catch (e) {
      wx.showToast({ title: e.message || '提交失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  },
})
