# 南大轻搭子前端

Vue 3 + Vite + Element Plus + Pinia + Vue Router + Axios

## 目录结构

```text
src/
├── api/           # 接口封装（按模块划分）
├── assets/        # 静态资源与全局样式
├── components/    # 公共组件
├── constants/     # 枚举常量（校区、年级、帖子类型等）
├── router/        # 路由配置
├── stores/        # Pinia 状态
├── utils/         # 工具（Axios 封装）
└── views/         # 页面
```

## 本地启动

```bash
cd frontend/nju-partner-frontend
npm install
npm run dev
```

开发服务器默认 `http://localhost:5173`，API 代理至 `http://localhost:8080/api`。

## 页面路由

| 路径 | 页面 |
|------|------|
| `/login` | 登录 |
| `/register` | 注册 |
| `/home` | 首页 |
| `/post/create` | 发布组队帖 |
| `/post/:id` | 帖子详情 |
| `/profile` | 个人中心 |
| `/admin` | 后台管理 |

## 开发顺序（参考根目录 README）

1. 登录 / 注册
2. 首页帖子列表与筛选
3. 发布页与详情页
4. 报名模块
5. 评论、收藏、个人中心
6. 后台管理
