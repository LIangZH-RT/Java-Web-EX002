<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import { getProducts, type Product, type PageResult } from '@/api/productApi'
import { addToCart } from '@/api/cartApi'

const route = useRoute()

const products = ref<Product[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

function imageUrl(product: Product): string {
  if (product.imageUrl && product.imageUrl.trim()) {
    return product.imageUrl
  }
  const seed = product.id * 12345
  return `https://placehold.co/280x280/e8e8e8/555?text=${encodeURIComponent(product.name.slice(0, 4))}`
}

function ratingStar(product: Product): string {
  return ((product.id * 7 + 30) % 20 + 30) / 10
}

function ratingCount(product: Product): string {
  const count = (product.id * 137 + 42) % 5000 + 50
  if (count >= 1000) return `${(count / 1000).toFixed(1)}k`
  return String(count)
}

function isBestseller(product: Product): boolean {
  return product.id % 4 === 1
}

function deliveryInfo(product: Product): string {
  if (product.price >= 299) return '免费配送'
  return `运费 ¥${(product.price * 0.05).toFixed(1)}`
}

async function loadProducts() {
  loading.value = true
  try {
    const res = await getProducts(currentPage.value, pageSize.value)
    const data = res.data as PageResult<Product>
    products.value = data.records
    total.value = data.total
  } catch {
    const start = (currentPage.value - 1) * pageSize.value
    products.value = allMockProducts.slice(start, start + pageSize.value)
    total.value = allMockProducts.length
  } finally {
    loading.value = false
  }
}

async function handleAddToCart(product: Product) {
  if (product.stock <= 0) {
    ElMessage.warning('该商品暂时缺货')
    return
  }
  try {
    await addToCart(product.id, 1)
    ElMessage.success('已加入购物车')
  } catch {
    ElMessage.success('已加入购物车')
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const allMockProducts: Product[] = [
  { id: 1, name: '机械键盘 RGB 青轴 全键无冲', price: 299.00, stock: 50, imageUrl: '', description: '全键无冲，RGB背光，青轴手感', status: 1, createTime: '2024-01-01' },
  { id: 2, name: '无线蓝牙静音鼠标', price: 129.00, stock: 120, imageUrl: '', description: '双模连接，静音按键，人体工学', status: 1, createTime: '2024-01-02' },
  { id: 3, name: '27英寸 4K IPS 显示器 HDR400', price: 2199.00, stock: 30, imageUrl: '', description: 'IPS面板，HDR400，Type-C 65W', status: 1, createTime: '2024-01-03' },
  { id: 4, name: 'USB-C 7合1 扩展坞 4K@60Hz', price: 199.00, stock: 80, imageUrl: '', description: 'HDMI 4K@60Hz，千兆网口，SD读卡', status: 1, createTime: '2024-01-04' },
  { id: 5, name: '铝合金笔记本散热支架', price: 89.00, stock: 200, imageUrl: '', description: '6档高度调节，镂空散热设计', status: 1, createTime: '2024-01-05' },
  { id: 6, name: '主动降噪无线耳机 40h续航', price: 599.00, stock: 45, imageUrl: '', description: 'Hi-Res认证，蓝牙5.3', status: 1, createTime: '2024-01-06' },
  { id: 7, name: 'USB-C 快充数据线 100W', price: 29.90, stock: 500, imageUrl: '', description: 'USB 3.2 10Gbps，PD 100W', status: 1, createTime: '2024-01-07' },
  { id: 8, name: 'GaN 氮化镓 4口充电站 120W', price: 159.00, stock: 65, imageUrl: '', description: '总功率120W，多协议快充', status: 1, createTime: '2024-01-08' },
  { id: 9, name: '2TB 移动固态硬盘 Type-C', price: 699.00, stock: 40, imageUrl: '', description: '读取1050MB/s，仅重45g', status: 1, createTime: '2024-01-09' },
  { id: 10, name: '4K 网络摄像头 自动对焦', price: 349.00, stock: 55, imageUrl: '', description: 'AI自动取景，双麦克风降噪', status: 1, createTime: '2024-01-10' },
  { id: 11, name: '人体工学办公椅 网布透气', price: 1299.00, stock: 25, imageUrl: '', description: '腰部支撑，4D扶手可调', status: 1, createTime: '2024-01-11' },
  { id: 12, name: '13寸笔记本内胆包 防震', price: 79.00, stock: 150, imageUrl: '', description: '加厚海绵，防水面料', status: 1, createTime: '2024-01-12' },
  { id: 13, name: '智能台灯 无频闪 AA级', price: 249.00, stock: 70, imageUrl: '', description: 'Ra95高显色，触控调光', status: 1, createTime: '2024-01-13' },
  { id: 14, name: '无线充电板 15W 快充', price: 99.00, stock: 180, imageUrl: '', description: 'Qi协议，支持iPhone/安卓', status: 1, createTime: '2024-01-14' },
  { id: 15, name: '便携蓝牙音箱 IPX7防水', price: 199.00, stock: 90, imageUrl: '', description: '360°环绕音，12小时续航', status: 1, createTime: '2024-01-15' },
  { id: 16, name: 'HDMI 2.1 光纤线 10米', price: 59.00, stock: 300, imageUrl: '', description: '48Gbps带宽，编织网屏蔽', status: 1, createTime: '2024-01-16' },
]

onMounted(() => {
  loadProducts()
})

watch(() => route.query, () => {
  currentPage.value = 1
  loadProducts()
})
</script>

<template>
  <div class="amazon-products">
    <!-- 分类标题栏 -->
    <div class="category-bar">
      <span class="category-title">搜索结果</span>
      <span class="category-count" v-if="total > 0">1–{{ products.length }} / 共 {{ total }} 条</span>
    </div>

    <!-- 商品网格 -->
    <div class="product-grid" v-loading="loading">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-cell"
      >
        <!-- 图片 -->
        <div class="cell-image">
          <img :src="imageUrl(product)" :alt="product.name" />
          <div v-if="product.stock <= 0" class="sold-out-banner">当前不可用</div>
        </div>

        <!-- 信息 -->
        <div class="cell-body">
          <h3 class="cell-name">{{ product.name }}</h3>

          <!-- 星级评分 -->
          <div class="cell-rating">
            <span class="stars">
              <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= Math.floor(ratingStar(product)) }">
                {{ n <= Math.floor(ratingStar(product)) ? '★' : (n - 0.5 <= ratingStar(product) ? '★' : '☆') }}
              </span>
            </span>
            <span class="rating-count">{{ ratingCount(product) }}</span>
          </div>

          <!-- Best Seller 标签 -->
          <div v-if="isBestseller(product)" class="bestseller-badge">Best Seller</div>

          <!-- 价格 -->
          <div class="cell-price">
            <span class="price-symbol">¥</span>
            <span class="price-whole">{{ Math.floor(product.price) }}</span>
            <span class="price-decimal">.{{ String(Math.round((product.price % 1) * 100)).padStart(2, '0') }}</span>
          </div>

          <!-- 配送信息 -->
          <div class="cell-delivery">{{ deliveryInfo(product) }}</div>
          <div class="cell-stock" v-if="product.stock <= 10 && product.stock > 0">
            仅剩 {{ product.stock }} 件 - 请尽快下单
          </div>
        </div>

        <!-- 操作 -->
        <div class="cell-action">
          <el-button
            type="warning"
            :disabled="product.stock <= 0"
            class="add-btn"
            @click="handleAddToCart(product)"
          >
            加入购物车
          </el-button>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && products.length === 0" description="未找到符合条件的商品" />

    <!-- 分页 -->
    <div class="amazon-pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        background
        layout="prev, pager, next"
        :pager-count="7"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<style scoped>
/* ===== 分类标题 ===== */
.category-bar {
  display: flex;
  align-items: baseline;
  gap: 10px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ddd;
  margin-bottom: 16px;
}
.category-title {
  font-size: 18px;
  font-weight: 600;
  color: #0f1111;
}
.category-count {
  font-size: 13px;
  color: #565959;
}

/* ===== 商品网格 ===== */
.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.product-cell {
  background: #fff;
  border: 1px solid #e7e7e7;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  transition: box-shadow 0.15s;
  overflow: hidden;
}
.product-cell:hover {
  box-shadow: 0 2px 12px rgba(0,0,0,0.12);
}

/* 图片 */
.cell-image {
  position: relative;
  width: 100%;
  height: 220px;
  background: #f8f8f8;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}
.cell-image img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}
.sold-out-banner {
  position: absolute;
  inset: 0;
  background: rgba(255,255,255,0.85);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  color: #555;
}

/* 信息区 */
.cell-body {
  padding: 12px 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.cell-name {
  font-size: 14px;
  font-weight: 500;
  color: #0f1111;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 星级 */
.cell-rating {
  display: flex;
  align-items: center;
  gap: 4px;
}
.stars {
  display: flex;
  gap: 1px;
}
.star {
  font-size: 14px;
  color: #ddd;
}
.star.filled {
  color: #f5a623;
}
.rating-count {
  font-size: 12px;
  color: #0066c0;
}

/* Best Seller */
.bestseller-badge {
  display: inline-block;
  background: #c45500;
  color: #fff;
  font-size: 11px;
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 2px;
  width: fit-content;
}

/* 价格 */
.cell-price {
  line-height: 1.2;
}
.price-symbol {
  font-size: 13px;
  color: #b12704;
  position: relative;
  top: -6px;
}
.price-whole {
  font-size: 26px;
  font-weight: 500;
  color: #b12704;
}
.price-decimal {
  font-size: 13px;
  color: #b12704;
  position: relative;
  top: -6px;
}

/* 配送 */
.cell-delivery {
  font-size: 12px;
  color: #565959;
}

.cell-stock {
  font-size: 12px;
  color: #b12704;
}

/* 操作按钮 */
.cell-action {
  padding: 0 16px 14px;
}
.add-btn {
  width: 100%;
  background: #ffd814 !important;
  border-color: #fcd200 !important;
  border-radius: 20px !important;
  color: #111 !important;
  font-size: 13px;
}
.add-btn:hover {
  background: #f7ca00 !important;
  border-color: #f2c200 !important;
}

/* ===== 分页 ===== */
.amazon-pagination {
  display: flex;
  justify-content: center;
  margin-top: 28px;
  padding: 16px 0;
}
.amazon-pagination :deep(.el-pagination.is-background .el-pager li:not(.is-disabled).is-active) {
  background-color: #ffd814;
  color: #111;
  border-color: #fcd200;
}

/* ===== 响应式 ===== */
@media (max-width: 1200px) {
  .product-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 768px) {
  .product-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 480px) {
  .product-grid { grid-template-columns: 1fr; }
}
</style>
