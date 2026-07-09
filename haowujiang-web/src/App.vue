<!--
  App.vue — 应用根组件

  这是整个单页应用（SPA）的根组件，负责：
  1. 全局状态管理 —— 使用 Vue 3 的 reactive() 管理共享数据
  2. 路由/导航 —— 通过 activeView 变量实现视图切换（无需 vue-router）
  3. 用户认证 —— 登录/登出流程，JWT token 管理
  4. API 调用封装 —— runApi() 统一处理请求的 loading 状态和错误
  5. 子视图调度 —— 根据 activeView 动态渲染对应的视图组件

  Vue 3 核心概念在本文件中的体现：
  - reactive()：创建响应式对象，数据变化时自动更新视图
  - ref()：创建响应式引用，适合基本类型和需要整体替换的值
  - computed()：计算属性，依赖变化时自动重新计算，有缓存机制
  - onMounted()：生命周期钩子，组件挂载后执行（类似 React 的 useEffect([], [])）
  - v-if/v-else-if：条件渲染
  - v-for：列表渲染
  - v-model：双向绑定
  - :attr（v-bind 简写）：单向绑定属性
  - @event（v-on 简写）：事件监听
-->
<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { Component } from 'vue'
// Element Plus 的消息提示组件（类似 toast）
import { ElMessage } from 'element-plus'
// Element Plus 图标 —— 每个图标都是一个 Vue 组件
import {
  Collection,
  DataAnalysis,
  House,
  Iphone,
  Key,
  Lock,
  Notebook,
  Refresh,
  Shop,
  SwitchButton,
  Timer,
  Trophy,
  UserFilled,
  VideoPlay
} from '@element-plus/icons-vue'
// 本地静态数据（武将、RAG 种子等）
import { generals as catalogGenerals } from './data/catalog'
// 后端 API 函数
import {
  addRagDocument,
  claimSeckill,
  clearIdentity,
  createBattle,
  endBattleTurn,
  fallbackRagDocuments,
  getApiBase,
  getSeckillState,
  getStoredIdentity,
  listGenerals,
  listOwnedGenerals,
  listRagDocuments,
  login,
  passBattleResponse,
  playBattleCard,
  respondBattleCard,
  saveIdentity
} from './services/api'
// 视图组件
import ArchitectureView from './views/ArchitectureView.vue'
import BattleView from './views/BattleView.vue'
import CenterView from './views/CenterView.vue'
import DetailView from './views/DetailView.vue'
import PlazaView from './views/PlazaView.vue'
import RagAdminView from './views/RagAdminView.vue'
import SeckillView from './views/SeckillView.vue'
// 类型定义
import type {
  BattleState,
  General,
  GeneralId,
  ProductState,
  RagDocument,
  SeckillResult,
  UserBasicVo,
  ViewKey
} from './types'

// ============================================================
// 类型定义
// ============================================================

/** 导航菜单项 —— 左侧侧边栏的每个菜单 */
type NavItem = {
  key: ViewKey        // 视图标识
  label: string       // 菜单标题
  meta: string        // 副标题（技术标签）
  icon: Component     // 图标组件
  adminOnly?: boolean // 是否仅管理员可见
}

// ============================================================
// 全局响应式状态（应用级别共享数据）
// ============================================================

/**
 * reactive() vs ref() 的选择：
 * - reactive() 适合对象/数组，直接 .属性 访问，不需要 .value
 * - ref() 适合基本类型，访问需要 .value，但在模板中自动解包
 */

// 产品全局状态 —— 使用 reactive 让所有子组件自动响应数据变化
const state = reactive<ProductState>({
  ownedGeneralIds: [],       // 用户拥有的武将 ID 列表
  seckill: {                 // 秒杀状态（初始为空，由后端填充）
    targetGeneralId: 'zhao-yun',
    stock: 0,
    total: 0,
    claimedUserIds: [],
    orders: [],
    events: []
  },
  ragDocuments: fallbackRagDocuments()  // 初始使用本地降级数据
})

// 武将列表 —— 初始为本地静态数据，后端返回后替换
const allGenerals = ref<General[]>(catalogGenerals)

// 当前显示的视图页面
const activeView = ref<ViewKey>('plaza')

// 详情页选中的武将
const selectedGeneralId = ref<GeneralId>('zhao-yun')

// 对战页敌方武将
const battleEnemyGeneralId = ref<GeneralId>('zhao-yun')

// 最近一次秒杀的结果（null 表示还没操作过）
const lastSeckillResult = ref<SeckillResult | null>(null)

// 对战完整状态（null 表示还没创建对战）
const battleState = ref<BattleState | null>(null)

// Loading 状态标志
const apiBusy = ref(false)       // 后端数据同步中
const battleBusy = ref(false)    // 对战操作进行中
const loginBusy = ref(false)     // 登录请求进行中

// 状态消息 —— 显示在左侧栏底部
const apiMessage = ref('')

// 用户身份信息 —— 从 localStorage 读取（即使未登录也有默认值）
const identity = reactive(getStoredIdentity())

// 当前登录用户的信息（登录成功后由后端返回）
const currentUser = ref<UserBasicVo | null>(null)

// 登录表单 —— 双向绑定到登录页的输入框
const loginForm = reactive({
  phone: '13800000000',   // 默认管理员手机号
  password: '123456'
})

// 后端 API 地址（展示用）
const apiBase = getApiBase()

// ============================================================
// 导航菜单配置
// ============================================================

/**
 * 左侧导航菜单项
 *
 * v-for 遍历生成菜单，每个 item 的 key 对应 activeView 的值，
 * 点击菜单时通过 handleMenuSelect 更新 activeView 实现页面切换。
 * adminOnly: true 的项只有管理员（role='1'）才能点击。
 */
const navItems: NavItem[] = [
  { key: 'plaza', label: '武将广场', meta: 'Shop', icon: Shop },
  { key: 'center', label: '武将中心', meta: 'Owned', icon: Collection },
  { key: 'seckill', label: '秒杀武将', meta: 'Redis Lua', icon: Timer },
  { key: 'battle', label: '对战演示', meta: 'AI Battle', icon: Trophy },
  { key: 'rag', label: 'RAG 后台', meta: 'Admin', icon: Notebook, adminOnly: true },
  { key: 'architecture', label: '系统蓝图', meta: 'Backend', icon: DataAnalysis }
]

// ============================================================
// 演示账号配置
// ============================================================

/**
 * 预置演示账号
 *
 * 用于快速切换管理员/普通用户身份，
 * 点击后自动填充登录表单。
 */
const demoAccounts = [
  {
    label: '管理员',
    phone: '13800000000',
    password: '123456',
    desc: '可进入 RAG 后台与所有演示功能'
  },
  {
    label: '普通用户',
    phone: '13900000000',
    password: '123456',
    desc: '用于体验武将中心、秒杀和对战'
  }
]

// ============================================================
// 计算属性
// ============================================================

/**
 * computed() 计算属性
 *
 * 计算属性会缓存结果，只有依赖的响应式数据变化时才会重新计算。
 * 比在模板中写复杂表达式更高效、更易读。
 */

/** 是否已登录 —— token 非空即为已登录 */
const isAuthenticated = computed(() => Boolean(identity.token))

/** 已拥有的武将列表 —— 从全部武将中筛选 */
const ownedGenerals = computed(() => allGenerals.value.filter((general) => state.ownedGeneralIds.includes(general.id)))

/** 当前选中的武将 —— 优先从后端数据查找，找不到用本地数据兜底 */
const selectedGeneral = computed(() => findGeneral(selectedGeneralId.value))

/** 当前选中武将的 RAG 文档 */
const selectedRagDocuments = computed(() => state.ragDocuments.filter((doc) => doc.generalId === selectedGeneralId.value))

/** 赵云数据（用于广场页快速访问） */
const zhaoYun = computed(() => findGeneral('zhao-yun'))

/** 张飞数据（用于广场页快速访问） */
const zhangFei = computed(() => findGeneral('zhang-fei'))

/** 对战中的敌方武将 */
const battleEnemyGeneral = computed(() => findGeneral(battleEnemyGeneralId.value))

/** 用户角色标签文本 */
const userRoleLabel = computed(() => identity.role === '1' ? '管理员' : '普通用户')

/** 用户显示名称 —— 优先用昵称，其次手机号，最后用 ID */
const userDisplayName = computed(() => currentUser.value?.nickname || currentUser.value?.phone || `用户 ${identity.userId}`)

/** 后端状态指示器的类型（success=正常, info=加载中, warning=有消息） */
const statusType = computed(() => apiMessage.value ? 'warning' : apiBusy.value ? 'info' : 'success')

/** 后端状态指示器的文本 */
const statusMessage = computed(() => apiMessage.value || (apiBusy.value ? '正在同步后端数据' : '后端数据已接入'))

// ============================================================
// 生命周期
// ============================================================

/**
 * onMounted —— Vue 3 生命周期钩子
 *
 * 组件挂载到 DOM 后执行，适合发起初始数据请求。
 * 类似于 React 的 useEffect(() => { ... }, [])。
 *
 * 这里根据是否已登录，决定拉取不同范围的数据：
 * - 已登录：拉取完整数据（武将 + 秒杀 + 已拥有 + RAG）
 * - 未登录：只拉取公开数据（武将 + 秒杀状态）
 */
onMounted(() => {
  if (isAuthenticated.value) {
    refreshBackend()       // 已登录，获取完整数据
    return
  }
  refreshPublicBackend()   // 未登录，只获取公开数据
})

// ============================================================
// 导航与视图切换
// ============================================================

/** 打开武将详情页 */
function openDetail(id: GeneralId) {
  selectedGeneralId.value = id
  activeView.value = 'detail'
}

/** 打开对战页 —— 默认敌方为赵云 */
async function openBattle(id?: GeneralId) {
  battleEnemyGeneralId.value = id && id !== 'zhang-fei' ? id : 'zhao-yun'
  activeView.value = 'battle'
  await startBattle()
}

/** 打开秒杀页 */
function openSeckill() {
  activeView.value = 'seckill'
}

/** 侧边栏菜单选择处理 */
function handleMenuSelect(key: string) {
  activeView.value = key as ViewKey
}

/** 填充演示账号到登录表单 */
function fillDemoAccount(account: typeof demoAccounts[number]) {
  loginForm.phone = account.phone
  loginForm.password = account.password
}

// ============================================================
// 秒杀操作
// ============================================================

/**
 * 秒杀赵云
 *
 * 流程：
 * 1. 调用后端秒杀接口
 * 2. 保存返回的秒杀结果（含执行步骤）
 * 3. 更新本地秒杀状态
 * 4. 刷新已拥有武将列表（秒杀成功则新增赵云）
 */
async function claimZhaoYun() {
  await runApi(async () => {
    lastSeckillResult.value = await claimSeckill('zhao-yun')
    if (lastSeckillResult.value.state) {
      state.seckill = lastSeckillResult.value.state
      // 注意：这里用 .state 而不是直接修改属性，
      // 因为 reactive 对象替换整个属性时需要用 .value（如果是 ref）
    }
    await refreshOwnedGenerals()
  })
}

// ============================================================
// RAG 知识库操作
// ============================================================

/**
 * 新增 RAG 知识文档
 *
 * 只允许管理员操作。新文档添加到列表头部（unshift），
 * 这样最新的知识显示在最前面。
 */
async function createRagDocument(payload: Pick<RagDocument, 'generalId' | 'title' | 'content'>) {
  if (identity.role !== '1') {
    ElMessage.warning('RAG 后台需要管理员账号')
    return
  }
  await runApi(async () => {
    const document = await addRagDocument(payload)
    state.ragDocuments.unshift(document)  // 头部插入，最新的显示在最前
    ElMessage.success('知识已添加')
  })
}

// ============================================================
// 对战操作
// ============================================================

/**
 * 创建新对战
 *
 * 玩家使用张飞（手动操作），敌方使用选中的武将（AI 驱动）。
 * battleBusy 用于在对战创建期间显示 loading 状态。
 */
async function startBattle() {
  await runApi(async () => {
    battleBusy.value = true
    // 调用后端创建对战：参数是武将的后端数字 ID
    battleState.value = await createBattle(zhangFei.value.backendId, battleEnemyGeneral.value.backendId)
  }, () => {
    battleBusy.value = false
  })
}

/** 出牌 —— 玩家在自己的回合使用手牌 */
async function playCard(cardId: string) {
  if (!battleState.value?.id) {
    return
  }
  await runApi(async () => {
    battleState.value = await playBattleCard(battleState.value!.id!, cardId)
  })
}

/**
 * 出闪响应 —— 当敌方出杀时，玩家选择用闪抵消
 *
 * 注意：这里会先检查卡牌是否可用作响应（前端预判），
 * 最终由后端战斗引擎校验（防止前端篡改）。
 */
async function respondCard(cardId: string) {
  if (!battleState.value?.id) {
    return
  }
  await runApi(async () => {
    battleState.value = await respondBattleCard(battleState.value!.id!, cardId)
  })
}

/** 放弃响应 —— 不出闪，承受伤害 */
async function passResponse() {
  if (!battleState.value?.id) {
    return
  }
  await runApi(async () => {
    battleState.value = await passBattleResponse(battleState.value!.id!)
  })
}

/** 结束回合 —— 当前玩家回合结束，轮到对方行动 */
async function endTurn() {
  if (!battleState.value?.id) {
    return
  }
  await runApi(async () => {
    battleState.value = await endBattleTurn(battleState.value!.id!)
  })
}

// ============================================================
// 数据同步
// ============================================================

/**
 * 未登录时的公开数据同步
 *
 * 只拉取不需要登录的数据：武将列表 + 秒杀状态。
 * 如果在未登录状态，无法获取"已拥有武将"（因为不知道是谁）。
 */
async function refreshPublicBackend() {
  await runApi(async () => {
    apiBusy.value = true
    // Promise.all 并行请求，提高加载速度
    const [generalRows, seckill] = await Promise.all([
      listGenerals(),
      getSeckillState('zhao-yun')
    ])
    // 后端有数据就用后端的，否则降级为本地静态数据
    allGenerals.value = generalRows.length ? generalRows : catalogGenerals
    state.seckill = seckill
  }, () => {
    apiBusy.value = false
  })
}

/**
 * 已登录时的完整数据同步
 *
 * 额外拉取：已拥有武将 + RAG 文档（管理员）。
 * 这个方法也用于登录后、以及点击"同步数据"按钮时的刷新。
 */
async function refreshBackend() {
  await runApi(async () => {
    apiBusy.value = true
    const [generalRows, seckill] = await Promise.all([
      listGenerals(),
      getSeckillState('zhao-yun')
    ])
    allGenerals.value = generalRows.length ? generalRows : catalogGenerals
    state.seckill = seckill
    await refreshOwnedGenerals()  // 获取我的武将
    if (identity.role === '1') {
      await refreshRagDocuments()  // 管理员额外拉取 RAG 文档
    }
  }, () => {
    apiBusy.value = false
  })
}

/** 刷新已拥有的武将列表 */
async function refreshOwnedGenerals() {
  const rows = await listOwnedGenerals()
  state.ownedGeneralIds = rows.map((general) => general.id)
}

/** 刷新 RAG 知识库 —— 按武将分别拉取，最后合并为一个数组 */
async function refreshRagDocuments() {
  const groups = await Promise.all(
    allGenerals.value.map((general) => listRagDocuments(general.id).catch(() => []))
  )
  state.ragDocuments = groups.flat()  // flat() 将二维数组拍平为一维
}

// ============================================================
// 登录与登出
// ============================================================

/**
 * 登录处理
 *
 * 流程：
 * 1. 表单校验（手机号和密码不能为空）
 * 2. 调用后端登录接口
 * 3. 保存返回的 token 和用户信息到 localStorage
 * 4. 切换到广场页
 * 5. 同步后端数据
 *
 * try/catch/finally 的错误处理模式：
 * - try：正常执行
 * - catch：捕获异常，展示错误信息
 * - finally：无论成功失败都执行（关闭 loading）
 */
async function loginByPassword() {
  // 前端基础校验
  if (!loginForm.phone.trim() || !loginForm.password.trim()) {
    ElMessage.warning('请输入手机号和密码')
    return
  }

  loginBusy.value = true
  apiMessage.value = ''
  try {
    const result = await login(loginForm.phone.trim(), loginForm.password)
    // 保存身份信息 —— 后续请求自动携带这些信息
    identity.token = result.token
    identity.userId = String(result.userBasicVo.id)
    identity.role = String(result.userBasicVo.role)
    currentUser.value = result.userBasicVo
    saveIdentity(identity)          // 持久化到 localStorage
    activeView.value = 'plaza'     // 跳转到广场页
    await refreshBackend()          // 同步数据
    apiMessage.value = `已登录：${result.userBasicVo.nickname || result.userBasicVo.phone}`
    ElMessage.success(apiMessage.value)
  } catch (error) {
    const message = error instanceof Error ? error.message : '登录失败'
    apiMessage.value = message
    ElMessage.error(message)
  } finally {
    loginBusy.value = false
  }
}

/**
 * 登出处理
 *
 * 清除所有用户相关状态，回到未登录状态。
 */
function logout() {
  identity.token = ''
  currentUser.value = null
  clearIdentity()              // 清除 localStorage 中的 token
  activeView.value = 'plaza'  // 回到广场（未登录也能看）
  battleState.value = null     // 清除对战状态
  apiMessage.value = '已退出登录'
  ElMessage.info('已退出登录')
}

// ============================================================
// 通用工具函数
// ============================================================

/**
 * API 调用包装器 —— 统一处理 loading 和错误
 *
 * 所有后端接口调用都通过这个函数包装，实现：
 * 1. 统一的错误提示（ElMessage.error）
 * 2. 可选的后置回调（finallyWork）
 *
 * 为什么不直接在各个方法里写 try/catch？
 * - 避免重复代码，所有 API 调用共享同一套错误处理逻辑
 * - 如果以后要改错误处理方式（如接入 Sentry 错误上报），只需改这里
 *
 * @param work       要执行的异步操作
 * @param finallyWork 操作完成后的回调（无论成功失败都执行）
 */
async function runApi(work: () => Promise<void>, finallyWork?: () => void) {
  try {
    apiMessage.value = ''
    await work()
  } catch (error) {
    const message = error instanceof Error ? error.message : '接口请求失败'
    apiMessage.value = message
    if (isAuthenticated.value) {
      ElMessage.error(message)  // 已登录用户才弹错误提示
    }
  } finally {
    finallyWork?.()
  }
}

/**
 * 从数据源查找武将
 *
 * 查找优先级：后端数据 > 本地静态数据 > 第一个武将（兜底）
 * 这样即使后端数据不完整，前端也不会报错。
 */
function findGeneral(id: GeneralId) {
  return allGenerals.value.find((general) => general.id === id)
    || catalogGenerals.find((general) => general.id === id)
    || catalogGenerals[0]
}
</script>

<template>
  <!--
    =====================================================
    登录页 —— 未登录时显示
    =====================================================
    v-if="!isAuthenticated" 控制：token 为空时显示登录页
  -->
  <section v-if="!isAuthenticated" class="login-page">
    <!-- 左侧视觉区域 -->
    <div class="login-visual">
      <!-- 品牌标志 -->
      <div class="brand-block brand-block--login">
        <span class="brand-mark">将</span>
        <div>
          <strong>武将对战平台</strong>
          <small>SpringBoot · Vue3 · Element Plus</small>
        </div>
      </div>

      <!-- 欢迎文案 -->
      <div class="login-hero-copy">
        <el-tag effect="dark" type="danger">三国杀武将演示</el-tag>
        <h1>先登录，再进入武将广场</h1>
        <p>账号登录后会写入 JWT，并自动携带用户身份访问武将中心、秒杀、对战和 RAG 后台。</p>
      </div>

      <!-- 武将角色展示图 -->
      <div class="login-generals">
        <img :src="zhaoYun.avatar" :alt="zhaoYun.name" />
        <img :src="zhangFei.avatar" :alt="zhangFei.name" />
      </div>
    </div>

    <!-- 右侧登录卡片 -->
    <el-card class="login-card" shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <p class="eyebrow">账号登录</p>
            <h2>进入平台</h2>
          </div>
          <el-icon><Lock /></el-icon>
        </div>
      </template>

      <!-- 错误/提示信息 -->
      <el-alert
        v-if="apiMessage"
        :title="apiMessage"
        type="warning"
        show-icon
        :closable="false"
      />

      <!--
        登录表单
        label-position="top"：标签在输入框上方
        @keyup.enter：键盘回车也可触发登录
      -->
      <el-form class="login-form" label-position="top">
        <el-form-item label="手机号">
          <el-input
            v-model="loginForm.phone"
            size="large"
            autocomplete="username"
            placeholder="请输入手机号"
            :prefix-icon="Iphone"
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            size="large"
            type="password"
            autocomplete="current-password"
            placeholder="请输入密码"
            show-password
            :prefix-icon="Key"
            @keyup.enter="loginByPassword"
          />
        </el-form-item>

        <!-- loading 属性：登录中显示加载动画，防止重复提交 -->
        <el-button
          class="login-submit"
          type="primary"
          size="large"
          :loading="loginBusy"
          :icon="VideoPlay"
          @click="loginByPassword"
        >
          登录
        </el-button>
      </el-form>

      <el-divider>演示账号</el-divider>

      <!-- 演示账号快捷选择 -->
      <div class="demo-account-list">
        <el-button
          v-for="account in demoAccounts"
          :key="account.phone"
          plain
          class="demo-account"
          @click="fillDemoAccount(account)"
        >
          <span>{{ account.label }}</span>
          <small>{{ account.phone }} / {{ account.password }}</small>
          <em>{{ account.desc }}</em>
        </el-button>
      </div>
    </el-card>
  </section>

  <!--
    =====================================================
    主应用布局 —— 已登录时显示
    =====================================================
    左侧侧边栏 + 右侧内容区（经典后台管理布局）
  -->
  <el-container v-else class="app-shell">
    <!-- 左侧侧边栏：品牌 + 菜单 + 状态 + 用户面板 -->
    <el-aside width="286px" class="sidebar">
      <!-- 品牌标志 -->
      <div class="brand-block">
        <span class="brand-mark">将</span>
        <div>
          <strong>武将对战平台</strong>
          <small>SpringBoot · Vue3</small>
        </div>
      </div>

      <!--
        导航菜单
        :default-active="activeView" —— 高亮当前选中的菜单项
        @select="handleMenuSelect"  —— 菜单点击回调
        :disabled="item.adminOnly && identity.role !== '1'" —— 非管理员禁用 RAG 后台菜单
      -->
      <el-menu
        class="nav-menu"
        :default-active="activeView"
        background-color="transparent"
        text-color="rgba(255,255,255,.78)"
        active-text-color="#f4c45d"
        @select="handleMenuSelect"
      >
        <el-menu-item
          v-for="item in navItems"
          :key="item.key"
          :index="item.key"
          :disabled="item.adminOnly && identity.role !== '1'"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <template #title>
            <span>{{ item.label }}</span>
            <small>{{ item.meta }}</small>
          </template>
        </el-menu-item>
      </el-menu>

      <!-- 后端连接状态指示器 -->
      <el-alert
        class="api-status"
        :type="statusType"
        :title="statusMessage"
        :description="`联调地址：${apiBase}`"
        show-icon
        :closable="false"
      />

      <!-- 用户信息面板 -->
      <div class="user-panel">
        <!-- UserFilled 图标作为默认头像 -->
        <el-avatar :icon="UserFilled" />
        <div class="user-panel__meta">
          <strong>{{ userDisplayName }}</strong>
          <span>{{ userRoleLabel }} · ID {{ identity.userId }}</span>
        </div>
        <div class="user-panel__actions">
          <!-- 同步数据按钮 -->
          <el-tooltip content="同步数据" placement="top">
            <el-button circle :icon="Refresh" :loading="apiBusy" @click="refreshBackend" />
          </el-tooltip>
          <!-- 退出登录按钮 -->
          <el-tooltip content="退出登录" placement="top">
            <el-button circle type="danger" :icon="SwitchButton" @click="logout" />
          </el-tooltip>
        </div>
      </div>
    </el-aside>

    <!--
      右侧主内容区

      v-loading：Element Plus 的 loading 指令，在 apiBusy 为 true 时显示遮罩
      对战页单独处理 loading（battleBusy），所以这里排除 battle 视图
    -->
    <el-main class="main-stage" v-loading="apiBusy && activeView !== 'battle'">
      <!--
        视图切换 —— 使用 v-if / v-else-if 链
        这种方式比 vue-router 简单，适合页面较少的应用
        每个视图组件通过 props 接收数据，通过 emit 向上传递事件
      -->

      <!-- 武将广场页 -->
      <PlazaView
        v-if="activeView === 'plaza'"
        :generals="allGenerals"
        :owned-ids="state.ownedGeneralIds"
        @detail="openDetail"
        @battle="openBattle"
        @seckill="openSeckill"
      />

      <!-- 武将中心页（我的武将） -->
      <CenterView
        v-else-if="activeView === 'center'"
        :owned-generals="ownedGenerals"
        @detail="openDetail"
        @battle="openBattle"
        @seckill="openSeckill"
      />

      <!-- 武将详情页 -->
      <DetailView
        v-else-if="activeView === 'detail'"
        :general="selectedGeneral"
        :owned="state.ownedGeneralIds.includes(selectedGeneral.id)"
        :rag-documents="selectedRagDocuments"
        @battle="openBattle"
        @seckill="openSeckill"
      />

      <!-- 秒杀页 -->
      <SeckillView
        v-else-if="activeView === 'seckill'"
        :general="zhaoYun"
        :seckill="state.seckill"
        :last-result="lastSeckillResult"
        :owned="state.ownedGeneralIds.includes('zhao-yun')"
        @claim="claimZhaoYun"
      />

      <!-- 对战页 -->
      <BattleView
        v-else-if="activeView === 'battle'"
        :player-general="zhangFei"
        :enemy-general="battleEnemyGeneral"
        :battle="battleState"
        :loading="battleBusy"
        @create="startBattle"
        @play-card="playCard"
        @respond-card="respondCard"
        @pass-response="passResponse"
        @end-turn="endTurn"
      />

      <!-- RAG 后台管理页（仅管理员） -->
      <RagAdminView
        v-else-if="activeView === 'rag'"
        :generals="allGenerals"
        :documents="state.ragDocuments"
        @add="createRagDocument"
      />

      <!-- 系统架构蓝图页 -->
      <ArchitectureView v-else />
    </el-main>
  </el-container>
</template>
