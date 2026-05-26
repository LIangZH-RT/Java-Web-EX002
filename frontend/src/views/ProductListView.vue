<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getProducts, type Product, type PageResult } from '@/api/productApi'

const route = useRoute()

const products = ref<Product[]>([])
const loading = ref(false)
const loadFailed = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)
const rangeStart = computed(() => (currentPage.value - 1) * pageSize.value + 1)
const rangeEnd = computed(() => (currentPage.value - 1) * pageSize.value + products.value.length)

function displayImageUrl(product: Product): string | null {
  const value = product.imageUrl?.trim()
  if (!value) {
    return null
  }
  return /^(https?:\/\/|data:image\/|\/)/i.test(value) ? value : null
}

async function loadProducts() {
  loading.value = true
  loadFailed.value = false
  try {
    const res = await getProducts(currentPage.value, pageSize.value)
    const data = res.data as PageResult<Product>
    products.value = data.records
    total.value = data.total
  } catch {
    products.value = []
    total.value = 0
    loadFailed.value = true
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadProducts()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

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
      <span class="category-title">
        <i class="layui-icon layui-icon-list"></i> 搜索结果
      </span>
      <span class="category-count layui-font-13" v-if="total > 0">{{ rangeStart }}-{{ rangeEnd }} / 共 {{ total }} 条</span>
    </div>

    <el-alert
      v-if="loadFailed"
      class="load-alert"
      title="商品加载失败，请检查后端服务和数据库连接"
      type="error"
      :closable="false"
      show-icon
    />

    <!-- 商品网格 -->
    <div class="product-grid" v-loading="loading">
      <div
        v-for="product in products"
        :key="product.id"
        class="product-cell"
      >
        <!-- 图片 -->
        <div class="cell-image">
          <img v-if="displayImageUrl(product)" :src="displayImageUrl(product)!" :alt="product.name" />
          <div v-else class="no-image">暂无图片</div>
        </div>

        <!-- 信息 -->
        <div class="cell-body">
          <h3 class="cell-name">{{ product.name }}</h3>

          <p class="cell-description">{{ product.description || '暂无描述' }}</p>

          <!-- 价格 -->
          <div class="cell-price">
            <span class="price-symbol">¥</span>
            <span class="price-whole">{{ Math.floor(product.price) }}</span>
            <span class="price-decimal">.{{ String(Math.round((product.price % 1) * 100)).padStart(2, '0') }}</span>
          </div>

        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && !loadFailed && products.length === 0" description="暂无商品数据" />

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
.load-alert {
  margin-bottom: 16px;
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
.no-image {
  color: #8c8c8c;
  font-size: 13px;
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

.cell-description {
  min-height: 34px;
  font-size: 12px;
  line-height: 1.45;
  color: #565959;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
