# Simple Car

Simple Car 是一个新能源汽车车主服务系统，包含车主端、后台管理端和 Spring Boot 后端服务。项目覆盖车辆管理、远程控制、充电服务、维保预约、道路救援、违章查询、社区动态和运营后台等功能。

## 项目结构

```text
simple-car/
├─ simple-car-client/    # 车主端前端，Vue 2 + Vue CLI + Vant
├─ simple-car-admin/     # 后台管理前端，Vue 3 + Vite
├─ simple-car-server/    # 后端服务，Spring Boot 3 + MyBatis-Plus
└─ README.md
```

## 技术栈

- 车主端：Vue 2、Vue Router、Vuex、Vant、Axios、ECharts、Three.js
- 后台端：Vue 3、Vite、TypeScript、Vue Router、Axios、Lucide Icons
- 后端：Java 17、Spring Boot 3.1、Spring Security、JWT、MyBatis-Plus
- 数据库：MySQL 8

## 功能模块

- 账号认证：登录、注册、忘记密码、JWT 鉴权
- 车辆服务：车辆绑定、车辆信息、车辆状态、远程启动
- 数据分析：充电订单、车辆里程、图表分析
- 生活服务：充电站、维保计划、维保预约、救援请求、违章查询
- 社区功能：动态列表、发布动态、点赞、评论
- 后台管理：用户 CRUD、车辆按用户管理、充电站/服务站管理、预约和救援状态流转、社区内容管理

## 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8+

## 后端启动

1. 创建数据库：

```sql
CREATE DATABASE simple_car DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 进入后端目录：

```powershell
cd simple-car-server
```

3. 复制示例配置并修改数据库密码：

```powershell
Copy-Item src/main/resources/application-example.yml src/main/resources/application.yml
```

4. 按顺序执行数据库脚本：

```text
src/main/resources/schema.sql
src/main/resources/data.sql
src/main/resources/add_features.sql
src/main/resources/new_tables.sql
src/main/resources/create_notice_table.sql
src/main/resources/user_settings.sql
```

5. 启动服务：

```powershell
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

后台端通过 Vite 代理访问后端：

```text
/dev-api -> http://localhost:8080
```

## 默认测试账号

```text
账号：13800000000
密码：123456
```

也可以使用种子数据中的账号：

```text
账号：13912345678
密码：123456
```

## 常用命令

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

项目默认忽略接口文档、任务书、临时 Word 文件和普通 Markdown 文档；根目录 `README.md` 是例外，会作为项目说明保留。
