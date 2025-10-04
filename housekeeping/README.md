# 家政服务管理系统后端

基于Spring Boot + Vue的家政服务管理系统后端API。

## 功能特性

### 管理员功能
- 登录、个人信息、修改密码
- 首页：浏览系统公告
- 数据统计：统计充值、提现、服务者和用户数量，查看一周充值趋势图、服务预约量对比图
- 服务预约：管理所有用户的服务预约情况，给预约订单分配家政人员
- 服务分类：管理家政服务的分类
- 家政服务：管理家政服务
- 提现记录：管理家政人员的提现记录信息
- 充值记录：管理普通用户的充值记录信息
- 轮播图管理：管理轮播图、关联家政服务
- 居家贴士：管理居家贴士信息
- 服务评价：管理家政服务的评价信息
- 收藏管理：管理家政服务的收藏信息
- 用户管理：管理普通用户信息
- 家政服务者管理：管理家政服务者信息
- 管理员管理：管理管理员信息
- 服务者认证：管理和审核家政人员的认证信息

### 家政人员功能
- 注册、登录、个人信息、修改密码
- 首页：浏览系统公告
- 服务者认证：提交材料进行家政服务者认证
- 家政服务：查看所有家政服务信息
- 家政预约：查看分配给自己的预约信息，并处理服务状态
- 提现记录：查看自己的提现记录，申请提现
- 服务评价：查看自己的家政服务评价信息

### 用户功能
- 注册、登录、个人信息、修改密码
- 首页：查看轮播图、查看家政服务分类、查看热门居家小贴士、查看系统公告
- 家政服务：通过分类展示家政服务信息，点击进入详情页
- 家政服务：分类查看所有的家政服务信息，可预约
- 居家小贴士：查看所有居家小贴士
- 家政预约：查看和管理自己的预约信息，可取消、可评价
- 我的收藏：查看和管理自己收藏的家政服务信息
- 充值记录：查看自己的充值记录、充值金额

## 技术栈

- **后端框架**: Spring Boot 3.5.6
- **数据库**: MySQL 8.0
- **ORM框架**: Spring Data JPA
- **安全框架**: Spring Security + JWT
- **构建工具**: Maven
- **Java版本**: 17

## 项目结构

```
src/main/java/com/example/housekeeping/
├── config/                 # 配置类
│   ├── JpaConfig.java
│   └── SecurityConfig.java
├── controller/             # 控制器层
│   ├── AuthController.java
│   ├── FileController.java
│   ├── HousekeepingServiceController.java
│   ├── ServiceCategoryController.java
│   ├── ServiceProviderController.java
│   └── UserController.java
├── entity/                 # 实体类
│   ├── Admin.java
│   ├── Banner.java
│   ├── BaseEntity.java
│   ├── Favorite.java
│   ├── HousekeepingService.java
│   ├── HousekeepingTip.java
│   ├── ProviderCertification.java
│   ├── RechargeRecord.java
│   ├── ServiceBooking.java
│   ├── ServiceCategory.java
│   ├── ServiceProvider.java
│   ├── ServiceReview.java
│   ├── SystemNotice.java
│   ├── User.java
│   └── WithdrawRecord.java
├── exception/              # 异常处理
│   ├── BusinessException.java
│   └── RestExceptionHandler.java
├── repository/             # 数据访问层
│   ├── AdminRepository.java
│   ├── BannerRepository.java
│   ├── FavoriteRepository.java
│   ├── HousekeepingServiceRepository.java
│   ├── HousekeepingTipRepository.java
│   ├── ProviderCertificationRepository.java
│   ├── RechargeRecordRepository.java
│   ├── ServiceBookingRepository.java
│   ├── ServiceCategoryRepository.java
│   ├── ServiceProviderRepository.java
│   ├── ServiceReviewRepository.java
│   ├── SystemNoticeRepository.java
│   ├── UserRepository.java
│   └── WithdrawRecordRepository.java
├── security/               # 安全相关
│   ├── JwtAuthenticationEntryPoint.java
│   └── JwtAuthenticationFilter.java
├── service/                # 服务层
│   ├── AuthService.java
│   ├── HousekeepingServiceService.java
│   ├── ServiceCategoryService.java
│   ├── ServiceProviderService.java
│   └── UserService.java
├── util/                   # 工具类
│   ├── FileUtil.java
│   ├── HtmlUtil.java
│   └── JwtUtil.java
├── common/                 # 公共类
│   ├── PageResult.java
│   └── Result.java
└── HousekeepingApplication.java  # 启动类
```

## 数据库设计

系统包含以下主要数据表：

- `admin` - 管理员表
- `user` - 普通用户表
- `service_provider` - 家政服务者表
- `service_category` - 服务分类表
- `housekeeping_service` - 家政服务表
- `service_booking` - 服务预约表
- `service_review` - 服务评价表
- `favorite` - 收藏表
- `recharge_record` - 充值记录表
- `withdraw_record` - 提现记录表
- `banner` - 轮播图表
- `housekeeping_tip` - 居家贴士表
- `system_notice` - 系统公告表
- `provider_certification` - 服务者认证材料表

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE housekeeping_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行SQL脚本：
```bash
mysql -u root -p housekeeping_db < housekeeping_db.sql
```

### 3. 配置文件

修改 `src/main/resources/application.properties` 中的数据库连接信息：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/housekeeping_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. 运行项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

项目启动后，访问地址：http://localhost:8080

## API接口

### 认证相关
- `POST /api/auth/user/login` - 用户登录
- `POST /api/auth/provider/login` - 服务者登录
- `POST /api/auth/admin/login` - 管理员登录
- `POST /api/auth/user/register` - 用户注册
- `POST /api/auth/provider/register` - 服务者注册
- `POST /api/auth/change-password` - 修改密码

### 文件上传
- `POST /api/upload/image` - 上传图片
- `POST /api/upload/document` - 上传文档
- `POST /api/upload/avatar` - 上传头像
- `DELETE /api/upload/{filePath}` - 删除文件

### 用户管理
- `GET /api/user/profile` - 获取用户信息
- `PUT /api/user/profile` - 更新用户信息
- `GET /api/user/list` - 获取用户列表（管理员）
- `PUT /api/user/{id}/status` - 更新用户状态（管理员）

### 服务者管理
- `GET /api/provider/profile` - 获取服务者信息
- `PUT /api/provider/profile` - 更新服务者信息
- `GET /api/provider/list` - 获取服务者列表（管理员）
- `PUT /api/provider/{id}/certification` - 更新认证状态（管理员）

### 服务分类
- `GET /api/category/public/list` - 获取公开分类列表
- `GET /api/category/list` - 获取所有分类（管理员）
- `POST /api/category` - 创建分类（管理员）
- `PUT /api/category/{id}` - 更新分类（管理员）

### 家政服务
- `GET /api/service/public/{id}` - 获取服务详情
- `GET /api/service/public/category/{categoryId}` - 根据分类获取服务
- `GET /api/service/public/search` - 搜索服务
- `GET /api/service/public/popular` - 获取热门服务
- `POST /api/service` - 创建服务（服务者）
- `PUT /api/service/{id}` - 更新服务（服务者）

## 默认管理员账号

- 用户名：admin
- 密码：123456

## 注意事项

1. 请确保MySQL服务已启动
2. 首次运行会自动创建数据表
3. 文件上传目录为项目根目录下的 `uploads` 文件夹
4. JWT token有效期为24小时
5. 所有接口都需要在请求头中携带JWT token（除了公开接口）

## 开发说明

### 添加新的API接口

1. 在对应的Controller中添加方法
2. 在Service层实现业务逻辑
3. 在Repository层添加数据访问方法
4. 更新API文档

### 数据库迁移

项目使用JPA自动建表，如需手动管理数据库，请：

1. 修改 `spring.jpa.hibernate.ddl-auto=validate`
2. 手动执行SQL脚本创建表结构

## 许可证

MIT License
