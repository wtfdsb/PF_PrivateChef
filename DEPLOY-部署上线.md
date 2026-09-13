# 上线部署指南（不用家里电脑常开）

目标：让小程序和商家后台 7×24 小时可访问，手机随时随地打开。

## 推荐方案：微信云托管（最省事）

**为什么选它**：免买服务器、免备案（默认域名可直接配置为小程序 request 合法域名）、
按量付费（最小实例为 0，没流量几乎不花钱）、自动重启、容器化一键部署。

### 你需要准备的
- 小程序管理员微信（能登录微信公众平台 + 微信云托管）
- 一个能收验证码的手机号（开通时实名）

### 第一步：数据库上云
1. 打开 <https://cloud.weixin.qq.com>，用小程序管理员微信扫码登录
2. 开通「云开发」→ 选择「按量付费」→ 创建环境
3. 环境里开通 **云数据库 MySQL**（或直接购买腾讯云 MySQL，两者互通）
4. 在数据库控制台新建库 `pf_private_chef`（utf8mb4）
5. 执行本仓库的 `PF_Private_Chef/sql/schema.sql` 和 `data.sql`（云数据库控制台支持直接执行 SQL）
6. 记下数据库的**内网地址、端口、账号、密码**（后面要填环境变量）

### 第二步：部署后端到云托管
1. 云开发控制台 → 「云托管」→ 创建服务
2. 部署方式选「代码仓库」→ 绑定 GitHub 账号 → 选仓库 `PF_Private_Chef`，
   构建目录填 `PF_Private_Chef`（仓库内已带 `Dockerfile`，自动多阶段构建，已配阿里云镜像加速）
3. 配置环境变量：

| 变量 | 值 |
|---|---|
| `DB_HOST` | 云数据库内网地址（形如 10.x.x.x） |
| `DB_PORT` | 3306 |
| `DB_NAME` | pf_private_chef |
| `DB_USER` | 云数据库账号 |
| `DB_PASSWORD` | 云数据库密码 |
| `WX_APPID` | wx45327e7181e4ce5a |
| `WX_SECRET` | 你新小程序的 AppSecret |
| `WX_MOCK_LOGIN` | false |
| `PF_TOKEN_SECRET` | 随机长字符串（乱敲一串） |
| `PF_NOTIFY_KEY` | Server酱 SendKey（新订单微信推送，也可配 PF_PUSHPLUS_TOKEN） |
| `TZ` | Asia/Shanghai |

4. 端口填 `8080`，最小实例数设 `0`（省成本，来请求自动拉起，冷启动约 5-10 秒）
5. 部署完成后记下**默认域名**（形如 `https://xxx.ap-shanghai.run.tcloudbase.com`）

### 第三步：小程序对接
1. 微信公众平台 → 开发管理 → 开发设置 → 服务器域名 → **request 合法域名**：
   添加云托管默认域名（免备案，直接加）
2. 改 `PF_Private_Chef_XCX/app.js`：
   ```js
   useMock: false,
   apiBase: 'https://xxx.ap-shanghai.run.tcloudbase.com',
   ```
3. 开发者工具重新编译 → 提审 → 发布

### 第四步：厨师管理后台（手机随时开）
- 地址：`https://xxx.ap-shanghai.run.tcloudbase.com/admin/`
- 账号 `admin` / 密码 `admin888`（**上线前必须改**，改法见后端 README）
- 微信里收藏这个链接即可，不需要任何电脑

### 成本预估
- 云托管：最小实例 0 + 按量，个人小流量每月几块钱以内
- 云数据库 MySQL：按量最低配，每月几十元
- 比买服务器 + 备案 + 电费划算得多

---

## 备选方案：腾讯云轻量服务器

- 约 100 元/年（2核2G），需要自己装 Docker + MySQL + Nginx
- **必须**买域名 + 做 ICP 备案（约 2-4 周）才能配 https 合法域名
- 适合以后想加多个服务、有技术同学维护的情况

## 备案提醒

- **小程序本身**无论用哪种方案都要备案（个人主体可备，微信公众平台里按引导提交，免费）
- 用云托管默认域名则**服务器域名免备案**，这是它最大的优势
