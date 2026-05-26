# EX002 在线购物系统

基于 MVC 设计模式的 Java Web 实验项目，实现商品浏览、用户登录、购物车管理和购物车数据持久化。项目采用 Servlet + MyBatis 提供后端接口，Vue 3 + Element Plus 构建前端页面，开发阶段通过 Vite 代理访问 Tomcat 后端。

## 功能概览

- 商品分页浏览：从 MySQL `products` 表读取商品名称、价格、描述和图片地址。
- 用户登录注册：支持登录、注册、退出和当前用户查询。
- 登录保护：购物车接口通过 `LoginFilter` 保护，未登录返回 `401` JSON。
- 购物车管理：支持加入购物车、分页查询、修改数量、删除商品。
- 结算选中状态：购物车支持单选、全选、半选；合计金额只统计已选商品。
- 数据持久化：购物车数据通过 MyBatis 保存到 `t_cart_item`，重启后保留。
- 前端界面：Vue 3 + Element Plus + Layui 图标，包含商品页、登录页、购物车页。

## 技术栈

| 层次 | 技术 |
|------|------|
| 后端 | Java 17+、Servlet Jakarta API、MyBatis、Jackson |
| 数据库 | MySQL 8.0+、MyBatis `POOLED` 连接池 |
| 前端 | Vue 3、TypeScript、Vue Router、Element Plus、axios、Vite |
| 构建部署 | Maven WAR、Tomcat 10+、Node.js 20+ |

## 项目结构

```text
EX002/
├── docs/                         # 实验说明和 MVC 分层文档
├── frontend/                     # Vue 3 前端项目
│   ├── src/api/                  # axios 请求封装
│   ├── src/views/                # 商品、登录、购物车页面
│   └── vite.config.ts            # Vite 代理配置
├── src/main/java/com/liang/
│   ├── controller/               # Servlet 控制器
│   ├── filter/                   # UTF-8 编码和登录过滤器
│   ├── mapper/                   # MyBatis Mapper 接口
│   ├── model/                    # 实体对象
│   ├── model/service/            # 业务接口与实现
│   └── util/                     # MyBatis 工具类
├── src/main/resources/
│   ├── mapper/                   # MyBatis XML
│   ├── db.property               # 本地数据库配置
│   └── mybatis-config.xml
├── src/main/webapp/WEB-INF/
│   └── web.xml                   # Filter 等 Web 配置
├── DEVLOG.md                     # 开发记录
└── pom.xml
```

## 数据库约定

后端默认连接 `src/main/resources/db.property` 中配置的 MySQL 数据库。请按本机环境修改 `driver`、`url`、`username`、`password`。

当前商品表按本地 `products` 表适配：

| 数据库列 | 后端字段 |
|----------|----------|
| `ID` | `id` |
| `name` | `name` |
| `price` | `price` |
| `description` | `description` |
| `imgurl` | `imageUrl` |

用户表支持自动识别 `user`、`users`、`t_user`，字段为 `id / username / password`。

购物车表不存在时会自动创建默认 `t_cart_item`：

```sql
CREATE TABLE IF NOT EXISTS t_cart_item (
  id BIGINT(20) NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  productid INT NOT NULL,
  quantity INT NOT NULL DEFAULT 1,
  selected TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否选中（1:选中, 0:未选中），用于结算',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY uk_cart_user_product (user_id, productid),
  KEY idx_cart_user_update (user_id, update_time),
  KEY idx_cart_user_selected (user_id, selected)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车明细表';
```

## 后端运行

在项目根目录执行：

```bash
mvn test
mvn package
```

构建成功后会生成：

```text
target/EX002.war
```

将 `EX002.war` 部署到 Tomcat 10+ 的 `webapps` 目录，后端访问上下文为：

```text
http://localhost:8080/EX002
```

## 前端运行

在 `frontend/` 目录执行：

```bash
npm install
npm run dev -- --host 127.0.0.1
```

默认前端地址：

```text
http://127.0.0.1:5173
```

Vite 会把 `/api` 请求代理到：

```text
http://localhost:8080/EX002
```

## 接口清单

统一响应格式：

```json
{
  "success": true,
  "message": "success",
  "data": {}
}
```

### 商品接口

| 方法 | URL | 参数 | 说明 |
|------|-----|------|------|
| GET | `/api/products` | `page`, `pageSize` | 商品分页列表 |
| GET | `/api/products/detail` | `productId` | 商品详情 |

### 用户接口

| 方法 | URL | 参数 | 说明 |
|------|-----|------|------|
| POST | `/api/auth/login` | `username`, `password` | 登录 |
| POST | `/api/auth/register` | `username`, `password` | 注册 |
| POST | `/api/auth/logout` | - | 退出登录 |
| GET | `/api/auth/current` | - | 当前登录用户 |

### 购物车接口

| 方法 | URL | 参数 | 说明 |
|------|-----|------|------|
| GET | `/api/cart/list` | `page`, `pageSize` | 当前用户购物车分页 |
| POST | `/api/cart/add` | `productId`, `quantity` | 加入购物车 |
| POST | `/api/cart/update` | `cartItemId`, `quantity` | 修改数量 |
| POST | `/api/cart/select` | `cartItemId`, `selected` | 修改结算选中状态 |
| POST | `/api/cart/delete` | `cartItemId` | 删除购物车商品 |

## 验证命令

```bash
mvn test
mvn package
cd frontend
npm run build
```

已知构建状态：后端测试和打包通过，前端生产构建通过；Vite 会提示大 chunk 体积警告，不影响功能运行。

## 文档

- `docs/实验二项目说明.md`：实验目标、功能要求和推荐开发顺序。
- `docs/M-Model层说明.md`：实体、DTO、数据库表设计。
- `docs/C-Controller层说明.md`：Servlet、Filter 和接口设计。
- `docs/V-View层说明.md`：页面和前端交互流程。
- `docs/MyBatis使用说明.md`：MyBatis 配置和使用示例。
- `DEVLOG.md`：项目开发过程和验证记录。
