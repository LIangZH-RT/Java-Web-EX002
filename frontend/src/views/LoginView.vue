<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { login, register } from '@/api/authApi'

const router = useRouter()
const activeTab = ref('login')

const loginForm = reactive({
  username: '',
  password: '',
})

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const loginLoading = ref(false)
const registerLoading = ref(false)

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请填写用户名和密码')
    return
  }
  loginLoading.value = true
  try {
    await login(loginForm.username, loginForm.password)
    ElMessage.success('登录成功')
    router.push('/')
  } catch {
    // 错误消息由 axios 响应拦截器统一展示。
  } finally {
    loginLoading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请填写必填项')
    return
  }
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次密码不一致')
    return
  }
  registerLoading.value = true
  try {
    await register(registerForm.username, registerForm.password)
    ElMessage.success('注册成功，请继续购物')
    router.push('/')
  } catch {
    // 错误消息由 axios 响应拦截器统一展示。
  } finally {
    registerLoading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <!-- Logo 区 -->
    <div class="login-logo" @click="router.push('/')">
      <i class="layui-icon layui-icon-cart-simple layui-font-30"></i>
      <span class="logo-text">ShopStore</span>
    </div>

    <!-- 卡片 -->
    <div class="login-card layui-panel">
      <el-tabs v-model="activeTab" class="login-tabs" stretch>
        <el-tab-pane label="登录" name="login">
          <p class="tab-subtitle layui-font-13">使用用户名和密码登录您的账户</p>
          <el-form :model="loginForm" label-position="top" size="large">
            <el-form-item label="用户名">
              <el-input v-model="loginForm.username" placeholder="请输入用户名">
                <template #prefix>
                  <i class="layui-icon layui-icon-username"></i>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password>
                <template #prefix>
                  <i class="layui-icon layui-icon-password"></i>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loginLoading" class="submit-btn" @click="handleLogin">
                <i class="layui-icon layui-icon-ok"></i> 登录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="help-links">
            <a href="#">忘记密码？</a>
            <a href="#">需要帮助？</a>
          </div>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <p class="tab-subtitle layui-font-13">创建一个新账户开始购物</p>
          <el-form :model="registerForm" label-position="top" size="large">
            <el-form-item label="用户名">
              <el-input v-model="registerForm.username" placeholder="最长 45 个字符">
                <template #prefix>
                  <i class="layui-icon layui-icon-username"></i>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="registerForm.password" type="password" placeholder="至少 6 位密码" show-password>
                <template #prefix>
                  <i class="layui-icon layui-icon-password"></i>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="再次输入密码" show-password>
                <template #prefix>
                  <i class="layui-icon layui-icon-password"></i>
                </template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="registerLoading" class="submit-btn" @click="handleRegister">
                <i class="layui-icon layui-icon-ok-circle"></i> 注册
              </el-button>
            </el-form-item>
          </el-form>
          <p class="agreement-text">
            注册即表示您同意我们的 <a href="#">使用条款</a> 和 <a href="#">隐私政策</a>
          </p>
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 底部新用户提示 -->
    <div class="login-footer">
      <div class="divider-line"><span>新用户？</span></div>
      <el-button class="create-account-btn" @click="activeTab = 'register'">
        创建您的 ShopStore 账户
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  max-width: 400px;
  margin: 0 auto;
  padding: 20px 0;
}
.login-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 20px;
  cursor: pointer;
}
.logo-text {
  font-size: 28px;
  font-weight: 700;
  color: #111;
}
.login-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px 24px 24px;
}
.login-tabs :deep(.el-tabs__nav-wrap::after) { height: 1px; }
.tab-subtitle {
  font-size: 13px;
  color: #555;
  margin-bottom: 16px;
}
.submit-btn {
  width: 100%;
  background: #ffd814 !important;
  border-color: #fcd200 !important;
  color: #111 !important;
  font-weight: 500;
}
.submit-btn:hover {
  background: #f7ca00 !important;
  border-color: #f2c200 !important;
}
.help-links {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}
.help-links a { color: #0066c0; }
.help-links a:hover { text-decoration: underline; color: #c45500; }
.agreement-text {
  font-size: 12px;
  color: #555;
  line-height: 1.6;
  margin-top: -8px;
}
.agreement-text a { color: #0066c0; }
.login-footer {
  margin-top: 20px;
  text-align: center;
}
.divider-line {
  display: flex;
  align-items: center;
  color: #767676;
  font-size: 12px;
  margin-bottom: 10px;
}
.divider-line::before,
.divider-line::after {
  content: '';
  flex: 1;
  height: 1px;
  background: #ddd;
}
.divider-line span {
  padding: 0 8px;
}
.create-account-btn {
  width: 100%;
  background: #fff;
  border: 1px solid #d5d9d9;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(213,217,217,.5);
}
.create-account-btn:hover { background: #f7fafa; }
</style>
