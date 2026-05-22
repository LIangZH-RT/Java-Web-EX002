# 开发日志

## 项目概述

- **项目名称**：EX002 — 基于 MVC 设计模式的在线购物系统
- **技术栈**：Java Web (Servlet + JDBC + Druid) + Vue 3 (Element Plus + axios)
- **构建工具**：Maven (WAR) + Vite
- **数据库**：MySQL 8.0+
- **运行环境**：JDK 17+ / Tomcat 10+ / Node.js 20+

---

## 2026-05-21 — 项目初始化与前端搭建

### 1. Maven Web 项目骨架搭建

- 创建 Maven WAR 项目，`pom.xml` 配置完成（坐标 `com.liang:EX002:1.0`，打包方式 `war`）
- 配置 `.gitignore`：排除 target/、.idea/、node_modules/、编译产物、OS 文件等
- 创建 `src/main/webapp/WEB-INF/web.xml`（Servlet 2.3 标准，待后续配置监听器和过滤器）
- 创建 `src/main/webapp/index.jsp`（占位首页，待后续替换）

### 2. 项目文档编写

完成 5 份设计文档，存放于 `docs/` 目录：

| 文档 | 内容 |
|------|------|
| `实验二项目说明.md` | 总体说明：实验目标、功能要求、技术列表、推荐包结构、数据库表、开发顺序 |
| `M-Model层说明.md` | Model 层设计：实体类 (Product/User/CartItem)、DTO (Result/PageResult)、DAO 接口、建表 SQL |
| `V-View层说明.md` | View 层设计：JSP 方案 vs Vue 方案、页面与接口对应、核心交互流程 |
| `C-Controller层说明.md` | Controller 层设计：Servlet 接口设计、Filter/Listener 规范、统一 JSON 响应格式 |
| `Vue和Servlet项目结构.md` | 前后端分离方案：目录结构、Vite 代理配置、开发与部署方式 |

### 3. Java 包结构规划

已规划 `src/main/java/com/liang/` 下的分层包结构（目录已建，Java 类待实现）：

```
com.liang
├── controller/     # Servlet: ProductServlet, CartServlet
├── service/        # 业务接口 + impl
├── dao/            # 数据访问接口 + impl
├── model/          # 实体类: Product, User, CartItem
├── dto/            # 传输对象: Result, PageResult
├── filter/         # 过滤器: EncodingFilter, CorsFilter
├── listener/       # 监听器: DataSourceListener
└── util/           # 工具类: DBUtil, JsonUtil
```

### 4. Vue 3 前端项目搭建

**环境**：`frontend/` 目录，Vite 8 + Vue 3.5 + TypeScript

**依赖安装**：
- `vue-router@4` — 前端路由
- `element-plus` — UI 组件库
- `@element-plus/icons-vue` — 图标库
- `axios` — HTTP 请求

**已完成的文件清单**：

| 文件 | 说明 |
|------|------|
| `src/main.ts` | 入口：注册 Element Plus（中文 locale）、Vue Router、全局图标、挂载 App |
| `src/App.vue` | 根组件：顶部导航栏（商品浏览 / 购物车切换）、`<router-view>` 出口、底部版权 |
| `src/router/index.ts` | 路由配置：`/` → `/products`，`/products` → ProductListView，`/cart` → CartView |
| `src/api/request.ts` | axios 实例：`baseURL=/api`，超时 10s，响应拦截器统一错误提示 |
| `src/api/productApi.ts` | 商品接口：`getProducts(page, pageSize)`、`getProductDetail(productId)`，含 TypeScript 类型定义 |
| `src/api/cartApi.ts` | 购物车接口：`getCartList`、`addToCart`、`updateCartItem`、`deleteCartItem`，含类型定义 |
| `src/views/ProductListView.vue` | 商品列表页：响应式卡片网格（4 列）、库存状态标签、加入购物车、分页组件、售罄遮罩 |
| `src/views/CartView.vue` | 购物车页：Element Plus 表格、数量输入框（自动校验库存上限）、删除确认弹窗、合计金额、分页 |
| `src/assets/css/main.css` | 全局样式：reset、字体、通用 class（`.page-header`、`.card-container`） |
| `vite.config.ts` | Vite 配置：`@` 路径别名 + `/api` 代理到 `http://localhost:8080/EX002` |
| `index.html` | 入口 HTML：`lang="zh-CN"`，title 改为"在线购物系统" |

**前端特性**：
- 后端不可用时自动 fallback 到内置模拟数据，可独立开发调试
- 分页支持：总数显示、上一页/下一页、页码跳转
- 购物车数量修改自动校验（≥1、≤库存）
- 删除操作有确认弹窗

### 5. 后端接口约定

前端通过 Vite 代理请求，开发时转发到 Tomcat 后端：

| 方法 | URL | 参数 | 对应 Servlet |
|------|-----|------|-------------|
| GET | `/api/products` | `page`, `pageSize` | ProductServlet |
| GET | `/api/products/detail` | `productId` | ProductServlet |
| GET | `/api/cart/list` | `page`, `pageSize` | CartServlet |
| POST | `/api/cart/add` | `productId`, `quantity` | CartServlet |
| POST | `/api/cart/update` | `cartItemId`, `quantity` | CartServlet |
| POST | `/api/cart/delete` | `cartItemId` | CartServlet |

统一响应格式：`{ success: boolean, message: string, data: ... }`

---

## 2026-05-21 (续) — 前端页面升级：Amazon 风格 + 登录注册

### 1. App.vue 导航栏全面改造

参照 Amazon 首页风格，将原有简洁导航栏替换为两层导航结构：

- **顶部暗色导航栏** (`#131921`)：
  - 左侧 Logo 区：`ShopStore` 品牌标识，可点击回首页
  - 中间搜索框：`el-input` + 黄色搜索按钮 (`#febd69`)，支持回车搜索
  - 右侧导航项：账户与列表入口 → `/login`、全部商品 → `/products`、购物车 → `/cart`
  - 悬停时白色边框高亮，Amazon 风格交互

- **次要导航栏** (`#232f3e`)：
  - 分类快捷入口：全部商品 / 数码电子 / 电脑办公 / 配件周边 / 生活好物

- **底部重新设计**：
  - "回到顶部" 链接栏 (`#37475a`)
  - 四列链接区：了解我们 / 合作联系 / 帮助中心 / 商务合作
  - 底部版权栏 (`#131a22`)

### 2. 商品列表页 — Amazon 购物风格改造

`ProductListView.vue` 全新设计：

| 特性 | 实现 |
|------|------|
| **布局** | CSS Grid 4 列响应式（3/2/1 列自动适配） |
| **卡片** | 白底 + 细灰边框，悬停阴影提升 |
| **图片** | 浅灰背景 (#f8f8f8)，`object-fit: contain`，售罄遮罩 |
| **星级评分** | ★ 黄色星级 + 评价数量链接色 (#0066c0) |
| **Best Seller** | 橙色标签 `#c45500`，部分商品显示 |
| **价格** | 红色系 `#b12704`，大字号整数 + 小字号角分，Amazon 经典样式 |
| **配送** | 满 ¥299 显示"免费配送"，否则显示运费 |
| **库存预警** | ≤10 件红字提醒"仅剩 X 件" |
| **按钮** | 黄色圆角胶囊按钮 `#ffd814`，Amazon "加入购物车" 风格 |
| **分页** | 居中布局，当前页高亮黄色，最多显示 7 个页码 |
| **模拟数据** | 16 件商品，覆盖 4 页（每页 12 条），方便演示分页效果 |

### 3. 登录 / 注册页面

新增 `LoginView.vue`：

- **Logo 区**：品牌标识 + 图标，点击回首页
- **Tab 切换**：登录 / 注册两个标签页，`el-tabs` 拉伸模式
- **登录表单**：邮箱 + 密码，黄色提交按钮，底部忘记密码/帮助链接
- **注册表单**：姓名 + 邮箱 + 密码 + 确认密码，提交后自动切回登录页
- **底部引导**：分割线 + "创建账户"按钮，新用户入口
- **交互反馈**：表单校验（空值、密码一致性）、loading 状态、ElMessage 提示
- **路由**：`/login`，导航栏"账户与列表"入口

### 4. 前端文件总览

```
frontend/src/
├── main.ts                      # 入口
├── App.vue                      # 根组件（Amazon 双层导航 + 深色底部）
├── router/index.ts              # 路由：/, /products, /cart, /login
├── api/
│   ├── request.ts               # axios 配置
│   ├── productApi.ts            # 商品接口
│   └── cartApi.ts               # 购物车接口
├── views/
│   ├── ProductListView.vue      # Amazon 风格商品列表页
│   ├── CartView.vue             # 购物车管理页
│   └── LoginView.vue            # 登录/注册页（新增）
└── assets/css/main.css          # 全局样式
```

---

## 2026-05-22 — Layui 集成

### 1. Layui CSS 引入

- 从 unpkg 下载 Layui v2.13.6 完整 CSS（131KB），写入 `src/assets/layui/css/layui.css`
- 字体文件已就绪：`iconfont.eot/woff/woff2/ttf/svg`（位于 `src/assets/layui/font/`）
- 在 `main.ts` 中导入 `layui.css`，位于 Element Plus CSS 之后、`main.css` 之前
- 导入顺序确保：Element Plus 基础样式 < Layui 工具类 < 自定义全局样式 < 组件 scoped 样式

### 2. App.vue — 导航栏与底部图标增强

| 位置 | 使用的 Layui 类 |
|------|----------------|
| Logo 旁 | `layui-icon layui-icon-cart-simple layui-font-22` |
| 购物车导航 | `layui-icon layui-icon-cart` |
| 回到顶部 | `layui-icon layui-icon-up` |

### 3. ProductListView.vue — 商品列表页增强

| 位置 | 使用的 Layui 类 |
|------|----------------|
| 分类标题 | `layui-icon layui-icon-list` |
| 分类计数 | `layui-font-13` |
| Best Seller 徽章 | `layui-icon layui-icon-praise` |
| 库存预警文字 | `layui-font-red` |
| 加入购物车按钮 | `layui-icon layui-icon-cart-simple` |

### 4. LoginView.vue — 登录注册页增强

| 位置 | 使用的 Layui 类 |
|------|----------------|
| Logo | `layui-icon layui-icon-cart-simple layui-font-30` |
| 表单卡片 | `layui-panel` |
| 邮箱输入前缀 | `layui-icon layui-icon-email` |
| 密码输入前缀 | `layui-icon layui-icon-password` |
| 姓名输入前缀 | `layui-icon layui-icon-username` |
| 登录按钮 | `layui-icon layui-icon-ok` |
| 注册按钮 | `layui-icon layui-icon-ok-circle` |

### 5. CartView.vue — 购物车页增强

| 位置 | 使用的 Layui 类 |
|------|----------------|
| 页面标题 | `layui-icon layui-icon-cart` |
| 卡片容器 | `layui-panel` |
| 继续购物按钮 | `layui-icon layui-icon-cart-simple` |
| 结算按钮 | `layui-icon layui-icon-rmb` |
| 移除按钮 | `layui-icon layui-icon-delete` |
| 空购物车按钮 | `layui-icon layui-icon-cart` |
| 合计标签 | `layui-font-16` |

### 6. 设计原则

- 所有组件 scoped 样式保持不变，确保现有页面视觉效果不受影响
- 仅以**增量方式**添加 Layui 图标类和工具类，不修改任何现有样式规则
- Layui 图标字体通过 `@font-face` 引用本地字体文件，构建时由 Vite 自动打包至 `dist/assets/`
- TypeScript 类型检查通过，Vite 构建成功

---

## 待完成

- [ ] 后端 Java 代码：实体类 / DAO / Service / Servlet / Filter / Listener
- [ ] 数据库建表：`t_user`、`t_product`、`t_cart_item`
- [ ] 数据库连接池配置（Druid `druid.properties`）
- [ ] `web.xml` 配置 Servlet 映射、Filter、Listener
- [ ] `pom.xml` 添加依赖：Servlet API、MySQL 驱动、Druid、Jackson 等
- [ ] 前后端联调
- [ ] 实验报告
