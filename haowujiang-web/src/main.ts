/**
 * main.ts — 应用入口文件
 *
 * 这是整个 Vue 应用的启动入口。Vite 构建时会从这个文件开始，
 * 递归解析所有依赖，最终打包成一个可运行的前端应用。
 *
 * 执行流程：
 * 1. createApp(App) —— 创建 Vue 应用实例，根组件为 App.vue
 * 2. .use(ElementPlus) —— 安装 Element Plus 组件库（全局注册所有 UI 组件）
 * 3. .mount('#app') —— 将应用挂载到 index.html 中的 <div id="app"> 上
 *
 * Element Plus 是饿了么团队出品的 Vue 3 组件库，
 * 提供了 el-button、el-card、el-table 等丰富的 UI 组件。
 */
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import App from './App.vue'
import 'element-plus/dist/index.css'  // Element Plus 的全局样式
import './styles.css'                  // 项目自定义样式

createApp(App)
  .use(ElementPlus)
  .mount('#app')
