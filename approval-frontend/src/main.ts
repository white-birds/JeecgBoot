import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import './styles/index.scss'

// 导入 Keycloak 初始化工具
import { initKeycloak } from './utils/auth'

// 核心：等 Keycloak 认证完了再挂载 Vue
initKeycloak(() => {
    const app = createApp(App)

    app.use(createPinia())
    app.use(router)
    app.use(Antd)

    app.mount('#app')
})