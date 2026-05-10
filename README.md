# Simple Car

Simple Car 是一个新能源汽车车主服务系统，包含车主端、后台管理端和 Spring Boot 后端服务。项目覆盖车辆管理、远程控制、充电服务、维保预约、道路救援、违章查询、社区动态和运营后台等功能。

## 项目结构

```text
simple-car/
├─ simple-car-client/    # 车主端前端，Vue 3 + Vite + Vant
├─ simple-car-admin/     # 后台管理前端，Vue 3 + Vite + TypeScript
├─ simple-car-server/    # 后端服务，Spring Boot 3 + MyBatis-Plus
├─ docs/                 # 项目设计和后续事项文档
└─ README.md
```

## 技术栈

- 车主端：Vue 3、Vue Router、Vuex、Vant、Axios、ECharts、Three.js
- 后台端：Vue 3、Vite、TypeScript、Vue Router、Axios、Lucide Icons
- 后端：Java 17、Spring Boot 3.1、Spring Security、JWT、MyBatis-Plus
- 数据库：MySQL 8

## 功能模块

- 账号认证：登录、注册、登录后修改密码、JWT 鉴权
- 车辆服务：车辆绑定、车辆信息、车辆状态、远程启动
- 数据分析：充电订单、车辆里程、图表分析
- 生活服务：充电站、维保计划、维保预约、道路救援、违章查询
- 社区功能：动态列表、发布动态、点赞、评论
- 后台管理：用户 CRUD、车辆按用户管理、充电站/服务站管理、预约和救援状态流转、社区内容管理

## 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8+

## 后端启动

1. 进入后端目录：

```powershell
cd simple-car-server
```

2. 复制示例配置并修改数据库密码：

```powershell
Copy-Item src/main/resources/application-example.yml src/main/resources/application.yml
```

3. 初始化数据库：

```powershell
cd src/main/resources
mysql -uroot -p < init_database.sql
```

`init_database.sql` 会执行 `rebuild_database.sql`，该脚本会删除并重建 `simple_car` 数据库。已有本地数据时请先备份。

4. 启动服务：

```powershell
cd ../../..
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080/dev-api
```

Swagger 地址：

```text
http://localhost:8080/dev-api/swagger-ui.html
```

## 车主端启动

```powershell
cd simple-car-client
npm install
npm run serve
```

车主端默认地址：

```text
http://localhost:4000
```

## 后台管理端启动

```powershell
cd simple-car-admin
npm install
npm run dev
```

后台默认地址：

```text
http://localhost:5174
```

前端通过 Vite 代理访问后端：

```text
/dev-api -> http://localhost:8080
```

## 默认测试账号

车主端：

```text
账号：13800000000
密码：123456

账号：13912345678
密码：123456
```

后台管理端：

```text
账号：admin
密码：123456
```

默认密码在 SQL 种子数据中以 `{bcrypt}` 哈希保存。生产环境请重新生成账号密码，并关闭或收紧 Swagger、跨域和演示数据。

## 认证与上传说明

- `/login`、`/register`、`/admin/auth/login` 为匿名接口。
- 忘记密码不再支持匿名直接重置；车主可登录后在账号安全中改密，未登录用户需联系管理员。
- `/common/upload` 需要登录后访问，避免匿名上传。
- `/uploads/**` 和 `/profile/**` 的 GET 访问会放行，用于浏览器读取已上传资源。
- 后端鉴权失败会返回真实 HTTP 状态码：未登录为 `401`，无后台权限为 `403`。

## 常用命令

后端测试：

```powershell
cd simple-car-server
mvn test
```

后端编译：

```powershell
cd simple-car-server
mvn -DskipTests compile
```

车主端构建：

```powershell
cd simple-car-client
npm run build
```

后台端构建：

```powershell
cd simple-car-admin
npm run build
```

## 配置说明

- `simple-car-server/src/main/resources/application.yml` 是本地配置文件，不建议提交。
- `simple-car-server/src/main/resources/application-example.yml` 是示例配置，可提交。
- 上传文件默认保存到 `uploads/`，该目录不提交。
- `node_modules/`、`dist/`、`target/` 等构建产物不提交。

## Git 说明

项目默认忽略接口文档、任务书、临时 Word 文件和普通 Markdown 文档；根目录 `README.md` 和 `docs/**/*.md` 是例外，会作为项目说明和设计文档保留。
