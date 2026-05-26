<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getCartList, updateCartItem, updateCartSelected, deleteCartItem, type CartItem } from '@/api/cartApi'
import type { PageResult } from '@/api/productApi'

const router = useRouter()
const cartItems = ref<CartItem[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(8)
const total = ref(0)

const selectedItems = computed(() => cartItems.value.filter((item) => item.selected))
const selectedQuantity = computed(() => selectedItems.value.reduce((sum, item) => sum + item.quantity, 0))
const totalPrice = computed(() => {
  return selectedItems.value.reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
})
const allSelected = computed(() => cartItems.value.length > 0 && cartItems.value.every((item) => item.selected))
const partiallySelected = computed(() => selectedItems.value.length > 0 && !allSelected.value)

function toChecked(value: boolean | string | number) {
  return value === true || value === 'true' || value === 1
}

function imageUrl(item: CartItem): string {
  const value = item.productImage?.trim()
  if (value && /^(https?:\/\/|data:image\/|\/)/i.test(value)) {
    return value
  }
  return '/images/products/no-image.png'
}

async function loadCart() {
  loading.value = true
  try {
    const res = await getCartList(currentPage.value, pageSize.value)
    const data = res.data as PageResult<CartItem>
    cartItems.value = data.records
    total.value = data.total
  } catch (error: any) {
    cartItems.value = []
    total.value = 0
    if (error.response?.status === 401) {
      router.push('/login')
    }
  } finally {
    loading.value = false
  }
}

async function handleQuantityChange(item: CartItem, newQuantity: number | string) {
  const qty = Number(newQuantity)
  if (!qty || qty < 1) {
    ElMessage.warning('数量不能小于1')
    loadCart()
    return
  }
  if (qty > item.stock) {
    ElMessage.warning(`库存不足，当前库存 ${item.stock}`)
    loadCart()
    return
  }
  try {
    await updateCartItem(item.id, qty)
    ElMessage.success('数量已更新')
    loadCart()
  } catch {
    loadCart()
  }
}

async function handleSelectedChange(item: CartItem, value: boolean | string | number) {
  const selected = toChecked(value)
  const previous = item.selected
  item.selected = selected
  try {
    await updateCartSelected(item.id, selected)
  } catch {
    item.selected = previous
    loadCart()
  }
}

async function handleSelectAllChange(value: boolean | string | number) {
  const selected = toChecked(value)
  const previous = cartItems.value.map((item) => ({ id: item.id, selected: item.selected }))
  cartItems.value.forEach((item) => {
    item.selected = selected
  })
  try {
    await Promise.all(cartItems.value.map((item) => updateCartSelected(item.id, selected)))
  } catch {
    previous.forEach((state) => {
      const item = cartItems.value.find((cartItem) => cartItem.id === state.id)
      if (item) {
        item.selected = state.selected
      }
    })
    loadCart()
  }
}

async function handleDelete(item: CartItem) {
  try {
    await ElMessageBox.confirm(
      `确定要从购物车中移除「${item.productName}」吗？`,
      '确认删除',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' },
    )
  } catch {
    return
  }

  try {
    await deleteCartItem(item.id)
    ElMessage.success('已移除')
    loadCart()
  } catch {
    loadCart()
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadCart()
}

function goShopping() {
  router.push('/products')
}

onMounted(() => {
  loadCart()
})
</script>

<template>
  <div>
    <div class="page-header">
      <h2><i class="layui-icon layui-icon-cart"></i> 购物车</h2>
      <p class="layui-font-13">管理已添加的商品，修改数量或移除商品</p>
    </div>

    <div class="card-container layui-panel" v-loading="loading">
      <template v-if="cartItems.length > 0">
        <el-table
          :data="cartItems"
          style="width: 100%"
          :header-cell-style="{ background: '#f5f7fa', color: '#303133', fontWeight: 600 }"
        >
          <el-table-column width="58" align="center">
            <template #header>
              <el-checkbox
                :model-value="allSelected"
                :indeterminate="partiallySelected"
                @change="handleSelectAllChange"
              />
            </template>
            <template #default="{ row }">
              <el-checkbox
                :model-value="row.selected"
                @change="(val: boolean | string | number) => handleSelectedChange(row, val)"
              />
            </template>
          </el-table-column>

          <el-table-column label="商品信息" min-width="360">
            <template #default="{ row }">
              <div class="cart-product-cell">
                <img :src="imageUrl(row)" :alt="row.productName" class="cart-product-img" />
                <div class="cart-product-info">
                  <span class="cart-product-name">{{ row.productName }}</span>
                  <span class="cart-product-price">¥{{ row.productPrice.toFixed(2) }}</span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">
              <span class="price-text">¥{{ row.productPrice.toFixed(2) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="数量" width="160" align="center">
            <template #default="{ row }">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="row.stock"
                size="small"
                @change="(val: number | string | undefined) => handleQuantityChange(row, val ?? 1)"
              />
            </template>
          </el-table-column>

          <el-table-column label="小计" width="140" align="center">
            <template #default="{ row }">
              <span class="subtotal-text">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="100" align="center">
            <template #default="{ row }">
              <el-button type="danger" link @click="handleDelete(row)">
                <i class="layui-icon layui-icon-delete"></i> 移除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="cart-footer">
          <div class="cart-total">
            <span class="selected-count">已选 {{ selectedQuantity }} 件，</span>
            <span class="total-label layui-font-16">合计：</span>
            <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
          </div>
          <div class="cart-actions">
            <el-button @click="goShopping">
              <i class="layui-icon layui-icon-cart-simple"></i> 继续购物
            </el-button>
            <el-button type="primary" size="large" :disabled="selectedQuantity === 0">
              <i class="layui-icon layui-icon-rmb"></i> 去结算 ({{ selectedQuantity }} 件)
            </el-button>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            background
            layout="total, prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </template>

      <el-empty v-else description="购物车是空的">
        <el-button type="primary" @click="goShopping">
          <i class="layui-icon layui-icon-cart"></i> 去逛逛
        </el-button>
      </el-empty>
    </div>
  </div>
</template>

<style scoped>
.cart-product-cell {
  display: flex;
  align-items: center;
  gap: 14px;
}

.cart-product-img {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  object-fit: cover;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cart-product-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.cart-product-name {
  font-size: 15px;
  font-weight: 500;
  color: #303133;
}

.cart-product-price {
  font-size: 13px;
  color: #909399;
}

.price-text {
  color: #606266;
  font-weight: 500;
}

.subtotal-text {
  color: #f56c6c;
  font-weight: 600;
  font-size: 15px;
}

.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.cart-total {
  display: flex;
  align-items: center;
  gap: 4px;
}

.total-label {
  font-size: 16px;
  color: #606266;
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.cart-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>
