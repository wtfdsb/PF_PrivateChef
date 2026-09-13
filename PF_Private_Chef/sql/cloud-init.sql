-- ============================================================
-- 云托管一键初始化：建库 + 建表 + 种子数据（由 schema.sql + data.sql 自动合并生成）
-- 用法：云开发控制台 -> 云数据库 -> SQL 执行 -> 粘贴本文件全部内容 -> 执行
-- ============================================================

-- ============================================================
-- PF_Private_Chef 数据库结构（MySQL 8.x / utf8mb4）
-- 执行：mysql -uroot -p < schema.sql
-- ============================================================
CREATE DATABASE IF NOT EXISTS pf_private_chef
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE pf_private_chef;

-- ------------------------------------------------------------
-- 微信用户
-- ------------------------------------------------------------
DROP TABLE IF EXISTS wx_user;
CREATE TABLE wx_user (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  openid      VARCHAR(64)  NOT NULL COMMENT '微信 openid',
  unionid     VARCHAR(64)  DEFAULT NULL,
  phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号（授权后写入）',
  nickname    VARCHAR(64)  DEFAULT NULL,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_openid (openid)
) ENGINE=InnoDB COMMENT='微信用户';

-- ------------------------------------------------------------
-- 厨师名片（单店，实际只有一条）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS chef_profile;
CREATE TABLE chef_profile (
  id                BIGINT       NOT NULL AUTO_INCREMENT,
  name              VARCHAR(32)  NOT NULL COMMENT '姓名/称呼',
  brand             VARCHAR(64)  DEFAULT NULL COMMENT '品牌名，如 新谷私厨',
  title             VARCHAR(64)  DEFAULT NULL COMMENT '头衔',
  years             INT          NOT NULL DEFAULT 0 COMMENT '从厨年限',
  city              VARCHAR(64)  DEFAULT NULL,
  area              VARCHAR(255) DEFAULT NULL COMMENT '服务范围',
  cuisines          TEXT         DEFAULT NULL COMMENT '擅长菜系，JSON 数组，如 ["豫菜","家常宴席"]',
  intro             VARCHAR(500) DEFAULT NULL COMMENT '自我介绍',
  phone             VARCHAR(20)  DEFAULT NULL COMMENT '对外电话（首页"电话咨询"用）',
  health_cert       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否持健康证',
  stat_banquets     INT          NOT NULL DEFAULT 0 COMMENT '累计宴席场次',
  stat_rating       DECIMAL(2,1) NOT NULL DEFAULT 5.0 COMMENT '客户评分（0-5）',
  awards            VARCHAR(255) DEFAULT NULL COMMENT '获奖/头衔，如 河南烹饪大赛获奖',
  status            TINYINT      NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
  deleted           TINYINT      NOT NULL DEFAULT 0,
  created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='厨师名片';

-- ------------------------------------------------------------
-- 宴席作品案例
-- ------------------------------------------------------------
DROP TABLE IF EXISTS work_case;
CREATE TABLE work_case (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  scene       VARCHAR(32)  NOT NULL COMMENT '场景：商务宴请/家宴/生日宴/满月宴/同学聚会/乔迁宴',
  people      INT          NOT NULL DEFAULT 0 COMMENT '人数',
  case_date   DATE         DEFAULT NULL COMMENT '举办日期',
  title       VARCHAR(128) NOT NULL COMMENT '标题',
  menu        VARCHAR(1000) DEFAULT NULL COMMENT '菜单',
  highlight   VARCHAR(500) DEFAULT NULL COMMENT '这一场的关键点',
  ref_price   INT          NOT NULL DEFAULT 0 COMMENT '参考价（元）',
  images      TEXT         DEFAULT NULL COMMENT '图片地址 JSON 数组，如 ["https://..."]',
  sort        INT          NOT NULL DEFAULT 0 COMMENT '排序，越小越前',
  status      TINYINT      NOT NULL DEFAULT 1 COMMENT '1上架 0下架',
  deleted     TINYINT      NOT NULL DEFAULT 0,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_scene (scene),
  KEY idx_sort (sort)
) ENGINE=InnoDB COMMENT='宴席作品案例';

-- ------------------------------------------------------------
-- 参考套餐档位
-- ------------------------------------------------------------
DROP TABLE IF EXISTS menu_package;
CREATE TABLE menu_package (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(64)  NOT NULL,
  per_person  INT          NOT NULL DEFAULT 0 COMMENT '人均参考价（旧字段，保留兼容）',
  start_price INT          NOT NULL DEFAULT 0 COMMENT '套餐起步价（总价，元）；0 表示面议定制',
  tag         VARCHAR(64)  DEFAULT NULL,
  dishes      TEXT         DEFAULT NULL COMMENT '菜品说明 JSON 数组',
  note        VARCHAR(255) DEFAULT NULL,
  sort        INT          NOT NULL DEFAULT 0,
  status      TINYINT      NOT NULL DEFAULT 1,
  deleted     TINYINT      NOT NULL DEFAULT 0,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_sort (sort)
) ENGINE=InnoDB COMMENT='参考套餐档位';

-- ------------------------------------------------------------
-- 服务项目详情（首页「服务项目」每个入口的内容页）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS service_item;
CREATE TABLE service_item (
  id           BIGINT       NOT NULL AUTO_INCREMENT,
  code         VARCHAR(32)  NOT NULL COMMENT '唯一标识，如 family/birthday/business',
  name         VARCHAR(32)  NOT NULL COMMENT '服务名称',
  subtitle     VARCHAR(64)  DEFAULT NULL COMMENT '一句话副标题',
  intro        VARCHAR(500) DEFAULT NULL COMMENT '介绍（说人话，别端着）',
  scenes       TEXT         DEFAULT NULL COMMENT '适合场景 JSON 数组',
  menu         TEXT         DEFAULT NULL COMMENT '常做的菜 JSON 数组',
  price_note   VARCHAR(255) DEFAULT NULL COMMENT '价格说明',
  prep         TEXT         DEFAULT NULL COMMENT '需要您准备的 JSON 数组',
  tips         VARCHAR(500) DEFAULT NULL COMMENT '小提示',
  sort         INT          NOT NULL DEFAULT 0,
  status       TINYINT      NOT NULL DEFAULT 1,
  deleted      TINYINT      NOT NULL DEFAULT 0,
  created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB COMMENT='服务项目详情';

-- ------------------------------------------------------------
-- 档期
-- ------------------------------------------------------------
DROP TABLE IF EXISTS schedule_slot;
CREATE TABLE schedule_slot (
  id          BIGINT      NOT NULL AUTO_INCREMENT,
  slot_date   DATE        NOT NULL COMMENT '日期',
  meal        VARCHAR(16) NOT NULL COMMENT '午宴/晚宴',
  status      VARCHAR(16) NOT NULL DEFAULT 'open' COMMENT 'open可约 full已满 closed关闭',
  remark      VARCHAR(255) DEFAULT NULL,
  created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_date_meal (slot_date, meal),
  KEY idx_date (slot_date)
) ENGINE=InnoDB COMMENT='档期';

-- ------------------------------------------------------------
-- 预约单
-- ------------------------------------------------------------
DROP TABLE IF EXISTS booking;
CREATE TABLE booking (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  booking_no    VARCHAR(32)  NOT NULL COMMENT '预约单号',
  user_id       BIGINT       DEFAULT NULL COMMENT 'wx_user.id',
  category      VARCHAR(32)  DEFAULT NULL COMMENT '服务项目 code（从哪个入口进来约的）',
  name          VARCHAR(32)  NOT NULL COMMENT '联系人',
  phone         VARCHAR(20)  NOT NULL,
  slot_date     DATE         NOT NULL,
  meal          VARCHAR(16)  NOT NULL,
  people        INT          NOT NULL,
  budget        VARCHAR(32)  DEFAULT NULL COMMENT '预算区间',
  address       VARCHAR(255) NOT NULL COMMENT '场地地址',
  remark        VARCHAR(500) DEFAULT NULL COMMENT '其他备注',
  taste         VARCHAR(500) DEFAULT NULL COMMENT '口味与忌口（不吃辣/海鲜过敏/老人孩子多等）',
  needs         VARCHAR(500) DEFAULT NULL COMMENT '特殊需求（摆盘仪式/酒水代办/餐后收拾/代采购等）',
  deposit_status TINYINT     NOT NULL DEFAULT 0 COMMENT '定金状态 0未收 1已收（线下收款后商家手动标记）',
  source        VARCHAR(64)  DEFAULT NULL COMMENT '介绍人',
  channel       VARCHAR(32)  DEFAULT NULL COMMENT '来源渠道 direct/share',
  status        VARCHAR(16)  NOT NULL DEFAULT 'pending'
                COMMENT 'pending待确认 confirmed已定档 making制作中 done已完成 canceled已取消',
  admin_remark  VARCHAR(500) DEFAULT NULL COMMENT '商家备注',
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_booking_no (booking_no),
  KEY idx_phone (phone),
  KEY idx_slot (slot_date, meal),
  KEY idx_status (status)
) ENGINE=InnoDB COMMENT='预约单';

-- ------------------------------------------------------------
-- 管理员（商家后台登录）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS admin_user;
CREATE TABLE admin_user (
  id            BIGINT       NOT NULL AUTO_INCREMENT,
  username      VARCHAR(32)  NOT NULL,
  password_hash VARCHAR(64)  NOT NULL COMMENT 'SHA-256(salt:password) 十六进制',
  salt          VARCHAR(32)  NOT NULL,
  created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='商家后台管理员';

-- ------------------------------------------------------------
-- 客户评价
-- ------------------------------------------------------------
DROP TABLE IF EXISTS review;
CREATE TABLE review (
  id          BIGINT       NOT NULL AUTO_INCREMENT,
  name        VARCHAR(32)  NOT NULL,
  scene       VARCHAR(32)  DEFAULT NULL,
  score       INT          NOT NULL DEFAULT 5,
  content     VARCHAR(500) NOT NULL,
  authorized  TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '客户是否授权展示',
  sort        INT          NOT NULL DEFAULT 0,
  status      TINYINT      NOT NULL DEFAULT 1,
  created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB COMMENT='客户评价';

-- ============================================================
-- 初始化数据（与前端 utils/mock.js 保持一致，保证前后端一致）
-- 执行：mysql -uroot -p pf_private_chef < data.sql
-- ⚠️ 厨师姓名、电话、案例照片为占位内容，上线前必须替换
-- ============================================================
USE pf_private_chef;

-- ---------- 厨师名片 ----------
INSERT INTO chef_profile
  (name, brand, title, years, city, area, cuisines, intro, phone, health_cert,
   stat_banquets, stat_rating, awards)
VALUES
  ('王师傅', '新谷私厨', '豫菜名师 · 上门家宴', 10, '河南 · 平顶山',
   '平顶山市区（偏远区域加收上门里程费）',
   '["豫菜","家常宴席","海鲜姿造","高端火锅","商务宴请","融合菜","淮扬菜","川菜"]',
   '从业十年，豫菜出身，把星级宴席搬进您家：八大菜系、精品海鲜、海鲜姿造、高端火锅，名厨亲自掌勺。从菜单设计、食材采买，到现场烹饪、上菜收尾，全流程负责。',
   '15903697992',
   1, 320, 4.9, '河南烹饪大赛获奖');

-- ---------- 作品案例 ----------
INSERT INTO work_case (scene, people, case_date, title, menu, highlight, ref_price, images, sort) VALUES
  ('家庭聚餐', 8, '2026-08-28', '家庭家宴 · 温馨一餐',
   '红烧黄河大鲤鱼、道口烧鸡、蒜香排骨、油焖大虾、清炒时蔬、手工馒头、小米粥',
   '一家人围坐的家常席，菜不花哨但道道是小时候的味道，老人孩子都吃得舒服',
   1280, '["/images/case-1.jpg"]', 1),
  ('生日寿宴', 10, '2026-08-16', '父亲六十大寿 · 十全十美',
   '整只烤鸭、清蒸石斑、葱烧海参、四喜丸子、海米扒白菜、长寿面、寿桃包',
   '寿宴讲究"整"字：鱼整条、鸡整只，上菜顺序按老规矩来，老人特别满意',
   2600, '["/images/case-2.jpg"]', 2),
  ('商务接待', 12, '2026-07-22', '商务私宴 · 高端接待',
   '清蒸东星斑、海鲜姿造冰盘、佛跳墙（简版）、扒广肚、炸八块、位上汤品',
   '客户要求"有面子不铺张"，豫菜硬菜撑场，人均控制在 300 出头',
   3888, '["/images/case-3.jpg"]', 3),
  ('精品海鲜', 8, '2026-07-05', '清蒸东星斑 · 精品海鲜',
   '清蒸东星斑、蒜蓉粉丝蒸扇贝、白灼基围虾、葱姜炒花蟹、海鲜粥',
   '海鲜当日采买，清蒸最见火候，鱼肉嫩到筷子一碰就散',
   2680, '["/images/case-4.jpg"]', 4),
  ('海鲜姿造', 10, '2026-06-18', '海鲜姿造 · 冰盘摆盘',
   '海鲜姿造冰盘、刺身拼盘、象拔蚌、北极贝、精美雕花摆盘',
   '冰盘造型按主题定制，上桌先拍照再动筷，朋友圈先吃',
   3280, '["/images/case-5.jpg"]', 5),
  ('节日宴席', 16, '2026-05-30', '中秋家宴 · 十六人团圆席',
   '八凉八热、红烧狮子头、清炖甲鱼汤、白灼虾、蒜蓉蒸扇贝、糯米甜饭、月饼',
   '节日档期提前三周定档，一家十六口，菜量足、节奏慢、边吃边聊',
   2680, '["/images/case-6.jpg"]', 6);

-- ---------- 参考套餐（三档） ----------
INSERT INTO menu_package (name, per_person, start_price, tag, dishes, note, sort) VALUES
  ('实惠家宴套餐', 0, 0, '家庭首选',
   '["豫菜经典","红烧类","汤羹","主食拼盘"]',
   '6-8人 · 适合家庭聚餐、朋友小聚。家常菜+豫菜经典+汤羹主食，荤素搭配、营养均衡，满满的烟火气。', 1),
  ('精品豫菜套餐', 0, 0, '宴请优选',
   '["八大菜系","精品海鲜","宴席硬菜","位上汤品"]',
   '8-12人 · 适合生日寿宴、商务家宴。中高档热菜+精品海鲜+宴席硬菜，豫菜为主、融合八大菜系经典，撑得起场面。', 2),
  ('高端尊享私宴', 0, 0, '高端定制',
   '["海鲜姿造","高端火锅","海鲜宴席","定制菜单"]',
   '12人以上 · 适合商务接待、高端宴请、重要节日。海鲜姿造冰盘、高端海鲜火锅/打边炉、八大菜系精品大菜，一站式高端私宴，按需求定制报价。', 3);

-- ---------- 服务项目详情（首页每个入口的内容页） ----------
INSERT INTO service_item (code, name, subtitle, intro, scenes, menu, price_note, prep, tips, sort) VALUES
  ('family', '家庭聚餐', '一家人吃顿好的',
   '不用下馆子等位，也不用我妈在厨房忙活一下午。您家的灶台、您家的碗筷，我上门把菜做了，吃完我把厨房归置干净再走。',
   '["周末团圆","爸妈来住几天","孩子考完试犒劳一下"]',
   '["红烧黄河大鲤鱼","道口烧鸡","蒜香排骨","油焖大虾","清炒时蔬","手工馒头","小米粥"]',
   '价格面议——菜单和人数定下来，跟师傅直接谈；食材实报实销，小票给您看。',
   '["有灶有锅、厨房能进人就行","老房子灶火小的提前说，我带电灶","碗筷餐具用您家里的"]',
   '老人孩子多的提前说一声，软烂口和不辣的分开做，不用一桌人迁就一个人。', 1),
  ('birthday', '生日寿宴', '寿面寿桃，按老规矩来',
   '给老人过大寿、给孩子过满月，这顿饭讲究的是个"整"字。鱼整条、鸡整只，上菜顺序我也按老规矩排。',
   '["老人六十/七十大寿","孩子满月、周岁","家里长辈过生日"]',
   '["整只烤鸭","清蒸石斑","葱烧海参","四喜丸子","长寿面","寿桃包"]',
   '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
   '["长寿面用您家灶现擀现下","蛋糕您自己定，我负责热菜","提前告诉我寿星忌口"]',
   '寿宴图个热闹，出菜节奏我会压慢一点，让大家边吃边聊。', 2),
  ('business', '商务接待', '有面子，不铺张',
   '招待客户这顿饭，难点不在菜贵，在于有分寸。硬菜撑场面，人均还压得住，客人夸"比饭店强"，您脸上也有光。',
   '["招待客户","答谢合作伙伴","新同事接风"]',
   '["清蒸东星斑","海鲜姿造冰盘","佛跳墙（简版）","扒广肚","炸八块","位上汤品"]',
   '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
   '["最好有单独的会客区域","酒水您备，或者我按清单代办","需要提前半小时到场的说一声"]',
   '上菜节奏跟着您谈事的进度走，不催不赶，该上汤的时候上汤。', 3),
  ('festival', '节日宴席', '过年过节，在家摆一桌',
   '中秋、春节、乔迁、满月，这些日子在家吃才叫过节。我提前到，您该接客接客，厨房交给我。',
   '["中秋团圆","春节家宴","乔迁、满月"]',
   '["八凉八热","红烧狮子头","清炖甲鱼汤","白灼虾","蒜蓉蒸扇贝","糯米甜饭"]',
   '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
   '["节日档期紧张，提前两三周定","家里人多，出菜我按波次走","有供奉/习俗流程的提前讲"]',
   '正月里的菜色讲彩头：鱼要"年年有余"，丸子要"团团圆圆"。', 4),
  ('seafood', '精品海鲜', '当天采买，清蒸见功夫',
   '海鲜这东西，新鲜占七成。我当天早市去挑，活的现杀现做。清蒸东星斑这种菜，火候差半分钟就不是那个味。',
   '["海鲜爱好者","招待讲究的客人","想换换口味"]',
   '["清蒸东星斑","蒜蓉粉丝蒸扇贝","白灼基围虾","葱姜炒花蟹","海鲜粥"]',
   '价格面议——海鲜随行就市，当天采买后跟师傅报实价。',
   '["海鲜价格随行就市，菜单定了当天报实价","对贝类过敏的提前说","海鲜上桌要趁热，上菜节奏会快"]',
   '不接生食刺身以外的生腌，吃坏肚子的事咱不干。', 5),
  ('sashimi', '海鲜姿造', '上桌先拍照的冰盘',
   '海鲜姿造说白了就是海鲜刺身摆出花样来。冰盘造型按您的场合来——生日摆寿字，商务摆大气，家里吃就实在点。',
   '["生日宴","商务宴请","想发朋友圈的场合"]',
   '["海鲜姿造冰盘","刺身拼盘","象拔蚌","北极贝","雕花摆盘"]',
   '价格面议——按食材和造型跟师傅谈。',
   '["冰盘主题提前两天沟通","需要大一点的餐桌","家里有冰柜更好，没有我自带冰"]',
   '上桌先别急着动筷，拍完照我帮您分。', 6),
  ('hotpot', '高端火锅', '汤底现熬的火锅局',
   '天冷了一家人围着火锅最热闹。我提前熬好汤底带过去，海鲜、和牛、手打虾滑，比外面火锅店吃得实在。',
   '["冬天家庭聚会","海鲜火锅局","打边炉"]',
   '["海鲜火锅","打边炉","和牛卷","手打虾滑","时蔬拼盘"]',
   '价格面议——按菜单和人数跟师傅谈，食材实报实销。',
   '["家里电磁炉就行，没有我带电火锅","汤底当天现熬，到您家再开火","有小朋友的备个清汤锅"]',
   '锅底料别扔，吃完给您留一份第二天下面条。', 7);

-- ---------- 档期：自动生成未来 21 天 × 午/晚宴 ----------
-- 规则与前端 mock 一致：周末晚宴默认已满，其余可约
INSERT INTO schedule_slot (slot_date, meal, status)
SELECT d.d,
       m.meal,
       CASE WHEN DAYOFWEEK(d.d) IN (1, 7) AND m.meal = '晚宴' THEN 'full' ELSE 'open' END
FROM (
  SELECT DATE_ADD(CURDATE(), INTERVAL n DAY) AS d
  FROM (
    SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
    UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14
    UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION SELECT 21
  ) nums
) d
CROSS JOIN (SELECT '午宴' AS meal UNION SELECT '晚宴') m;

-- ---------- 客户评价（默认未授权展示，上线前须逐个取得同意） ----------
INSERT INTO review (name, scene, score, content, authorized, sort) VALUES
  ('张女士', '家庭聚餐', 5, '菜做得比饭店还地道，厨房收拾得比来之前还干净。', 0, 1),
  ('李先生', '商务接待', 5, '客人一直夸，说在哪家饭店都没吃到这个味道。', 0, 2),
  ('王先生', '生日寿宴', 5, '提前一小时就来熟悉灶台了，很专业，老人很满意。', 0, 3);

-- ---------- 管理员（商家后台） ----------
-- 初始账号 admin / admin888 —— 上线前务必修改！
-- password_hash = SHA256("pfxg2026:admin888") 十六进制
INSERT INTO admin_user (username, password_hash, salt) VALUES
  ('admin', '23e4f13c8df4c37130873c7b8277dd13ab6b6f994707b6659c43740cf03685bd', 'pfxg2026');
