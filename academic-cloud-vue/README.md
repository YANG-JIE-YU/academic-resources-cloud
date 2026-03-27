# academic-cloud-vue

统一前端项目（Vue 3 + Vite + Naive UI），用于“基于微服务架构的学术资源门户系统”。

## 技术栈

- Vue 3
- Vite
- TypeScript
- Naive UI
- @vicons/ionicons5
- Pinia
- Vue Router
- Axios

## 启动方式

1. 进入目录：

```bash
cd D:\workspace\academic-resources-cloud\academic-cloud-vue
```

2. 安装依赖：

```bash
npm install
```

3. 复制环境变量：

```bash
copy .env.example .env
```

4. 启动开发环境：

```bash
npm run dev
```

默认访问地址：`http://localhost:5173`

## 网关代理配置

- 前端请求统一走 `/api`
- Vite 代理目标由 `VITE_API_TARGET` 决定，默认：`http://127.0.0.1:18080`
- 请确保后端 `gateway-service` 已启动

## 路由结构

- 门户端：`/`、`/papers`、`/notices`、`/activities`、`/forum`、`/ai/*`
- 认证端：`/auth/login`、`/auth/register`
- 管理端：`/admin/*`

## 权限说明

- 游客可访问：首页、公告、活动、论文、搜索、登录注册
- 登录用户可访问：个人中心、收藏、论坛发帖、AI 页面
- 管理员可访问：`/admin/*` 全部管理页面

## 已接入的后端接口

- 用户认证：`/api/user/auth/*`
- 用户中心：`/api/user/users/*`
- 收藏评论论坛：`/api/user/favorites`、`/api/user/comments`、`/api/user/forum/*`
- 论文分类：`/api/paper/papers/*`、`/api/paper/categories/*`
- AI 模块：`/api/ai/*`

## 说明

- 公告与活动管理页当前使用前端本地演示数据（后端接口预留）。
- 前端请求层已统一处理 Token 注入、错误提示、401 跳转登录。
