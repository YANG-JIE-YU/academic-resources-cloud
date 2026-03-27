# 基于微服务架构的学术资源门户系统（用户-论文-AI推荐）

本项目在 `docs/springbootg75441il` 单体工程基础上做渐进式微服务改造，当前已完成骨架搭建与核心代码结构生成。

## 1. 模块结构

```text
academic-resources-cloud
├─ docs
│  └─ springbootg75441il           # 原单体，仅参考，不删除
├─ academic-cloud-common           # 通用模块（Result/异常/JWT/常量/跨服务DTO）
├─ academic-cloud-gateway          # 网关服务（路由、CORS、Token校验）
├─ academic-cloud-user-service     # 用户服务（登录注册、RBAC基础、收藏、评论、论坛、行为记录）
├─ academic-cloud-paper-service    # 内容服务（论文、分类、公告、活动管理与门户展示）
├─ academic-cloud-ai-service       # AI服务（对话、摘要、关键词、推荐、日志）
└─ pom.xml                         # Maven父工程
```

## 2. 端口与服务规划

- `academic-cloud-gateway`：`18080`
- `academic-cloud-user-service`：`18081`
- `academic-cloud-paper-service`：`18082`
- `academic-cloud-ai-service`：`18083`

网关统一入口示例：

- 用户接口：`/api/user/**`
- 论文接口：`/api/paper/**`
- AI接口：`/api/ai/**`

## 3. Nacos配置说明

每个服务已提供 `bootstrap.yml`，默认读取：

- `NACOS_SERVER_ADDR`（默认 `127.0.0.1:8848`）
- `NACOS_NAMESPACE`（默认空）
- `NACOS_GROUP`（默认 `DEFAULT_GROUP`）

建议在 Nacos 中维护以下 Data ID（可选）：

- `academic-cloud-gateway.yml`
- `academic-cloud-user-service.yml`
- `academic-cloud-paper-service.yml`
- `academic-cloud-ai-service.yml`

本地开发也可直接用各模块 `application.yml` 启动。

## 4. 数据库拆分建议

- `academic_cloud_user_db`：用户、角色、用户角色、收藏、评论、论坛、行为日志
- `academic_cloud_paper_db`：论文、论文分类、公告、活动
- `academic_cloud_ai_db`：AI对话日志、摘要/关键词日志、推荐日志

遵循原则：不跨服务直接访问对方数据库，服务间通过 HTTP/OpenFeign 调用。

## 5. 单体到微服务命名映射（建议）

- `yonghu / users` -> `uc_user`
- `storeup` -> `uc_favorite`
- `discuss*` -> `uc_comment`
- `forum` -> `uc_forum_post`
- `lunwen` -> `pc_paper`
- `xueshuzhutileixing` -> `pc_paper_category`

说明：保留原业务语义，同时把中文拼音表名升级为更规范的服务域命名，便于论文与答辩展示。

## 6. 启动顺序

1. 启动 MySQL、Redis、Nacos
2. 建库：`academic_cloud_user_db`、`academic_cloud_paper_db`、`academic_cloud_ai_db`
3. 执行初始化脚本：
   1. `docs/sql/academic_cloud_user_db_init.sql`
   2. `docs/sql/academic_cloud_paper_db_init.sql`
   3. `docs/sql/academic_cloud_ai_db_init.sql`
4. 启动服务（建议顺序）
   1. `academic-cloud-user-service`
   2. `academic-cloud-paper-service`
   3. `academic-cloud-ai-service`
   4. `academic-cloud-gateway`

## 7. 编译命令

在项目根目录执行：

```bash
mvn clean compile -DskipTests
```

如需打包：

```bash
mvn clean package -DskipTests
```

## 8. 迁移策略（渐进式）

1. 先迁移用户认证与基础用户数据，前端登录链路先跑通。
2. 迁移论文管理与检索接口，确保门户主流程可用。
3. 将原单体推荐逻辑（如按收藏推荐）迁入 AI 服务，已支持对接外部大模型（MiniMax）并保留失败回退。
4. 在网关统一鉴权，逐步下线单体会话式鉴权。
5. 前端接口按网关路径逐步切换，避免一次性重写。

## 9. Docker 一键部署（推荐）

项目根目录已提供：

- `docker-compose.yml`
- `docker/Dockerfile.backend`（4 个后端服务复用）
- `docker/Dockerfile.frontend`
- `docker/nginx/default.conf`
- `docker/mysql/init/00-init-databases.sh`

### 9.1 启动

1. 复制环境变量模板：

```bash
cp .env.docker.example .env
```

或在 PowerShell 中执行：

```powershell
Copy-Item .env.docker.example .env
```

2. 按需编辑 `.env`（至少修改 `JWT_SECRET`，如需调用外部 AI 再填写 `AI_API_KEY` 等）。

3. 启动全部容器：

```bash
docker compose up -d --build
```

### 9.2 访问入口

- 前端：`http://localhost:18000`
- 网关：`http://localhost:18080`
- Nacos：`http://localhost:8848/nacos`

### 9.3 说明

- MySQL 首次启动会自动建库并导入 `docs/sql/*.sql` 初始化数据。
- 若你修改了 SQL 初始化脚本，需要清理数据卷后重建：

```bash
docker compose down -v
docker compose up -d --build
```
