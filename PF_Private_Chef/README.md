# PF_Private_Chef · 后端

上门私厨小程序（平顶山）的后端服务。

> 前端小程序在 **`PF_Private_Chef_XCX`**，两个目录配套使用。

---

## 技术栈

| 组件 | 版本 | 说明 |
|---|---|---|
| JDK | **17** | ⚠️ 见下方「环境坑」 |
| Spring Boot | 3.3.4 | |
| MyBatis-Plus | 3.5.7 | 单表 CRUD 全自动，无 XML |
| MySQL | 8.x | utf8mb4 |

---

## ⚠️ 环境坑（本机必看）

本机 `JAVA_HOME` 当前指向 **JDK 8**：

```
JAVA_HOME = C:\Program Files\Java\jdk1.8.0_181
```

Spring Boot 3 必须用 **JDK 17** 编译，直接用会报 `release version 17 not supported`。本机已有：

```
D:\DevelopTools\jdk-17
D:\DevelopTools\apache-maven-3.8.9\bin\mvn.cmd
```

**临时生效（当前终端窗口）**：

```powershell
$env:JAVA_HOME = "D:\DevelopTools\jdk-17"
mvn clean package -DskipTests
```

**永久生效（推荐）**：把系统环境变量 `JAVA_HOME` 改成 `D:\DevelopTools\jdk-17`，
并把 `%JAVA_HOME%\bin` 放在 PATH 里 `C:\Program Files\Common Files\Oracle\Java\javapath` 的**前面**。

---

## 启动步骤

### 1. 建库 + 灌数据

```bash
mysql -uroot -p < sql/schema.sql
mysql -uroot -p pf_private_chef < sql/data.sql
```

`schema.sql` 建 7 张表；`data.sql` 灌入与前端 mock 一致的示例数据
（王师傅、6 场宴席案例、4 档套餐、未来 21 天档期、3 条评价）。

### 2. 改数据库连接

`src/main/resources/application.yml`，或用环境变量覆盖（推荐）：

```bash
set DB_USER=root
set DB_PASSWORD=你的密码
```

### 3. 启动

```bash
$env:JAVA_HOME = "D:\DevelopTools\jdk-17"
mvn spring-boot:run
```

访问 <http://localhost:8080/api/chef> 应返回厨师名片的 JSON。

---

## 目录结构

```
src/main/java/com/pf/chef/
├─ PfChefApplication.java     启动类
├─ common/
│   ├─ R.java                 统一响应 {code, msg, data}
│   ├─ BizException.java      业务异常（msg 直接给用户看）
│   ├─ GlobalExceptionHandler.java
│   └─ TokenUtil.java         HMAC 签名的极简 token
├─ config/
│   ├─ WxProperties.java      微信 appid/secret/mock 开关
│   ├─ AuthInterceptor.java   登录态解析（当前"可选鉴权"）
│   ├─ AdminInterceptor.java  后台接口强制管理员 token
│   └─ WebConfig.java         跨域 + 拦截器注册 + /admin 转发
├─ entity/                    8 张表的实体
├─ mapper/                    8 个 BaseMapper
├─ dto/BookingCreateReq.java  预约入参（带校验注解）
├─ service/
│   ├─ WxAuthService.java     登录 + code2session
│   ├─ AdminAuthService.java  管理员登录 + 密码校验（加盐 SHA-256）
│   ├─ ContentService.java    厨师/案例/套餐/评价（只读）
│   ├─ SlotService.java       档期
│   └─ BookingService.java    预约（核心）
├─ controller/
│   ├─ AuthController.java    /api/auth
│   ├─ ContentController.java /api/chef /cases /packages /reviews
│   ├─ SlotController.java    /api/slots
│   ├─ BookingController.java /api/bookings
│   └─ AdminController.java   /api/admin  ← 后台（登录/统计/订单/定金/档期/评价）
└─ resources/
    ├─ application.yml
    └─ static/admin/index.html   ← 商家后台 H5（手机浏览器直接用）
```

---

## 接口清单

前端 `utils/api.js` 调的就是这些，**路径和方法完全对齐**。

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| GET | `/api/chef` | 厨师名片（品牌/数据条/服务项目） | 公开 |
| GET | `/api/cases?scene=&limit=` | 作品案例 | 公开 |
| GET | `/api/cases/{id}` | 作品详情 | 公开 |
| GET | `/api/packages` | 参考套餐（3 档，含起步价） | 公开 |
| GET | `/api/slots?from=&to=` | 档期 | 公开 |
| GET | `/api/reviews` | 客户评价（仅已授权） | 公开 |
| POST | `/api/auth/login` | 微信登录 `{code}` → `{token, openid}` | 公开 |
| POST | `/api/bookings` | 提交预约（含口味忌口/特殊需求） | 公开（待收紧） |
| GET | `/api/bookings/mine` | 我的预约 | 公开（待收紧） |
| POST | `/api/admin/login` | 管理员登录 → `{token}` | 公开 |
| GET | `/api/admin/stats` | 仪表盘：各状态单数 | 🔒 管理员 |
| GET | `/api/admin/bookings?status=&date=` | 后台：预约列表 | 🔒 管理员 |
| POST | `/api/admin/bookings/{id}/status` | 后台：改状态 | 🔒 管理员 |
| POST | `/api/admin/bookings/{id}/deposit` | 后台：标记定金已收/未收 | 🔒 管理员 |
| GET | `/api/admin/slots?from=&to=` | 后台：档期 | 🔒 管理员 |
| POST | `/api/admin/slots/{id}/status` | 后台：改档期 | 🔒 管理员 |
| GET | `/api/admin/reviews` | 后台：全部评价 | 🔒 管理员 |
| POST | `/api/admin/reviews/{id}/authorize?authorized=` | 后台：授权展示 | 🔒 管理员 |

### 统一响应格式

```json
{ "code": 0, "msg": "ok", "data": { } }
```

`code = 0` 成功，非 0 失败，`msg` 可直接展示给用户。

### 预约状态机

```
pending(待联系确认) → confirmed(已定档) → making(制作中) → done(已完成)
                    ↘ canceled(已取消)
```

定金状态独立于订单状态：`deposit_status` 0 未收 / 1 已收，商家线下收款后手动标记。

---

## 商家后台（厨师管理端）

**入口**：浏览器打开 `http://<主机IP>:8080/admin/`（手机、电脑都行，页面在 `src/main/resources/static/admin/`）。

**初始账号**：`admin / admin888`（种子数据里，上线前必须改）。

功能：
- 📋 预约单：客户称呼、电话（点击直接拨打）、人数、预算、地址、口味忌口、特殊需求、定金状态（标记已收/未收）、改单状态（确认接单 → 制作中 → 完成 / 取消）
- 🗓 档期：未来 14 天午/晚宴一键切换 可约/已满/关闭
- ⭐ 评价：查看全部评价并授权展示（授权后才出现在小程序首页）
- 📊 顶部数据条：待确认/已定档/制作中/今日场次

**手机访问**：手机与电脑连同一个 WiFi，用电脑的局域网 IP（`ipconfig` 查 IPv4 地址），
例如 `http://192.168.1.5:8080/admin/`。若打不开，在 Windows 防火墙放行 8080 端口。

**改管理员密码**：

```sql
-- 生成新哈希：SHA256("pfxg2026:新密码")，下面的例子是 admin999 的哈希
UPDATE admin_user SET password_hash = SHA2(CONCAT(salt, ':', '新密码'), 256) WHERE username = 'admin';
```

> 注意：token 有效期 30 天，签发密钥在环境变量 `PF_TOKEN_SECRET`（上线前必须设置）。

---

## 两个关键设计说明

### 1. 为什么档期没有"扣库存"

私厨的稀缺性是**一天只能接一场**，不是"还剩 3 份"。所以提交预约**不会**把档期改成 `full` ——
档期变满是**你在后台手动定档**后改的（`POST /api/admin/slots/{id}/status`）。

这样设计的原因：预约只是"意向"，需要电话确认菜单和报价才成立。
如果提交即锁档，客户随口提交一次就把档期占死了。

### 2. 防刷而不是防超卖

同一手机号同一天最多提交 3 单（`pf.booking.max-per-phone-per-day`），
挡的是误触和恶意刷单。真正的并发竞争在这个业态里不存在。

---

## 前端对接

改 `PF_Private_Chef_XCX/utils/api.js` 顶部：

```js
const config = {
  useMock: false,                              // ← 关掉假数据
  apiBase: 'http://localhost:8080',            // ← 指向本服务
}
```

> 开发者工具里记得勾选「不校验合法域名」，否则本地 http 请求会被拦。
> 上线时必须换成 **https + 已备案域名**，并加到小程序的 request 合法域名里。

---

## ⚠️ 上线前必须处理

| # | 事项 | 现状 |
|---|---|---|
| 1 | ~~后台接口零鉴权~~ | ✅ 已加管理员登录 + token 拦截（`/api/admin/**` 除登录外全部拦截），记得改默认密码 |
| 2 | **`wx.mock-login` 必须关闭** | 现在没配 appid 时会返回伪造 openid |
| 3 | **收紧「我的预约」** | 目前能用手机号查别人的预约，必须改成只认登录态 |
| 4 | **`TokenUtil.SECRET`** | 默认值硬编码，必须设环境变量 `PF_TOKEN_SECRET` |
| 5 | 数据库密码 | 别提交明文，用环境变量 |
| 6 | 小程序备案 + 类目 | 见项目根目录的合规方案文档 |
| 7 | 评价授权 | 默认 `authorized=0`，**必须逐个取得客户同意**才能展示 |
| 8 | 厨师资料占位内容 | `chef_profile` 里的姓名/电话/获奖为示例，替换成你哥的真实信息 |
