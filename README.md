# PF_PrivateChef · 私人厨师预约上门小程序

「上门私厨」小程序（平顶山）—— 作品展示 + 档期预约。个人使用。

## 仓库结构

| 目录 | 说明 | 技术栈 |
|---|---|---|
| [`PF_Private_Chef/`](PF_Private_Chef) | 后端服务 | Java 17 · Spring Boot 3.3 · MyBatis-Plus · MySQL 8 |
| [`PF_Private_Chef_XCX/`](PF_Private_Chef_XCX) | 微信小程序前端 | 原生小程序（WXML/WXSS/JS） |

- 后端说明、接口清单见 [`PF_Private_Chef/README.md`](PF_Private_Chef/README.md)
- 前端接口封装在 `PF_Private_Chef_XCX/utils/api.js`，与后端路径完全对齐

## 快速开始

### 后端

```powershell
mysql -uroot -p < PF_Private_Chef/sql/schema.sql
mysql -uroot -p pf_private_chef < PF_Private_Chef/sql/data.sql
$env:JAVA_HOME = "D:\DevelopTools\jdk-17"
cd PF_Private_Chef
mvn spring-boot:run
```

> ⚠️ Spring Boot 3 必须用 JDK 17，详见后端 README 的「环境坑」一节。

### 前端

用微信开发者工具打开 `PF_Private_Chef_XCX/` 目录，并在开发者工具中勾选「不校验合法域名」（本地联调 http 用）。
切换 mock / 真实接口：改 `PF_Private_Chef_XCX/app.js` 顶部的 `useMock`。

### 商家后台（厨师管理端）

后端启动后，浏览器打开 `http://localhost:8080/admin/`，初始账号 `admin / admin888`。
订单管理、档期、定金标记、评价授权都在里面；手机上用同一 WiFi 下电脑的局域网 IP 访问。
