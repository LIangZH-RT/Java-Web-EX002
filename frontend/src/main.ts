import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import App from './App.vue'
import router from './router'
import './assets/css/main.css'

const app = createApp(App)

app.use(ElementPlus, { locale: {
  el: {
    pagination: {
      goto: '跳至',
      pagesize: '条/页',
      total: '共 {total} 条',
      pageClassifier: '页',
      page: '页',
      prev: '上一页',
      next: '下一页',
      currentPage: '第 {currentPage} 页',
      prev5: '向前 5 页',
      next5: '向后 5 页',
    },
  },
} })
app.use(router)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
