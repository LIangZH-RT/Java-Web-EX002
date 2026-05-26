# 开发日志

## 项目概述

- **项目名称**：EX002 — 基于 MVC 设计模式的在线购物系统
- **技术栈**：Java Web (Servlet + MyBatis `POOLED`) + Vue 3 (Element Plus + axios)
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

- [x] 商品展示后端链路：实体类 / Mapper / Service / Servlet / 分页响应
- [x] 商品数据表 `products` 查询接入
- [x] 商品查询数据库连接池配置：MyBatis 内置 `POOLED`，暂不使用 `DBUtils`
- [x] `web.xml` 配置 `ProductServlet` 商品接口映射
- [x] `pom.xml` 添加商品接口所需的 Servlet API、MySQL 驱动、MyBatis、Jackson 依赖
- [x] 商品列表前后端联调
- [ ] 用户、购物车等后续后端接口及数据表
- [ ] 通用 Filter / Listener
- [ ] 实验报告

---

## 2026-05-25 — MyBatis 商品展示前后端联调

### 1. 实际商品表适配

已基于本地 `local_shop` 数据库中的真实商品表完成映射：

| 数据库列 | Java / JSON 字段 | 处理方式 |
|------|------|------|
| `ID` | `id` | SQL 别名映射 |
| `name` | `name` | 直接映射 |
| `price` | `price` | 直接映射 |
| `descriptoion` | `description` | 保留数据库现有列名，通过 SQL 别名对外提供规范字段 |
| `imgURL` | `imageUrl` | SQL 别名映射；当前库中的值均为占位值 `1` |

商品表目前不包含库存字段，因此本次商品展示不输出模拟库存，也不将“加入购物车”作为已完成能力。`imgURL` 当前尚无可加载图片地址，前端仅对合法图片 URL 渲染图片，其余显示“暂无图片”，避免破图。

### 2. 后端实现

- 修正 `Product` 实体与数据库展示字段的对应关系，使用 `imageUrl` 与 `description` 输出前端需要的数据。
- 使用 `ProductMapper` 与 `mapper/ProductMapper.xml` 完成商品分页查询、商品总数查询和按 ID 查询。
- 新增 `MyBatisUtil`，读取 `mybatis-config.xml` 创建单例 `SqlSessionFactory`；商品查询通过 MyBatis 内置 `POOLED` 数据源访问数据库，未调用 `DBUtils`。
- 新增 `ProductService`、`ProductServiceImpl`，封装分页和详情查询。
- 新增 `Result`、`PageResult` DTO，统一输出 `{ success, message, data }` 响应结构。
- 新增 `ProductServlet` 并在 `web.xml` 注册接口：

| 方法 | URL | 参数 | 返回内容 |
|------|-----|------|------|
| GET | `/api/products` | `page`, `pageSize` | 分页商品列表 |
| GET | `/api/products/detail` | `productId` | 单件商品详情 |

### 3. 前端实现

- `productApi.ts` 类型与后端商品 JSON 字段对齐，图片和描述支持数据库空值。
- `ProductListView.vue` 仅渲染接口返回的数据库商品，删除接口失败后回退到内置模拟商品的逻辑。
- 商品卡片展示数据库中的名称、图片、描述和价格；图片为空时显示本地空状态。
- 接口失败时显示加载失败提示，分页区间按当前页正确计算。
- `main.ts` 改为使用 Element Plus 提供的中文 locale，修复类型检查错误。

### 4. 验证结果

- `mvn test -q`：通过。
- `mvn package -q`：通过，已生成可部署的 `target/EX002.war`。
- `npm run build`：类型检查和 Vite 生产构建通过；存在已有的大 chunk 体积警告，不阻断商品展示。
- 通过 `ProductServiceImpl.findPage(1, 12)` 对数据库执行只读查询：`products` 表共读取到 `100` 条商品，第一页返回 `12` 条，确认 MyBatis 连接池、Mapper 与商品数据查询链路可用。
- 已部署到本机 Tomcat 10.1：`GET http://localhost:8080/EX002/api/products?page=1&pageSize=2` 返回真实商品 JSON。
- 已启动 Vite 开发服务：`http://127.0.0.1:5173/products` 可访问，`/api/products` 代理到 Tomcat 后端并返回真实商品数据。
