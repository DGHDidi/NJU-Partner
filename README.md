# 南大轻搭子 NJU Partner

## 1. 项目简介

**南大轻搭子** 是一个面向南京大学学生的校园轻量组队 Web 平台。

本项目聚焦大学生日常生活中高频、低门槛、临时性的组队需求，例如：

* 自习搭子
* 运动搭子
* 竞赛组队
* 课程学习搭子
* 拼单
* 饭搭子
* 短途出行
* 讲座同行
* 社团活动同行
* 跨校区同行

本项目不做泛社交平台，不做复杂好友系统，不做大型校园活动平台，而是围绕“发帖—筛选—报名—通过—成团关闭”的核心闭环，解决学生临时找不到合适同伴的问题。

---

## 2. 项目定位

### 2.1 项目名称

中文名：南大轻搭子
英文名：NJU Partner
项目类型：南京大学校园轻量组队 Web 平台

### 2.2 服务对象

本项目只面向南京大学学生，不考虑其他学校。

项目中的校区固定为：

* 仙林校区
* 鼓楼校区
* 浦口校区
* 苏州校区

### 2.3 核心问题

大学生经常有临时组队需求，例如：

* 想去图书馆自习，但想找一个同伴互相监督
* 想打羽毛球、篮球、跑步，但临时缺人
* 想参加比赛，但找不到队友
* 想去听讲座，但不想一个人去
* 想拼单、拼车、短途出行，但微信群信息太杂
* 想找同专业、同年级、同校区的人一起完成任务

传统方式主要依赖微信群、QQ群、朋友圈、表白墙等，但存在以下问题：

* 信息杂乱，容易被刷屏
* 过期信息多
* 难以按校区、年级、专业、兴趣筛选
* 组队成功后信息仍然留在群里
* 联系和报名流程不清楚

本项目通过结构化组队帖和标签筛选，降低信息噪音，提高匹配效率。

---

## 3. 核心业务流程

本项目的核心闭环如下：

```text
用户注册 / 登录
        ↓
完善个人信息：校区、年级、专业
        ↓
浏览首页组队帖
        ↓
按校区、类型、时间、年级、关键词筛选
        ↓
进入帖子详情
        ↓
报名 / 留言
        ↓
发布者查看报名列表
        ↓
发布者通过或拒绝报名
        ↓
通过人数达到需求人数
        ↓
帖子自动变为“已成团”
        ↓
进入我的发布 / 我的报名记录
```

第一阶段必须保证这个流程可以完整跑通。

---

## 4. 技术栈

### 4.1 前端

* Vue 3
* Vue Router
* Pinia
* Axios
* Element Plus
* Vite

### 4.2 后端

* Spring Boot
* MyBatis Plus
* MySQL
* JWT
* Lombok
* Validation

### 4.3 数据库

* MySQL 8.x

### 4.4 开发工具

* Cursor：辅助生成和修改代码
* IntelliJ IDEA：运行和调试 Spring Boot 后端
* Apifox / Postman：测试后端接口
* Navicat / DBeaver：管理数据库
* Git：版本管理

---

## 5. 项目架构

项目采用前后端分离架构。

```text
用户浏览器
   ↓
Vue 3 前端
   ↓ Axios 请求
Spring Boot 后端
   ↓ MyBatis Plus
MySQL 数据库
```

推荐项目目录：

```text
nju-partner
├── backend
│   └── nju-partner-backend
│
├── frontend
│   └── nju-partner-frontend
│
├── sql
│   └── init.sql
│
├── README.md
└── .cursor
    └── rules
        └── project.md
```

---

## 6. 功能模块

### 6.1 用户模块

用户模块负责注册、登录、个人资料管理。

#### 功能需求

* 用户注册
* 用户登录
* JWT 身份认证
* 获取当前用户信息
* 修改个人资料
* 区分普通用户和管理员

#### 用户注册字段

```text
username：用户名
password：密码
nickname：昵称
campus：校区
grade：年级
major：专业
```

#### 校区枚举

```text
仙林校区
鼓楼校区
浦口校区
苏州校区
```

#### 年级枚举

```text
大一
大二
大三
大四
研究生
博士生
其他
```

#### 用户角色

```text
USER：普通用户
ADMIN：管理员
```

---

### 6.2 组队帖模块

组队帖是本项目最核心的数据。

#### 功能需求

* 发布组队帖
* 查看组队帖列表
* 查看组队帖详情
* 按条件筛选组队帖
* 修改自己发布的组队帖
* 关闭自己发布的组队帖
* 删除违规组队帖
* 人数满后自动成团

#### 帖子类型

```text
自习搭子
运动搭子
竞赛组队
课程学习
拼单
饭搭子
短途出行
讲座同行
社团活动
跨校区同行
其他
```

#### 帖子状态

```text
0：招募中
1：已成团
2：已关闭
3：已过期
```

#### 首页筛选条件

```text
校区 campus
活动类型 type
关键词 keyword
年级 grade
状态 status
时间范围 startTime / endTime
```

---

### 6.3 报名模块

报名模块负责用户申请加入组队帖。

#### 功能需求

* 用户报名参加组队帖
* 用户取消报名
* 发布者查看报名列表
* 发布者通过报名
* 发布者拒绝报名
* 通过报名后帖子当前人数加 1
* 当前人数达到需求人数后，帖子自动变为“已成团”

#### 报名状态

```text
0：待审核
1：已通过
2：已拒绝
3：已取消
```

#### 业务规则

1. 用户不能报名自己发布的帖子。
2. 同一个用户不能重复报名同一个帖子。
3. 只有帖子发布者可以通过或拒绝报名。
4. 只有招募中的帖子可以报名。
5. 帖子已成团、已关闭、已过期时不能报名。
6. 报名通过后，帖子 current_count 加 1。
7. 如果 current_count >= need_count，帖子 status 自动改为 1，即已成团。

---

### 6.4 评论模块

评论模块用于帖子下方的轻量沟通。

#### 功能需求

* 用户在帖子下留言
* 查看帖子评论
* 用户删除自己的评论
* 管理员删除违规评论

评论不是私聊系统，不做实时聊天。

---

### 6.5 收藏模块

收藏模块用于保存感兴趣的帖子。

#### 功能需求

* 收藏帖子
* 取消收藏
* 查看我的收藏

---

### 6.6 个人中心模块

个人中心展示用户自己的相关记录。

#### 功能需求

* 查看我的资料
* 查看我的发布
* 查看我的报名
* 查看我的收藏
* 修改个人资料
* 退出登录

---

### 6.7 后台管理模块

后台管理用于课程项目展示，可以做简化版本。

#### 功能需求

* 管理员查看所有帖子
* 管理员删除违规帖子
* 管理员查看用户列表
* 管理员封禁用户
* 管理员删除违规评论

---

## 7. 页面设计

### 7.1 登录页 `/login`

功能：

* 用户名输入框
* 密码输入框
* 登录按钮
* 跳转注册页

登录成功后：

```text
保存 token 到 localStorage
跳转到首页 /home
```

---

### 7.2 注册页 `/register`

功能：

* 用户名
* 密码
* 确认密码
* 昵称
* 校区选择
* 年级选择
* 专业输入
* 注册按钮

注册成功后跳转登录页。

---

### 7.3 首页 `/home`

首页展示组队帖信息流。

页面结构：

```text
顶部导航栏
搜索框
筛选栏
帖子卡片列表
发布按钮
分页组件
```

帖子卡片展示：

```text
标题
活动类型
校区
地点
活动时间
人数进度
发布者昵称
状态
简短描述
```

示例：

```text
【自习搭子】今晚杜厦图书馆三楼刷高数
类型：自习搭子
校区：仙林校区
时间：今晚 19:00
地点：杜厦图书馆三楼
人数：1 / 3
状态：招募中
```

---

### 7.4 发布页 `/post/create`

发布组队帖表单字段：

```text
标题
活动类型
活动校区
具体地点
活动时间
需要人数
年级限制
专业限制
活动描述
联系方式
```

表单校验：

* 标题不能为空
* 类型不能为空
* 校区不能为空
* 活动时间不能为空
* 需要人数必须大于 0
* 描述不能为空

---

### 7.5 帖子详情页 `/post/:id`

展示内容：

```text
帖子标题
发布者信息
活动类型
活动校区
具体地点
活动时间
人数需求
当前人数
年级限制
专业限制
联系方式
活动描述
报名按钮
评论列表
报名列表
关闭招募按钮
```

权限逻辑：

* 普通用户可以报名、评论、收藏。
* 帖子发布者可以查看报名列表、通过报名、拒绝报名、关闭帖子。
* 管理员可以删除帖子和评论。

---

### 7.6 我的页面 `/profile`

展示：

```text
我的资料
我的发布
我的报名
我的收藏
退出登录
```

---

### 7.7 后台管理页 `/admin`

展示：

```text
帖子管理表格
用户管理表格
评论管理表格
```

后台页面可以用 Element Plus 的 `el-table` 实现。

---

## 8. 数据库设计

### 8.1 user 用户表

```sql
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(255) COMMENT '头像',
    campus VARCHAR(50) COMMENT '校区',
    grade VARCHAR(50) COMMENT '年级',
    major VARCHAR(100) COMMENT '专业',
    role VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    status INT DEFAULT 1 COMMENT '状态：1正常，0封禁',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

---

### 8.2 post 组队帖表

```sql
CREATE TABLE post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    type VARCHAR(50) NOT NULL COMMENT '活动类型',
    description TEXT COMMENT '活动描述',
    location VARCHAR(100) COMMENT '具体地点',
    activity_time DATETIME COMMENT '活动时间',
    need_count INT NOT NULL COMMENT '需要人数',
    current_count INT DEFAULT 0 COMMENT '当前已通过人数',
    campus VARCHAR(50) COMMENT '活动校区',
    grade_limit VARCHAR(50) COMMENT '年级限制',
    major_limit VARCHAR(100) COMMENT '专业限制',
    contact VARCHAR(100) COMMENT '联系方式',
    status INT DEFAULT 0 COMMENT '状态：0招募中，1已成团，2已关闭，3已过期',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);
```

---

### 8.3 application 报名表

```sql
CREATE TABLE application (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '报名用户ID',
    message VARCHAR(255) COMMENT '报名留言',
    status INT DEFAULT 0 COMMENT '状态：0待审核，1已通过，2已拒绝，3已取消',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_post_user (post_id, user_id)
);
```

---

### 8.4 comment 评论表

```sql
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论用户ID',
    content VARCHAR(500) NOT NULL COMMENT '评论内容',
    status INT DEFAULT 1 COMMENT '状态：1正常，0删除',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);
```

---

### 8.5 favorite 收藏表

```sql
CREATE TABLE favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_post (user_id, post_id)
);
```

---

### 8.6 category 分类表

```sql
CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序',
    status INT DEFAULT 1 COMMENT '状态：1启用，0禁用'
);
```

---

### 8.7 初始分类数据

```sql
INSERT INTO category (name, sort, status) VALUES
('自习搭子', 1, 1),
('运动搭子', 2, 1),
('竞赛组队', 3, 1),
('课程学习', 4, 1),
('拼单', 5, 1),
('饭搭子', 6, 1),
('短途出行', 7, 1),
('讲座同行', 8, 1),
('社团活动', 9, 1),
('跨校区同行', 10, 1),
('其他', 99, 1);
```

---

## 9. 后端接口设计

所有接口统一以 `/api` 开头。

统一返回格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

---

### 9.1 用户接口

#### 注册

```text
POST /api/user/register
```

请求参数：

```json
{
  "username": "test",
  "password": "123456",
  "nickname": "小南",
  "campus": "仙林校区",
  "grade": "大一",
  "major": "计算机科学与技术"
}
```

#### 登录

```text
POST /api/user/login
```

请求参数：

```json
{
  "username": "test",
  "password": "123456"
}
```

返回：

```json
{
  "token": "jwt-token",
  "userInfo": {
    "id": 1,
    "username": "test",
    "nickname": "小南",
    "role": "USER"
  }
}
```

#### 获取个人信息

```text
GET /api/user/profile
```

请求头：

```text
Authorization: Bearer token
```

#### 修改个人信息

```text
PUT /api/user/profile
```

---

### 9.2 帖子接口

#### 发布帖子

```text
POST /api/posts
```

请求参数：

```json
{
  "title": "今晚杜厦图书馆找自习搭子",
  "type": "自习搭子",
  "description": "主要复习高数，安静学习，不闲聊。",
  "location": "杜厦图书馆三楼",
  "activityTime": "2026-06-01 19:00:00",
  "needCount": 2,
  "campus": "仙林校区",
  "gradeLimit": "不限",
  "majorLimit": "不限",
  "contact": "微信：xxxx"
}
```

#### 查询帖子列表

```text
GET /api/posts
```

支持查询参数：

```text
campus
type
keyword
status
grade
pageNum
pageSize
```

示例：

```text
GET /api/posts?campus=仙林校区&type=自习搭子&status=0&pageNum=1&pageSize=10
```

#### 查询帖子详情

```text
GET /api/posts/{id}
```

#### 修改帖子

```text
PUT /api/posts/{id}
```

#### 关闭帖子

```text
PUT /api/posts/{id}/close
```

#### 删除帖子

```text
DELETE /api/posts/{id}
```

---

### 9.3 报名接口

#### 报名参加

```text
POST /api/posts/{id}/apply
```

请求参数：

```json
{
  "message": "我也想一起去，可以准时到。"
}
```

#### 查看某帖子报名列表

```text
GET /api/posts/{id}/applications
```

#### 通过报名

```text
PUT /api/applications/{id}/pass
```

#### 拒绝报名

```text
PUT /api/applications/{id}/reject
```

#### 取消报名

```text
PUT /api/applications/{id}/cancel
```

---

### 9.4 评论接口

#### 发表评论

```text
POST /api/posts/{id}/comments
```

请求参数：

```json
{
  "content": "请问活动大概持续多久？"
}
```

#### 查看评论

```text
GET /api/posts/{id}/comments
```

#### 删除评论

```text
DELETE /api/comments/{id}
```

---

### 9.5 收藏接口

#### 收藏帖子

```text
POST /api/posts/{id}/favorite
```

#### 取消收藏

```text
DELETE /api/posts/{id}/favorite
```

#### 查看我的收藏

```text
GET /api/user/favorites
```

---

### 9.6 个人中心接口

#### 我的发布

```text
GET /api/user/my-posts
```

#### 我的报名

```text
GET /api/user/my-applications
```

#### 我的收藏

```text
GET /api/user/my-favorites
```

---

### 9.7 后台接口

#### 查看所有帖子

```text
GET /api/admin/posts
```

#### 删除帖子

```text
DELETE /api/admin/posts/{id}
```

#### 查看所有用户

```text
GET /api/admin/users
```

#### 封禁用户

```text
PUT /api/admin/users/{id}/ban
```

#### 解封用户

```text
PUT /api/admin/users/{id}/unban
```

---

## 10. 后端代码结构建议

后端包名统一使用：

```text
com.nju.partner
```

推荐结构：

```text
com.nju.partner
├── NjuPartnerApplication.java
│
├── common
│   ├── Result.java
│   ├── ResultCode.java
│   └── BaseContext.java
│
├── config
│   ├── WebConfig.java
│   └── MyBatisPlusConfig.java
│
├── controller
│   ├── UserController.java
│   ├── PostController.java
│   ├── ApplicationController.java
│   ├── CommentController.java
│   ├── FavoriteController.java
│   └── AdminController.java
│
├── dto
│   ├── UserRegisterDTO.java
│   ├── UserLoginDTO.java
│   ├── PostCreateDTO.java
│   ├── PostQueryDTO.java
│   └── ApplicationCreateDTO.java
│
├── vo
│   ├── UserVO.java
│   ├── LoginVO.java
│   ├── PostVO.java
│   ├── PostDetailVO.java
│   └── ApplicationVO.java
│
├── entity
│   ├── User.java
│   ├── Post.java
│   ├── Application.java
│   ├── Comment.java
│   ├── Favorite.java
│   └── Category.java
│
├── mapper
│   ├── UserMapper.java
│   ├── PostMapper.java
│   ├── ApplicationMapper.java
│   ├── CommentMapper.java
│   ├── FavoriteMapper.java
│   └── CategoryMapper.java
│
├── service
│   ├── UserService.java
│   ├── PostService.java
│   ├── ApplicationService.java
│   ├── CommentService.java
│   └── FavoriteService.java
│
├── service.impl
│   ├── UserServiceImpl.java
│   ├── PostServiceImpl.java
│   ├── ApplicationServiceImpl.java
│   ├── CommentServiceImpl.java
│   └── FavoriteServiceImpl.java
│
├── interceptor
│   └── JwtInterceptor.java
│
├── utils
│   ├── JwtUtils.java
│   └── PasswordUtils.java
│
└── exception
    ├── BusinessException.java
    └── GlobalExceptionHandler.java
```

---

## 11. 前端代码结构建议

前端推荐结构：

```text
src
├── api
│   ├── user.js
│   ├── post.js
│   ├── application.js
│   ├── comment.js
│   ├── favorite.js
│   └── admin.js
│
├── assets
│
├── components
│   ├── PostCard.vue
│   ├── FilterBar.vue
│   └── Navbar.vue
│
├── router
│   └── index.js
│
├── stores
│   └── user.js
│
├── utils
│   └── request.js
│
├── views
│   ├── Login.vue
│   ├── Register.vue
│   ├── Home.vue
│   ├── PostCreate.vue
│   ├── PostDetail.vue
│   ├── Profile.vue
│   └── Admin.vue
│
├── App.vue
└── main.js
```

---

## 12. 前端页面风格要求

页面风格应简洁、校园化、清爽。

建议风格：

* 主色调：蓝色、紫色、白色
* 布局：顶部导航栏 + 内容卡片
* 组件：尽量使用 Element Plus
* 首页帖子用卡片展示
* 后台用表格展示
* 表单不要太复杂

首页建议展示文案：

```text
南大轻搭子
找自习搭子、运动搭子、竞赛队友、讲座同行、跨校区同行
```

---

## 13. 第一阶段开发顺序

请 Cursor 严格按以下顺序开发，不要一次性生成整个项目。

### 阶段 1：项目初始化

1. 创建后端 Spring Boot 项目
2. 创建前端 Vue 3 项目
3. 创建数据库 SQL
4. 配置 MySQL 连接
5. 跑通后端启动
6. 跑通前端启动

### 阶段 2：用户模块

1. user 表
2. User 实体类
3. UserMapper
4. UserService
5. UserController
6. 注册接口
7. 登录接口
8. JWT 工具类
9. 登录拦截器
10. 前端登录页
11. 前端注册页

### 阶段 3：帖子模块

1. post 表
2. Post 实体类
3. PostMapper
4. PostService
5. PostController
6. 发布帖子接口
7. 查询帖子列表接口
8. 查询帖子详情接口
9. 关闭帖子接口
10. 前端首页
11. 前端发布页
12. 前端详情页

### 阶段 4：报名模块

1. application 表
2. Application 实体类
3. ApplicationMapper
4. ApplicationService
5. ApplicationController
6. 报名接口
7. 查看报名列表接口
8. 通过报名接口
9. 拒绝报名接口
10. 人数满后自动成团逻辑
11. 前端报名按钮
12. 发布者报名管理区域

### 阶段 5：评论、收藏、个人中心

1. 评论接口
2. 收藏接口
3. 我的发布
4. 我的报名
5. 我的收藏
6. 个人中心页面

### 阶段 6：后台管理

1. 管理员查看帖子
2. 管理员删除帖子
3. 管理员查看用户
4. 管理员封禁用户
5. 后台管理页面

### 阶段 7：美化和测试

1. 页面美化
2. 增加测试数据
3. 统一接口错误提示
4. 完整测试核心流程
5. 修复 bug

---

## 14. Cursor 开发约束

Cursor 在生成或修改代码时必须遵守以下规则：

1. 不要一次性生成完整项目。
2. 每次只实现一个明确模块。
3. 修改代码前，先说明将新增或修改哪些文件。
4. 不要删除已有功能。
5. 不要随意修改数据库字段名。
6. 前后端字段命名必须保持一致。
7. 后端接口统一以 `/api` 开头。
8. 后端统一使用 `Result` 返回。
9. 后端包名统一为 `com.nju.partner`。
10. 前端统一使用 Element Plus 组件。
11. Axios 请求统一放在 `src/api` 目录，不要直接散落在 Vue 页面中。
12. 登录 token 存储在 `localStorage`。
13. 前端请求通过 `src/utils/request.js` 统一封装。
14. 没有明确要求时，不要引入复杂技术。
15. 不要做私聊系统。
16. 不要做微信小程序。
17. 不要做多学校扩展。
18. 不要做复杂 AI 推荐。
19. 不要做真实学号认证。
20. 代码应尽量简单，适合课程项目展示。

---

## 15. MVP 最小可用版本

第一版只需要完成以下功能：

```text
注册
登录
发布组队帖
首页查看组队帖
按校区和类型筛选
查看帖子详情
报名参加
发布者通过报名
人数满后自动成团
我的发布
我的报名
```

第一版暂时不做：

```text
私聊
实时消息
微信登录
学校认证
复杂推荐算法
活动爬虫
多学校支持
```

---

## 16. 核心演示流程

项目最终至少要能演示以下流程：

```text
1. 用户 A 注册并登录
2. 用户 A 发布一个组队帖：今晚仙林校区图书馆自习，缺 2 人
3. 用户 B 注册并登录
4. 用户 B 在首页筛选“仙林校区 + 自习搭子”
5. 用户 B 进入帖子详情页
6. 用户 B 点击报名
7. 用户 A 在自己的帖子详情页看到报名列表
8. 用户 A 通过用户 B 的报名
9. 帖子当前人数增加
10. 当当前人数达到需要人数时，帖子状态自动变为“已成团”
11. 用户 A 可以在“我的发布”看到该帖子
12. 用户 B 可以在“我的报名”看到该记录
```

---

## 17. 推荐测试数据

可以准备以下测试用户：

```text
用户1：
username: alice
password: 123456
nickname: 小南
campus: 仙林校区
grade: 大一
major: 计算机科学与技术

用户2：
username: bob
password: 123456
nickname: 鼓楼小王
campus: 鼓楼校区
grade: 大二
major: 电子信息

用户3：
username: admin
password: 123456
nickname: 管理员
campus: 仙林校区
grade: 其他
major: 管理员
role: ADMIN
```

推荐测试帖子：

```text
1. 今晚杜厦图书馆找自习搭子
类型：自习搭子
校区：仙林校区
地点：杜厦图书馆三楼
人数：2

2. 周五晚上鼓楼校区找饭搭子
类型：饭搭子
校区：鼓楼校区
地点：南园附近
人数：3

3. 仙林体育馆羽毛球缺 2 人
类型：运动搭子
校区：仙林校区
地点：体育馆
人数：4

4. 数学建模竞赛组队
类型：竞赛组队
校区：仙林校区
地点：线上 + 线下
人数：3

5. 周末南京博物院短途出行
类型：短途出行
校区：鼓楼校区
地点：南京博物院
人数：4
```

---

## 18. 开发时的优先级

### P0：必须完成

* 注册登录
* JWT 鉴权
* 发布帖子
* 首页帖子列表
* 帖子详情
* 报名
* 通过报名
* 成团关闭
* 我的发布
* 我的报名

### P1：建议完成

* 评论
* 收藏
* 多条件筛选
* 后台管理
* 用户封禁
* 删除违规帖子

### P2：有时间再做

* 首页推荐排序
* 活动聚合
* 举报功能
* 帖子过期自动扫描
* 页面进一步美化

### P3：只写进未来展望

* 微信小程序端
* 学校统一身份认证
* 消息通知
* 私聊
* AI 匹配
* 多学校扩展

---

## 19. 推荐 Git 提交节奏

每完成一个小模块就提交一次。

```bash
git add .
git commit -m "初始化项目结构"

git add .
git commit -m "完成用户注册登录模块"

git add .
git commit -m "完成组队帖发布和查询"

git add .
git commit -m "完成报名和成团逻辑"

git add .
git commit -m "完成个人中心页面"

git add .
git commit -m "完成后台管理页面"
```

---

## 20. Cursor 第一条提示词

将本 README 放到项目根目录后，可以对 Cursor 发送：

```text
请先完整阅读 README.md，理解本项目需求。

这是一个课程项目，项目名叫“南大轻搭子”，技术栈为 Vue 3 + Element Plus + Spring Boot + MyBatis Plus + MySQL + JWT。

请不要一次性生成完整项目。请先根据 README.md 帮我规划第一阶段的具体开发步骤，并列出需要创建的目录和文件。

要求：
1. 只规划，不要直接生成大量代码。
2. 每一步说明对应的文件路径。
3. 优先完成 MVP 核心闭环。
4. 严格遵守 README.md 中的开发约束。
```

---

## 21. Cursor 后续模块提示词模板

### 用户模块提示词

```text
请阅读 README.md，现在开始实现用户注册登录模块。

要求：
1. 后端使用 Spring Boot + MyBatis Plus。
2. 包名使用 com.nju.partner。
3. 实现 User 实体、Mapper、Service、Controller。
4. 实现 POST /api/user/register。
5. 实现 POST /api/user/login。
6. 登录成功返回 JWT token。
7. 注册时检查 username 是否重复。
8. campus 只能是仙林校区、鼓楼校区、浦口校区、苏州校区。
9. 所有接口统一返回 Result。
10. 先列出你将新增或修改的文件，再生成代码。
```

### 帖子模块提示词

```text
请阅读 README.md，现在开始实现组队帖模块。

要求：
1. 实现发布组队帖。
2. 实现帖子列表查询。
3. 查询列表支持 campus、type、keyword、status 筛选。
4. 实现帖子详情查询。
5. 实现关闭帖子。
6. 只有登录用户可以发布帖子。
7. 只有帖子发布者可以关闭帖子。
8. 所有接口统一返回 Result。
9. 先列出你将新增或修改的文件，再生成代码。
```

### 报名模块提示词

```text
请阅读 README.md，现在开始实现报名模块。

要求：
1. 实现 POST /api/posts/{id}/apply 报名。
2. 实现 GET /api/posts/{id}/applications 查看报名列表。
3. 实现 PUT /api/applications/{id}/pass 通过报名。
4. 实现 PUT /api/applications/{id}/reject 拒绝报名。
5. 用户不能报名自己发布的帖子。
6. 用户不能重复报名同一个帖子。
7. 只有帖子发布者可以通过或拒绝报名。
8. 通过报名后，post.current_count + 1。
9. 如果 current_count >= need_count，则帖子 status 改为 1，表示已成团。
10. 所有接口统一返回 Result。
11. 先列出你将新增或修改的文件，再生成代码。
```

---

## 22. 项目最终目标

本项目最终目标不是做一个功能极其复杂的社交平台，而是完成一个清晰、可演示、可讲解的课程项目。

最终项目需要做到：

```text
功能闭环完整
数据库设计清楚
接口结构清楚
页面展示清楚
业务逻辑合理
代码结构规范
答辩讲得明白
```

最重要的不是功能数量，而是核心流程能稳定运行。

核心流程就是：

```text
注册登录 → 发帖 → 筛选 → 报名 → 通过 → 成团 → 记录归档
```
