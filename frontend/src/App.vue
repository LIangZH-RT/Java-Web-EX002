<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

function scrollToTop() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<template>
  <div class="app-container">
    <!-- 顶部暗色导航条 -->
    <div class="top-navbar">
      <div class="nav-inner">
        <div class="nav-left" @click="router.push('/')">
          <span class="nav-logo">ShopStore</span>
        </div>

        <div class="nav-spacer"></div>

        <div class="nav-right">
          <div class="nav-item" @click="router.push('/login')">
            <span class="nav-label">你好, 请登录</span>
            <span class="nav-title">账户与列表</span>
          </div>
          <div class="nav-item" @click="router.push('/products')">
            <span class="nav-label">浏览</span>
            <span class="nav-title">全部商品</span>
          </div>
          <div class="nav-item nav-cart" @click="router.push('/cart')">
            <span class="nav-title cart-label">购物车</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容 -->
    <el-main class="app-main">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <div class="app-footer">
      <div class="footer-back">
        <a href="#" @click.prevent="scrollToTop">回到顶部</a>
      </div>
      <div class="footer-main">
        <div class="footer-col">
          <h4>了解我们</h4>
          <a href="#">关于 ShopStore</a>
          <a href="#">新闻中心</a>
          <a href="#">可持续发展</a>
        </div>
        <div class="footer-col">
          <h4>合作联系</h4>
          <a href="#">卖家入驻</a>
          <a href="#">联盟营销</a>
          <a href="#">广告推广</a>
        </div>
        <div class="footer-col">
          <h4>帮助中心</h4>
          <a href="#">配送说明</a>
          <a href="#">退换货政策</a>
          <a href="#">客户服务</a>
        </div>
        <div class="footer-col">
          <h4>商务合作</h4>
          <a href="#">大宗采购</a>
          <a href="#">员工福利</a>
          <a href="#">礼品卡</a>
        </div>
      </div>
      <div class="footer-bottom">
        <span>© 2024 ShopStore 在线购物系统 — 实验二 MVC 设计模式</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* ===== 顶部暗色导航栏 ===== */
.top-navbar {
  background: #131921;
  color: #fff;
  height: 60px;
  position: sticky;
  top: 0;
  z-index: 100;
}
.nav-inner {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 16px;
}
.nav-left {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 2px;
  border: 1px solid transparent;
}
.nav-left:hover { border-color: #fff; }
.nav-logo {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: -0.5px;
}

.nav-spacer {
  flex: 1;
}

.nav-right {
  display: flex;
  align-items: center;
  gap: 4px;
}
.nav-item {
  display: flex;
  flex-direction: column;
  padding: 6px 12px;
  cursor: pointer;
  border: 1px solid transparent;
  border-radius: 2px;
  line-height: 1.2;
}
.nav-item:hover { border-color: #fff; }
.nav-label { font-size: 11px; color: #ccc; }
.nav-title { font-size: 13px; font-weight: 700; }
.nav-cart {
  flex-direction: row;
  align-items: flex-end;
}
.cart-label { margin-bottom: 0; }

/* ===== 主内容 ===== */
.app-main {
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 20px 16px;
  min-height: calc(100vh - 200px);
}

/* ===== 底部 ===== */
.footer-back {
  background: #37475a;
  text-align: center;
  padding: 14px;
}
.footer-back a { color: #fff; font-size: 13px; }
.footer-back:hover { background: #485769; }
.footer-main {
  background: #232f3e;
  display: flex;
  justify-content: center;
  gap: 80px;
  padding: 40px 20px;
  flex-wrap: wrap;
}
.footer-col { display: flex; flex-direction: column; gap: 8px; }
.footer-col h4 { color: #fff; font-size: 15px; margin-bottom: 4px; }
.footer-col a { color: #ddd; font-size: 13px; }
.footer-col a:hover { text-decoration: underline; }
.footer-bottom {
  background: #131a22;
  text-align: center;
  padding: 20px;
}
.footer-bottom span { color: #999; font-size: 12px; }
</style>
