import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import './styles/index.scss'

// ❌ 删除：import { initKeycloak } from './utils/auth'

// ✅ 改为：直接初始化 Vue，不再等待 Keycloak
const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(Antd)

app.mount('#app')