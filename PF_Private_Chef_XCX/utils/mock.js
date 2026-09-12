/**
 * 本地假数据 —— 用于开发期跑通全流程
 *
 * ⚠️ 上线前必须替换成真实内容：
 *   1. 厨师姓名、从业年限、擅长菜系、服务区域
 *   2. 每条 case 的 images 换成真实宴席照片（云存储或 COS 链接）
 *   3. packages 的菜品与参考价按实际报价调整
 *   4. 所有价格都写"参考价"，实际以沟通确认的菜单为准
 */

/** 厨师名片 */
const chef = {
  id: 'chef_001',
  name: '王师傅',
  title: '中式宴席主厨',
  years: 15,
  city: '河南 · 平顶山',
  // 服务范围（平顶山全市）
  area: '平顶山全市（新华、卫东、湛河、石龙及各县市）',
  cuisines: ['豫菜', '家常宴席', '融合菜', '商务宴请'],
  intro:
    '从业 15 年，擅长中式宴席与家常硬菜。上门为您掌勺：从菜单设计、食材采买，到现场烹饪、上菜收尾，全流程负责。',
  healthCert: true, // 健康证持有
  // 统计数据（用于首页展示，接后端后改为实时计算）
  stats: {
    banquets: 320, // 累计上门的宴席场次
    repeatRate: 0.68, // 老客户复购率
    maxPeople: 30, // 单场最多接待人数
  },
  // 服务流程
  flow: [
    { step: '01', title: '沟通需求', desc: '人数、口味、预算、场地与时间' },
    { step: '02', title: '确认菜单', desc: '按预算出菜单，文字确认后定档' },
    { step: '03', title: '采买备料', desc: '食材实报实销，小票留存' },
    { step: '04', title: '上门烹饪', desc: '现场掌勺、上菜、收尾清理' },
  ],
}

/** 宴席作品案例 */
const cases = [
  {
    id: 'case_001',
    scene: '商务宴请',
    people: 12,
    date: '2026-08-28',
    title: '公司客户答谢宴 · 12 人',
    menu: '清蒸黄河鲤鱼、道口烧鸡、葱烧海参、扒广肚、炸八块、蒜香排骨、油焖大虾、四凉四热',
    highlight: '客户要求"有面子但不铺张"，用豫菜硬菜撑场，人均控制在 300 出头',
    refPrice: 3888,
    images: [],
  },
  {
    id: 'case_002',
    scene: '家宴',
    people: 8,
    date: '2026-08-16',
    title: '三代同堂家宴 · 8 人',
    menu: '红烧肉、糖醋软溜黄河鲤、干炸小酥肉、蒜泥白肉、地三鲜、凉拌荆芥、手工馒头',
    highlight: '老人牙口不好，把硬菜做成软烂口，小孩那份单独做了不辣的',
    refPrice: 1280,
    images: [],
  },
  {
    id: 'case_003',
    scene: '生日宴',
    people: 10,
    date: '2026-07-22',
    title: '父亲六十大寿 · 10 人',
    menu: '整只烤鸭、清蒸石斑、佛跳墙（简版）、油焖大虾、海米扒白菜、长寿面',
    highlight: '寿宴讲究"整"字，鱼要整条、鸡要整只，上菜顺序也按老规矩来',
    refPrice: 2600,
    images: [],
  },
  {
    id: 'case_004',
    scene: '满月宴',
    people: 20,
    date: '2026-07-05',
    title: '宝宝满月宴 · 20 人',
    menu: '八凉八热、扒肘子、四喜丸子、清蒸鲈鱼、糯米甜饭、红鸡蛋',
    highlight: '家里老人多、孩子多，整体口味偏清淡，特意备了两道甜口',
    refPrice: 3200,
    images: [],
  },
  {
    id: 'case_005',
    scene: '同学聚会',
    people: 14,
    date: '2026-06-18',
    title: '毕业十年同学聚会 · 14 人',
    menu: '羊肉串、烤羊排、大盘鸡、香辣蟹、手撕包菜、拍黄瓜、冰镇酸梅汤',
    highlight: '氛围局，主打硬菜配酒，出菜节奏压慢，边喝边上',
    refPrice: 1800,
    images: [],
  },
  {
    id: 'case_006',
    scene: '乔迁宴',
    people: 16,
    date: '2026-05-30',
    title: '新房乔迁 · 16 人',
    menu: '红烧狮子头、清炖狮子头汤、白灼虾、蒜蓉粉丝蒸扇贝、锅塌豆腐、四喜烤麸',
    highlight: '新房厨房不熟，提前一小时到场熟悉灶台和燃气',
    refPrice: 2200,
    images: [],
  },
]

/** 参考套餐（不是死价，按实际食材结算） */
const packages = [
  {
    id: 'pkg_150',
    name: '家常宴席',
    perPerson: 150,
    tag: '家宴 · 朋友聚',
    dishes: ['四凉六热', '一道整鱼', '一道汤', '主食 + 果盘'],
    note: '家常硬菜为主，适合 6-12 人',
  },
  {
    id: 'pkg_300',
    name: '体面宴席',
    perPerson: 300,
    tag: '生日 · 乔迁 · 答谢',
    dishes: ['六凉八热', '整鱼整鸡', '海参或鲍鱼一道', '两道汤', '主食 + 甜点 + 果盘'],
    note: '有排面也压得住预算，最常被选的档位',
  },
  {
    id: 'pkg_500',
    name: '商务宴请',
    perPerson: 500,
    tag: '商务 · 重要客人',
    dishes: ['八凉十热', '高档海鲜 2-3 道', '参鲍类一道', '三道汤', '主食 + 甜点 + 果盘 + 茶'],
    note: '上菜节奏与摆盘会特别设计，适合招待重要客人',
  },
  {
    id: 'pkg_custom',
    name: '定制菜单',
    perPerson: 0,
    tag: '上不封顶',
    dishes: ['按您的要求定制'],
    note: '高端食材、特殊菜系、外请帮厨，均可单独沟通',
  },
]

/** 档期（开发期假数据；接后端后按日期实时查询） */
function buildSlots() {
  const meals = ['午宴', '晚宴']
  const slots = []
  const base = new Date()
  for (let i = 1; i <= 21; i++) {
    const d = new Date(base.getTime() + i * 86400000)
    const date = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    const weekday = d.getDay() // 0=周日
    meals.forEach((meal, idx) => {
      // 模拟：周末晚宴已被订走，其余可约
      const isWeekend = weekday === 0 || weekday === 6
      const full = isWeekend && meal === '晚宴'
      slots.push({
        id: `${date}_${idx}`,
        date,
        meal,
        weekday,
        status: full ? 'full' : 'open',
      })
    })
  }
  return slots
}

const slots = buildSlots()

/** 客户评价（需授权后展示） */
const reviews = [
  { id: 'r1', name: '张女士', scene: '家宴', score: 5, content: '菜做得比饭店还地道，厨房收拾得比来之前还干净。' },
  { id: 'r2', name: '李先生', scene: '商务宴请', score: 5, content: '客人一直夸，说在哪家饭店都没吃到这个味道。' },
  { id: 'r3', name: '王先生', scene: '生日宴', score: 5, content: '提前一小时就来熟悉灶台了，很专业，老人很满意。' },
]

module.exports = { chef, cases, packages, slots, reviews }
