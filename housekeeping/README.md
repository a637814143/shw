# 家政服务管理系统 - 后端

基于Spring Boot + Vue的家政服务管理系统后端部分，专注于管理员角色功能。

## 功能特性

### 管理员功能
- ✅ 登录、个人信息、修改密码
- ✅ 首页：浏览系统公告
- ✅ 数据统计：统计充值、提现、服务者和用户数量，查看一周充值趋势图、服务预约量对比图
- ✅ 服务分类：管理家政服务的分类
- ✅ 用户管理：管理普通用户信息
- ✅ 家政服务者管理：管理家政服务者信息
- 🔄 服务预约：管理所有用户的服务预约情况，给预约订单分配家政人员
- 🔄 家政服务：管理家政服务
- 🔄 提现记录：管理家政人员的提现记录信息
- 🔄 充值记录：管理普通用户的充值记录信息
- 🔄 轮播图管理：管理轮播图、关联家政服务
- 🔄 居家贴士：管理居家贴士信息
- 🔄 服务评价：管理家政服务的评价信息
- 🔄 收藏管理：管理家政服务的收藏信息
- 🔄 管理员管理：管理管理员信息
- 🔄 服务者认证：管理和审核家政人员的认证信息

## 技术栈

- **后端框架**: Spring Boot 3.5.6
- **数据库**: MySQL 8.0+
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security + JWT
- **构建工具**: Maven
- **Java版本**: 17

## 项目结构

```
housekeeping/
├── src/main/java/com/example/housekeeping/
│   ├── config/          # 配置类
│   ├── controller/      # 控制器层
│   ├── dto/            # 数据传输对象
│   ├── entity/         # 实体类
│   ├── exception/      # 异常处理
│   ├── repository/     # 数据访问层
│   ├── security/       # 安全配置
│   ├── service/        # 业务逻辑层
│   └── util/           # 工具类
├── src/main/resources/
│   ├── sql/            # SQL脚本
│   ├── application.properties  # 应用配置
│   └── data.sql        # 初始数据
└── pom.xml            # Maven配置
```

## 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.6+

## 快速开始

### 1. 数据库准备

创建MySQL数据库：
```sql
CREATE DATABASE housekeeping_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 配置文件

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/housekeeping_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的数据库密码
```

### 3. 运行项目

使用Maven Wrapper运行：
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

或者使用IDE直接运行 `HousekeepingApplication.java`

### 4. 访问应用

- 应用地址: http://localhost:8080
- 默认管理员账号: admin / admin123

## API文档

### 管理员登录
```
POST /api/admin/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

### 获取当前管理员信息
```
GET /api/admin/profile
Authorization: Bearer {token}
```

### 获取基础统计数据
```
GET /api/admin/statistics/basic
Authorization: Bearer {token}
```

### 获取系统公告
```
GET /api/admin/notices
Authorization: Bearer {token}
```

## 数据库表结构

主要数据表：
- `admin` - 管理员表
- `user` - 普通用户表
- `provider` - 家政服务者表
- `service_category` - 服务分类表
- `housekeeping_service` - 家政服务表
- `service_booking` - 服务预约表
- `recharge_record` - 充值记录表
- `withdraw_record` - 提现记录表
- `banner` - 轮播图表
- `housekeeping_tip` - 居家贴士表
- `service_review` - 服务评价表
- `favorite` - 收藏表
- `system_notice` - 系统公告表

## 开发说明

### 添加新功能
1. 在 `entity` 包中创建实体类
2. 在 `repository` 包中创建数据访问接口
3. 在 `service` 包中创建业务逻辑类
4. 在 `controller` 包中创建控制器类

### 安全配置
- 使用JWT进行身份认证
- 管理员接口需要 `ROLE_ADMIN` 权限
- 密码使用BCrypt加密存储

## 注意事项

1. 确保MySQL服务已启动
2. 数据库连接信息配置正确
3. 首次运行会自动创建数据表并插入初始数据
4. 默认管理员密码为 `admin123`，建议首次登录后修改密码

## 后续开发计划

- [ ] 完善服务预约管理功能
- [ ] 实现家政服务管理
- [ ] 添加提现和充值记录管理
- [ ] 实现轮播图管理
- [ ] 添加居家贴士管理
- [ ] 完善服务评价和收藏管理
- [ ] 实现管理员管理功能
- [ ] 添加服务者认证管理
