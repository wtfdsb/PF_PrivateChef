/**
 * 本地假数据 —— 用于开发期跑通全流程
 *
 * ⚠️ 上线前必须替换成真实内容：
 *   1. 厨师姓名、电话、获奖头衔
 *   2. 每条 case 的 images 换成真实宴席照片（云存储或 COS 链接）
 *   3. packages 的起步价按实际报价调整
 *   4. 所有价格都写"起/参考价"，实际以沟通确认的菜单为准
 * 内容与后端 sql/data.sql 保持一致。
 */

/** 厨师名片 */
const chef = {
  id: 'chef_001',
  name: '王师傅',
  brand: '新谷私厨',
  title: '豫菜名师 · 上门家宴',
  years: 10,
  city: '河南 · 平顶山',
  // 服务范围（对齐豆包模板：仅限市区，偏远加里程费）
  area: '平顶山市区（偏远区域加收上门里程费）',
  cuisines: ['豫菜', '家常宴席', '海鲜姿造', '高端火锅', '商务宴请', '融合菜', '淮扬菜', '川菜'],
  intro:
    '从业十年，豫菜出身，把星级宴席搬进您家：八大菜系、精品海鲜、海鲜姿造、高端火锅，名厨亲自掌勺。从菜单设计、食材采买，到现场烹饪、上菜收尾，全流程负责。',
  healthCert: true, // 健康证持有
  phone: '15903697992', // 对外电话（首页/关于页「电话咨询」拨打）
  awards: '河南烹饪大赛获奖',
  // 首页数据条（接后端后由 chef_profile 表实时提供）
  stats: {
    banquets: 320, // 累计上门的宴席场次
    rating: 4.9, // 客户评分
    cuisines: 8, // 精通菜系数
  },
  // 服务项目
  services: [
    { name: '家庭聚餐', desc: '团圆饭 · 朋友小聚' },
    { name: '生日寿宴', desc: '寿面 · 寿桃 · 老规矩' },
    { name: '商务接待', desc: '有面子 · 有分寸' },
    { name: '节日宴席', desc: '中秋 · 春节 · 满月' },
    { name: '精品海鲜', desc: '当日采买 · 清蒸见火候' },
    { name: '海鲜姿造', desc: '冰盘摆盘 · 主题定制' },
    { name: '高端火锅', desc: '打边炉 · 海鲜火锅' },
  ],
  // 服务流程
  flow: [
    { step: '01', title: '沟通需求', desc: '人数、口味、预算、场地与时间' },
    { step: '02', title: '确认菜单', desc: '按预算出菜单，文字确认后定档' },
    { step: '03', title: '采买备料', desc: '食材当日采买，实报实销，小票留存' },
    { step: '04', title: '上门烹饪', desc: '现场掌勺、上菜、收尾清理' },
  ],
}

/** 宴席作品案例（对齐豆包模板的品类） */
const cases = [
  {
    id: 'case_001',
    scene: '家庭聚餐',
    people: 8,
    date: '2026-08-28',
    title: '家庭家宴 · 温馨一餐',
    menu: '红烧黄河大鲤鱼、道口烧鸡、蒜香排骨、油焖大虾、清炒时蔬、手工馒头、小米粥',
    highlight: '一家人围坐的家常席，菜不花哨但道道是小时候的味道，老人孩子都吃得舒服',
    refPrice: 1280,
    images: ['/images/case-1.jpg'],
  },
  {
    id: 'case_002',
    scene: '生日寿宴',
    people: 10,
    date: '2026-08-16',
    title: '父亲六十大寿 · 十全十美',
    menu: '整只烤鸭、清蒸石斑、葱烧海参、四喜丸子、海米扒白菜、长寿面、寿桃包',
    highlight: '寿宴讲究"整"字：鱼整条、鸡整只，上菜顺序按老规矩来，老人特别满意',
    refPrice: 2600,
    images: ['/images/case-2.jpg'],
  },
  {
    id: 'case_003',
    scene: '商务接待',
    people: 12,
    date: '2026-07-22',
    title: '商务私宴 · 高端接待',
    menu: '清蒸东星斑、海鲜姿造冰盘、佛跳墙（简版）、扒广肚、炸八块、位上汤品',
    highlight: '客户要求"有面子不铺张"，豫菜硬菜撑场，人均控制在 300 出头',
    refPrice: 3888,
    images: ['/images/case-3.jpg'],
  },
  {
    id: 'case_004',
    scene: '精品海鲜',
    people: 8,
    date: '2026-07-05',
    title: '清蒸东星斑 · 精品海鲜',
    menu: '清蒸东星斑、蒜蓉粉丝蒸扇贝、白灼基围虾、葱姜炒花蟹、海鲜粥',
    highlight: '海鲜当日采买，清蒸最见火候，鱼肉嫩到筷子一碰就散',
    refPrice: 2680,
    images: ['/images/case-4.jpg'],
  },
  {
    id: 'case_005',
    scene: '海鲜姿造',
    people: 10,
    date: '2026-06-18',
    title: '海鲜姿造 · 冰盘摆盘',
    menu: '海鲜姿造冰盘、刺身拼盘、象拔蚌、北极贝、精美雕花摆盘',
    highlight: '冰盘造型按主题定制，上桌先拍照再动筷，朋友圈先吃',
    refPrice: 3280,
    images: ['/images/case-5.jpg'],
  },
  {
    id: 'case_006',
    scene: '节日宴席',
    people: 16,
    date: '2026-05-30',
    title: '中秋家宴 · 十六人团圆席',
    menu: '八凉八热、红烧狮子头、清炖甲鱼汤、白灼虾、蒜蓉蒸扇贝、糯米甜饭、月饼',
    highlight: '节日档期提前三周定档，一家十六口，菜量足、节奏慢、边吃边聊',
    refPrice: 2680,
    images: ['/images/case-6.jpg'],
  },
]

/** 参考套餐（不是死价，按实际食材结算） */
const packages = [
  {
    id: 'pkg_688',
    name: '实惠家宴套餐',
    startPrice: 0,
    tag: '家庭首选',
    dishes: ['豫菜经典', '红烧类', '汤羹', '主食拼盘'],
    note: '6-8人 · 适合家庭聚餐、朋友小聚。家常菜+豫菜经典+汤羹主食，荤素搭配、营养均衡，满满的烟火气。',
  },
  {
    id: 'pkg_1288',
    name: '精品豫菜套餐',
    startPrice: 0,
    tag: '宴请优选',
    dishes: ['八大菜系', '精品海鲜', '宴席硬菜', '位上汤品'],
    note: '8-12人 · 适合生日寿宴、商务家宴。中高档热菜+精品海鲜+宴席硬菜，豫菜为主、融合八大菜系经典，撑得起场面。',
  },
  {
    id: 'pkg_custom',
    name: '高端尊享私宴',
    startPrice: 0,
    tag: '高端定制',
    dishes: ['海鲜姿造', '高端火锅', '海鲜宴席', '定制菜单'],
    note: '12人以上 · 适合商务接待、高端宴请、重要节日。海鲜姿造冰盘、高端海鲜火锅/打边炉、八大菜系精品大菜，一站式高端私宴，按需求定制报价。',
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
  { id: 'r1', name: '张女士', scene: '家庭聚餐', score: 5, content: '菜做得比饭店还地道，厨房收拾得比来之前还干净。' },
  { id: 'r2', name: '李先生', scene: '商务接待', score: 5, content: '客人一直夸，说在哪家饭店都没吃到这个味道。' },
  { id: 'r3', name: '王先生', scene: '生日寿宴', score: 5, content: '提前一小时就来熟悉灶台了，很专业，老人很满意。' },
]

/** 服务项目详情（与后端 sql/data.sql 的 service_item 一致） */
const services = [
  {
    code: 'family', name: '家庭聚餐', subtitle: '一家人吃顿好的',
    intro: '不用下馆子等位，也不用我妈在厨房忙活一下午。您家的灶台、您家的碗筷，我上门把菜做了，吃完我把厨房归置干净再走。',
    scenes: ['周末团圆', '爸妈来住几天', '孩子考完试犒劳一下'],
    menu: ['红烧黄河大鲤鱼', '道口烧鸡', '蒜香排骨', '油焖大虾', '清炒时蔬', '手工馒头', '小米粥'],
    priceNote: '价格面议——菜单和人数定下来，跟师傅直接谈；食材实报实销，小票给您看。',
    prep: ['有灶有锅、厨房能进人就行', '老房子灶火小的提前说，我带电灶', '碗筷餐具用您家里的'],
    tips: '老人孩子多的提前说一声，软烂口和不辣的分开做，不用一桌人迁就一个人。',
  },
  {
    code: 'birthday', name: '生日寿宴', subtitle: '寿面寿桃，按老规矩来',
    intro: '给老人过大寿、给孩子过满月，这顿饭讲究的是个"整"字。鱼整条、鸡整只，上菜顺序我也按老规矩排。',
    scenes: ['老人六十/七十大寿', '孩子满月、周岁', '家里长辈过生日'],
    menu: ['整只烤鸭', '清蒸石斑', '葱烧海参', '四喜丸子', '长寿面', '寿桃包'],
    priceNote: '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
    prep: ['长寿面用您家灶现擀现下', '蛋糕您自己定，我负责热菜', '提前告诉我寿星忌口'],
    tips: '寿宴图个热闹，出菜节奏我会压慢一点，让大家边吃边聊。',
  },
  {
    code: 'business', name: '商务接待', subtitle: '有面子，不铺张',
    intro: '招待客户这顿饭，难点不在菜贵，在于有分寸。硬菜撑场面，人均还压得住，客人夸"比饭店强"，您脸上也有光。',
    scenes: ['招待客户', '答谢合作伙伴', '新同事接风'],
    menu: ['清蒸东星斑', '海鲜姿造冰盘', '佛跳墙（简版）', '扒广肚', '炸八块', '位上汤品'],
    priceNote: '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
    prep: ['最好有单独的会客区域', '酒水您备，或者我按清单代办', '需要提前半小时到场的说一声'],
    tips: '上菜节奏跟着您谈事的进度走，不催不赶，该上汤的时候上汤。',
  },
  {
    code: 'festival', name: '节日宴席', subtitle: '过年过节，在家摆一桌',
    intro: '中秋、春节、乔迁、满月，这些日子在家吃才叫过节。我提前到，您该接客接客，厨房交给我。',
    scenes: ['中秋团圆', '春节家宴', '乔迁、满月'],
    menu: ['八凉八热', '红烧狮子头', '清炖甲鱼汤', '白灼虾', '蒜蓉蒸扇贝', '糯米甜饭'],
    priceNote: '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
    prep: ['节日档期紧张，提前两三周定', '家里人多，出菜我按波次走', '有供奉/习俗流程的提前讲'],
    tips: '正月里的菜色讲彩头：鱼要"年年有余"，丸子要"团团圆圆"。',
  },
  {
    code: 'seafood', name: '精品海鲜', subtitle: '当天采买，清蒸见功夫',
    intro: '海鲜这东西，新鲜占七成。我当天早市去挑，活的现杀现做。清蒸东星斑这种菜，火候差半分钟就不是那个味。',
    scenes: ['海鲜爱好者', '招待讲究的客人', '想换换口味'],
    menu: ['清蒸东星斑', '蒜蓉粉丝蒸扇贝', '白灼基围虾', '葱姜炒花蟹', '海鲜粥'],
    priceNote: '价格面议——海鲜随行就市，当天采买后跟师傅报实价。',
    prep: ['海鲜价格随行就市，菜单定了当天报实价', '对贝类过敏的提前说', '海鲜上桌要趁热，上菜节奏会快'],
    tips: '不接生食刺身以外的生腌，吃坏肚子的事咱不干。',
  },
  {
    code: 'sashimi', name: '海鲜姿造', subtitle: '上桌先拍照的冰盘',
    intro: '海鲜姿造说白了就是海鲜刺身摆出花样来。冰盘造型按您的场合来——生日摆寿字，商务摆大气，家里吃就实在点。',
    scenes: ['生日宴', '商务宴请', '想发朋友圈的场合'],
    menu: ['海鲜姿造冰盘', '刺身拼盘', '象拔蚌', '北极贝', '雕花摆盘'],
    priceNote: '价格面议——按食材和造型跟师傅谈。',
    prep: ['冰盘主题提前两天沟通', '需要大一点的餐桌', '家里有冰柜更好，没有我自带冰'],
    tips: '上桌先别急着动筷，拍完照我帮您分。',
  },
  {
    code: 'hotpot', name: '高端火锅', subtitle: '汤底现熬的火锅局',
    intro: '天冷了一家人围着火锅最热闹。我提前熬好汤底带过去，海鲜、和牛、手打虾滑，比外面火锅店吃得实在。',
    scenes: ['冬天家庭聚会', '海鲜火锅局', '打边炉'],
    menu: ['海鲜火锅', '打边炉', '和牛卷', '手打虾滑', '时蔬拼盘'],
    priceNote: '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
    prep: ['家里电磁炉就行，没有我带电火锅', '汤底当天现熬，到您家再开火', '有小朋友的备个清汤锅'],
    tips: '锅底料别扔，吃完给您留一份第二天下面条。',
  },
]

/** 单点菜单（与后端 menu_item 表一致，管理端可改） */
const menuItems = [
  { id: 1, category: 'cold', name: '锦绣三文鱼刺身', price: 298 },
  { id: 2, category: 'cold', name: '35年宝丰陈酿醉六月黄', price: 298, unit: '6只' },
  { id: 3, category: 'cold', name: '招牌脆皮乳鸽', price: 68, unit: '只' },
  { id: 4, category: 'cold', name: '陈派酒香罗氏虾', price: 108 },
  { id: 5, category: 'cold', name: '丘比椒辣八爪鱼', price: 78 },
  { id: 6, category: 'cold', name: '酒香自制牛腱', price: 78 },
  { id: 7, category: 'cold', name: '柠香去骨鸭掌', price: 68 },
  { id: 8, category: 'cold', name: '葱香脆皮鸡', price: 68 },
  { id: 9, category: 'cold', name: '樟树港辣椒捞腰片', price: 58 },
  { id: 10, category: 'cold', name: '老虎菜拌海蜇丝', price: 38 },
  { id: 11, category: 'cold', name: '金瓜慕斯糕', price: 39 },
  { id: 12, category: 'cold', name: '洛神花泡椒莴笋', price: 32 },
  { id: 13, category: 'cold', name: '夜香花拌绣球菌', price: 32 },
  { id: 14, category: 'cold', name: '虫草花拌龙须芽', price: 29 },
  { id: 15, category: 'cold', name: '荆芥拌青瓜', price: 29 },
  { id: 16, category: 'cold', name: '米醋七彩云南花生', price: 26 },
  { id: 17, category: 'cold', name: '油炸花生米', price: 19 },
  { id: 18, category: 'hot', name: '章丘大葱烧金沙参', price: 398 },
  { id: 19, category: 'hot', name: '家烧东海大黄鱼', price: 298 },
  { id: 20, category: 'hot', name: '铁棍山药烧东海鳗鱼肚', price: 298 },
  { id: 21, category: 'hot', name: '沙律芥味大虾球', price: 128 },
  { id: 22, category: 'hot', name: '川湘海鲜一品烩', price: 128 },
  { id: 23, category: 'hot', name: '陈派奶汤扒广肚', price: 108 },
  { id: 24, category: 'hot', name: '避风塘罗氏虾', price: 108 },
  { id: 25, category: 'hot', name: '风味椒盐罗氏虾', price: 108 },
  { id: 26, category: 'hot', name: '国宴红焖羊排', price: 108 },
  { id: 27, category: 'hot', name: '砂锅茄汁安格斯牛肋条', price: 98 },
  { id: 28, category: 'hot', name: '芸豆焖走山鸡', price: 98 },
  { id: 29, category: 'hot', name: '红胡椒猪尾', price: 88 },
  { id: 30, category: 'hot', name: '烟笋炒湖南腊肉', price: 68 },
  { id: 31, category: 'hot', name: '蜜椒松板肉', price: 68 },
  { id: 32, category: 'hot', name: '湖南小炒护心肉', price: 59 },
  { id: 33, category: 'hot', name: '刀板香洋水豆腐', price: 39 },
  { id: 34, category: 'hot', name: '浓汤玛咖菌', price: 38 },
  { id: 35, category: 'hot', name: '金瓜榴莲蜜薯', price: 36 },
  { id: 36, category: 'hot', name: '芦笋百合素什锦', price: 39 },
  { id: 37, category: 'hot', name: '生炒广东西兰花苔', price: 36 },
  { id: 38, category: 'hot', name: '西芹百合炒腰果', price: 39 },
  { id: 39, category: 'hot', name: '油淋广东菜心', price: 29 },
  { id: 40, category: 'staple', name: '特色酱肉包', price: 39 },
  { id: 41, category: 'staple', name: '农家摊咸食', price: 29 },
  { id: 42, category: 'staple', name: '什锦小菜蟒', price: 39 },
  { id: 43, category: 'staple', name: '冰网鲜虾大锅贴', price: 48 },
  { id: 44, category: 'staple', name: '葱香芝麻叶手工面', price: 12, unit: '碗' },
  { id: 45, category: 'staple', name: '广式靓汤', price: 39 },
  { id: 46, category: 'deposit', name: '古法豉汁蒸东星斑', price: 798, unit: '条' },
  { id: 47, category: 'deposit', name: '古法豉汁蒸老虎斑', price: 298, unit: '条' },
  { id: 48, category: 'deposit', name: '古法豉汁蒸多宝鱼', price: 158, unit: '条' },
  { id: 49, category: 'deposit', name: '古法豉汁蒸鲈鱼', price: 98, unit: '条' },
  { id: 50, category: 'deposit', name: '波士顿龙虾', price: 598, unit: '斤' },
  { id: 51, category: 'deposit', name: '白芍基尾虾', price: 88, unit: '斤' },
  { id: 52, category: 'deposit', name: '避风塘焗松叶蟹', price: 988, unit: '只' },
  { id: 53, category: 'deposit', name: '手打肉饼松叶蟹', price: 988, unit: '只' },
  { id: 54, category: 'deposit', name: '新疆红枣烧河鳗', price: 298, unit: '份' },
  { id: 55, category: 'deposit', name: '咸肉烧野生甲鱼', price: 368, unit: '份' },
  { id: 56, category: 'deposit', name: '罗氏大虾烧牛蛙', price: 198, unit: '份' },
  { id: 57, category: 'deposit', name: '江南烹汁八爪鱼', price: 128, unit: '份' },
  { id: 58, category: 'deposit', name: '鲜虾羊肚菌素什锦', price: 158, unit: '份' },
]

module.exports = { chef, cases, packages, slots, reviews, services, menuItems }


