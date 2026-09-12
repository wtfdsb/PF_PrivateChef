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
   '13XXXXXXXXX',
   1, 320, 4.9, '河南烹饪大赛获奖');

-- ---------- 作品案例 ----------
INSERT INTO work_case (scene, people, case_date, title, menu, highlight, ref_price, images, sort) VALUES
  ('家庭聚餐', 8, '2026-08-28', '家庭家宴 · 温馨一餐',
   '红烧黄河大鲤鱼、道口烧鸡、蒜香排骨、油焖大虾、清炒时蔬、手工馒头、小米粥',
   '一家人围坐的家常席，菜不花哨但道道是小时候的味道，老人孩子都吃得舒服',
   1280, '[]', 1),
  ('生日寿宴', 10, '2026-08-16', '父亲六十大寿 · 十全十美',
   '整只烤鸭、清蒸石斑、葱烧海参、四喜丸子、海米扒白菜、长寿面、寿桃包',
   '寿宴讲究"整"字：鱼整条、鸡整只，上菜顺序按老规矩来，老人特别满意',
   2600, '[]', 2),
  ('商务接待', 12, '2026-07-22', '商务私宴 · 高端接待',
   '清蒸东星斑、海鲜姿造冰盘、佛跳墙（简版）、扒广肚、炸八块、位上汤品',
   '客户要求"有面子不铺张"，豫菜硬菜撑场，人均控制在 300 出头',
   3888, '[]', 3),
  ('精品海鲜', 8, '2026-07-05', '清蒸东星斑 · 精品海鲜',
   '清蒸东星斑、蒜蓉粉丝蒸扇贝、白灼基围虾、葱姜炒花蟹、海鲜粥',
   '海鲜当日采买，清蒸最见火候，鱼肉嫩到筷子一碰就散',
   2680, '[]', 4),
  ('海鲜姿造', 10, '2026-06-18', '海鲜姿造 · 冰盘摆盘',
   '海鲜姿造冰盘、刺身拼盘、象拔蚌、北极贝、精美雕花摆盘',
   '冰盘造型按主题定制，上桌先拍照再动筷，朋友圈先吃',
   3280, '[]', 5),
  ('节日宴席', 16, '2026-05-30', '中秋家宴 · 十六人团圆席',
   '八凉八热、红烧狮子头、清炖甲鱼汤、白灼虾、蒜蓉蒸扇贝、糯米甜饭、月饼',
   '节日档期提前三周定档，一家十六口，菜量足、节奏慢、边吃边聊',
   2680, '[]', 6);

-- ---------- 参考套餐（三档） ----------
INSERT INTO menu_package (name, per_person, start_price, tag, dishes, note, sort) VALUES
  ('实惠家宴套餐', 0, 688, '家庭首选',
   '["豫菜经典","红烧类","汤羹","主食拼盘"]',
   '6-8人 · 适合家庭聚餐、朋友小聚。家常菜+豫菜经典+汤羹主食，荤素搭配、营养均衡，满满的烟火气。', 1),
  ('精品豫菜套餐', 0, 1288, '宴请优选',
   '["八大菜系","精品海鲜","宴席硬菜","位上汤品"]',
   '8-12人 · 适合生日寿宴、商务家宴。中高档热菜+精品海鲜+宴席硬菜，豫菜为主、融合八大菜系经典，撑得起场面。', 2),
  ('高端尊享私宴', 0, 0, '高端定制',
   '["海鲜姿造","高端火锅","海鲜宴席","定制菜单"]',
   '12人以上 · 适合商务接待、高端宴请、重要节日。海鲜姿造冰盘、高端海鲜火锅/打边炉、八大菜系精品大菜，一站式高端私宴，按需求定制报价。', 3);

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
