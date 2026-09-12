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
