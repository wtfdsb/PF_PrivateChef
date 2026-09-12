-- ============================================================
-- 初始化数据（先把前端 mock 的内容灌进去，保证前后端一致）
-- 执行：mysql -uroot -p pf_private_chef < data.sql
-- ============================================================
USE pf_private_chef;

-- ---------- 厨师名片 ----------
INSERT INTO chef_profile
  (name, title, years, city, area, cuisines, intro, phone, health_cert,
   stat_banquets, stat_repeat_rate, stat_max_people)
VALUES
  ('王师傅', '中式宴席主厨', 15, '河南 · 平顶山',
   '平顶山全市（新华、卫东、湛河、石龙及各县市）',
   '["豫菜","家常宴席","融合菜","商务宴请"]',
   '从业 15 年，擅长中式宴席与家常硬菜。上门为您掌勺：从菜单设计、食材采买，到现场烹饪、上菜收尾，全流程负责。',
   '13XXXXXXXXX',
   1, 320, 68, 30);

-- ---------- 作品案例 ----------
INSERT INTO work_case (scene, people, case_date, title, menu, highlight, ref_price, images, sort) VALUES
  ('商务宴请', 12, '2026-08-28', '公司客户答谢宴 · 12 人',
   '清蒸黄河鲤鱼、道口烧鸡、葱烧海参、扒广肚、炸八块、蒜香排骨、油焖大虾、四凉四热',
   '客户要求"有面子但不铺张"，用豫菜硬菜撑场，人均控制在 300 出头',
   3888, '[]', 1),
  ('家宴', 8, '2026-08-16', '三代同堂家宴 · 8 人',
   '红烧肉、糖醋软溜黄河鲤、干炸小酥肉、蒜泥白肉、地三鲜、凉拌荆芥、手工馒头',
   '老人牙口不好，把硬菜做成软烂口，小孩那份单独做了不辣的',
   1280, '[]', 2),
  ('生日宴', 10, '2026-07-22', '父亲六十大寿 · 10 人',
   '整只烤鸭、清蒸石斑、佛跳墙（简版）、油焖大虾、海米扒白菜、长寿面',
   '寿宴讲究"整"字，鱼要整条、鸡要整只，上菜顺序也按老规矩来',
   2600, '[]', 3),
  ('满月宴', 20, '2026-07-05', '宝宝满月宴 · 20 人',
   '八凉八热、扒肘子、四喜丸子、清蒸鲈鱼、糯米甜饭、红鸡蛋',
   '家里老人多、孩子多，整体口味偏清淡，特意备了两道甜口',
   3200, '[]', 4),
  ('同学聚会', 14, '2026-06-18', '毕业十年同学聚会 · 14 人',
   '羊肉串、烤羊排、大盘鸡、香辣蟹、手撕包菜、拍黄瓜、冰镇酸梅汤',
   '氛围局，主打硬菜配酒，出菜节奏压慢，边喝边上',
   1800, '[]', 5),
  ('乔迁宴', 16, '2026-05-30', '新房乔迁 · 16 人',
   '红烧狮子头、清炖狮子头汤、白灼虾、蒜蓉粉丝蒸扇贝、锅塌豆腐、四喜烤麸',
   '新房厨房不熟，提前一小时到场熟悉灶台和燃气',
   2200, '[]', 6);

-- ---------- 参考套餐 ----------
INSERT INTO menu_package (name, per_person, tag, dishes, note, sort) VALUES
  ('家常宴席', 150, '家宴 · 朋友聚',
   '["四凉六热","一道整鱼","一道汤","主食 + 果盘"]',
   '家常硬菜为主，适合 6-12 人', 1),
  ('体面宴席', 300, '生日 · 乔迁 · 答谢',
   '["六凉八热","整鱼整鸡","海参或鲍鱼一道","两道汤","主食 + 甜点 + 果盘"]',
   '有排面也压得住预算，最常被选的档位', 2),
  ('商务宴请', 500, '商务 · 重要客人',
   '["八凉十热","高档海鲜 2-3 道","参鲍类一道","三道汤","主食 + 甜点 + 果盘 + 茶"]',
   '上菜节奏与摆盘会特别设计，适合招待重要客人', 3),
  ('定制菜单', 0, '上不封顶',
   '["按您的要求定制"]',
   '高端食材、特殊菜系、外请帮厨，均可单独沟通', 4);

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
  ('张女士', '家宴', 5, '菜做得比饭店还地道，厨房收拾得比来之前还干净。', 0, 1),
  ('李先生', '商务宴请', 5, '客人一直夸，说在哪家饭店都没吃到这个味道。', 0, 2),
  ('王先生', '生日宴', 5, '提前一小时就来熟悉灶台了，很专业，老人很满意。', 0, 3);
