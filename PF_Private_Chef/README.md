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
│   └─ WebConfig.java         跨域 + 拦截器注册
├─ entity/                    7 张表的实体
├─ mapper/                    7 个 BaseMapper
├─ dto/BookingCreateReq.java  预约入参（带校验注解）
├─ service/
│   ├─ WxAuthService.java     登录 + code2session
│   ├─ ContentService.java    厨师/案例/套餐/评价（只读）
│   ├─ SlotService.java       档期
│   └─ BookingService.java    预约（核心）
└─ controller/
    ├─ AuthController.java    /api/auth
    ├─ ContentController.java /api/chef /cases /packages /reviews
    ├─ SlotController.java    /api/slots
    ├─ BookingController.java /api/bookings
    └─ AdminController.java   /api/admin  ← 后台
```

---

## 接口清单

前端 `utils/api.js` 调的就是这些，**路径和方法完全对齐**。

| 方法 | 路径 | 说明 | 鉴权 |
|---|---|---|---|
| GET | `/api/chef` | 厨师名片 | 公开 |
| GET | `/api/cases?scene=&limit=` | 作品案例 | 公开 |
| GET | `/api/cases/{id}` | 作品详情 | 公开 |
| GET | `/api/packages` | 参考套餐 | 公开 |
| GET | `/api/slots?from=&to=` | 档期 | 公开 |
| GET | `/api/reviews` | 客户评价（仅已授权） | 公开 |
| POST | `/api/auth/login` | 微信登录 `{code}` → `{token, openid}` | 公开 |
| POST | `/api/bookings` | 提交预约 | 公开（待收紧） |
| GET | `/api/bookings/mine` | 我的预约 | 公开（待收紧） |
| GET | `/api/admin/bookings?status=&date=` | 后台：预约列表 | ⚠️ **无** |
| POST | `/api/admin/bookings/{id}/status` | 后台：改状态 | ⚠️ **无** |
| GET | `/api/admin/slots?from=&to=` | 后台：档期 | ⚠️ **无** |
| POST | `/api/admin/slots/{id}/status` | 后台：改档期 | ⚠️ **无** |
| GET | `/api/admin/reviews` | 后台：全部评价 | ⚠️ **无** |
| POST | `/api/admin/reviews/{id}/authorize?authorized=` | 后台：授权展示 | ⚠️ **无** |

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
| 1 | **后台接口零鉴权** | 任何人都能调 `/api/admin/**` 改档期、看所有客户手机号 |
| 2 | **`wx.mock-login` 必须关闭** | 现在没配 appid 时会返回伪造 openid |
| 3 | **收紧「我的预约」** | 目前能用手机号查别人的预约，必须改成只认登录态 |
| 4 | **`TokenUtil.SECRET`** | 硬编码的默认值，必须改成环境变量 `PF_TOKEN_SECRET` |
| 5 | 数据库密码 | 别提交明文，用环境变量 |
| 6 | 小程序备案 + 类目 | 见项目根目录的合规方案文档 |
| 7 | 评价授权 | 默认 `authorized=0`，**必须逐个取得客户同意**才能展示 |
