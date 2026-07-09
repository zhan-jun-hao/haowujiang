/**
 * api.ts — 后端 API 服务层
 *
 * 本文件封装了所有与后端通信的逻辑，包括：
 * 1. HTTP 请求封装（fetch + 统一错误处理）
 * 2. 认证管理（JWT token 的存储、读取、清除）
 * 3. 业务接口（登录、武将列表、秒杀、对战、RAG 知识库等）
 *
 * 架构设计思路：
 * - 所有 API 函数返回已解析好的业务数据（而不是 Response 对象），
 *   调用方不需要关心 HTTP 状态码和 JSON 解析细节
 * - 通过 getStoredIdentity() 从 localStorage 读取身份信息，
 *   统一附加到每个请求头中（token + userId + role）
 * - 请求失败时抛出 Error，由上层 App.vue 的 runApi() 统一捕获和处理
 */

import { generals as catalogGenerals, ragSeeds } from '../data/catalog'
import type {
  ApiIdentity,
  BattleState,
  General,
  GeneralId,
  LoginResult,
  RagDocument,
  SeckillResult,
  SeckillState,
  UserBasicVo
} from '../types'

// ---------- 后端数据结构类型 ----------

/**
 * 后端统一响应格式
 *
 * 所有后端接口都返回这个结构：
 * { code: 200, msg: "success", data: {...} }
 *
 * 泛型 T 是 data 字段的具体类型
 */
interface Result<T> {
  code: number
  msg: string
  data: T
}

/** 后端返回的武将基本信息 VO（Value Object） */
interface GeneralBasicVo {
  id: number
  code: GeneralId
  name: string
  title: string
  camp: string
  hp: number
  rarity: string
  skillName: string
  skillSummary: string
  unlockSource: number | string
}

/** 后端返回的武将详情 */
interface GeneralDetailVo {
  basicVo: GeneralBasicVo
}

/** 后端返回的"我的武将" */
interface MyGeneralRespVo {
  generalBasicVo: GeneralBasicVo
}

/** 后端返回的 RAG 文档 */
interface RagDocumentBasicVo {
  id: number | string
  generalCode: GeneralId
  title: string
  content: string
  updateTime: string
}

// ---------- 常量配置 ----------

/**
 * API 基础地址
 *
 * 优先级：环境变量 VITE_API_BASE_URL > 默认值 '/api'
 * Vite 中环境变量通过 import.meta.env 访问，变量名必须以 VITE_ 开头
 */
const API_BASE = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

/**
 * localStorage 键名
 *
 * 用于持久化存储用户认证信息，浏览器关闭后依然保留。
 * 前缀使用项目名，避免与其他项目冲突。
 */
const TOKEN_KEY = 'haowujiang-token'        // JWT token
const USER_ID_KEY = 'haowujiang-dev-user-id'  // 用户 ID
const ROLE_KEY = 'haowujiang-dev-role'       // 用户角色

// ---------- 身份管理 ----------

/**
 * 从 localStorage 读取已保存的身份信息
 *
 * 为什么用 localStorage 而不是 sessionStorage？
 * - localStorage 在浏览器关闭后仍然保留，用户体验更好（不需要每次重新登录）
 * - 在开发阶段，默认 userId='1', role='1'（管理员），方便调试
 */
export function getStoredIdentity(): ApiIdentity {
  return {
    token: localStorage.getItem(TOKEN_KEY) || '',
    userId: localStorage.getItem(USER_ID_KEY) || '1',
    role: localStorage.getItem(ROLE_KEY) || '1'
  }
}

/** 保存身份信息到 localStorage —— 登录成功后调用 */
export function saveIdentity(identity: ApiIdentity) {
  localStorage.setItem(TOKEN_KEY, identity.token)
  localStorage.setItem(USER_ID_KEY, identity.userId)
  localStorage.setItem(ROLE_KEY, identity.role)
}

/** 清除身份信息 —— 退出登录时调用（只清除 token，保留 userId 和 role 方便调试） */
export function clearIdentity() {
  localStorage.removeItem(TOKEN_KEY)
}

/** 获取 API 基础地址 —— 用于在界面上展示当前联调的后端地址 */
export function getApiBase() {
  return API_BASE
}

// ---------- 业务 API ----------

/**
 * 登录接口
 *
 * @param phone   手机号
 * @param password  密码
 * @returns 登录结果（包含 JWT token 和用户信息）
 *
 * skipAuth: true —— 登录接口不需要携带 token（因为此时还没有 token）
 */
export async function login(phone: string, password: string) {
  return request<LoginResult>('/auth/login', {
    method: 'POST',
    body: { phone, password },
    skipAuth: true
  })
}

/** 获取所有武将列表 —— 广场页展示用 */
export async function listGenerals() {
  const rows = await request<GeneralBasicVo[]>('/client/generals', { method: 'GET' })
  return rows.map(toGeneral)  // 将后端 VO 转换为前端统一的 General 格式
}

/** 获取单个武将详情 */
export async function getGeneralInfo(id: number) {
  const detail = await request<GeneralDetailVo>(`/client/generals/${id}`, { method: 'GET' })
  return toGeneral(detail.basicVo)
}

/** 获取当前用户已拥有的武将列表 */
export async function listOwnedGenerals() {
  const rows = await request<MyGeneralRespVo[]>('/client/users/me/generals', { method: 'GET' })
  return rows.map((item) => toGeneral(item.generalBasicVo))
}

/** 获取秒杀实时状态（剩余库存、订单列表、事件日志等） */
export async function getSeckillState(generalCode: GeneralId) {
  const state = await request<SeckillState>(`/client/seckill/generals/${generalCode}/state`, { method: 'GET' })
  return normalizeSeckillState(state)  // 容错处理：确保数组字段不为 undefined
}

/**
 * 执行秒杀 —— 核心接口
 *
 * 后端秒杀流程：
 * 1. Sentinel 限流检查（QPS 控制）
 * 2. Redis Lua 脚本原子操作：检查库存 + 检查一人一单 + 扣库存
 * 3. Kafka 发送下单事件（异步）
 * 4. 返回秒杀结果
 */
export async function claimSeckill(generalCode: GeneralId) {
  const result = await request<SeckillResult>(`/client/seckill/generals/${generalCode}`, { method: 'POST' })
  if (result.state) {
    result.state = normalizeSeckillState(result.state)
  }
  return result
}

/** 获取某个武将的 RAG 知识库文档列表 */
export async function listRagDocuments(generalCode: GeneralId) {
  const rows = await request<RagDocumentBasicVo[]>(`/admin/rag/generals/${generalCode}/documents`, { method: 'GET' })
  return rows.map(toRagDocument)
}

/** 添加 RAG 知识文档（仅管理员可用） */
export async function addRagDocument(payload: Pick<RagDocument, 'generalId' | 'title' | 'content'>) {
  const row = await request<RagDocumentBasicVo>(`/admin/rag/generals/${payload.generalId}/documents`, {
    method: 'POST',
    body: {
      title: payload.title,
      content: payload.content
    }
  })
  return toRagDocument(row)
}

/** 获取所有用户列表（管理员功能） */
export async function listUsers() {
  return request<UserBasicVo[]>('/admin/users', { method: 'GET' })
}

/**
 * 创建对战
 *
 * @param playerGeneralId  玩家武将的后端 ID
 * @param enemyGeneralId   敌方武将的后端 ID
 * @returns 对战初始状态
 */
export async function createBattle(playerGeneralId: number, enemyGeneralId: number) {
  return request<BattleState>('/client/battles', {
    method: 'POST',
    body: {
      playerGeneralId,
      enemyGeneralId
    }
  })
}

/** 出牌 —— 在玩家回合中使用手牌 */
export async function playBattleCard(battleId: string, cardId: string) {
  return request<BattleState>(`/client/battles/${battleId}/actions`, {
    method: 'POST',
    body: {
      action: 'PLAY_CARD',
      cardId
    }
  })
}

/** 响应 —— 当敌方出杀时，玩家选择出闪抵消 */
export async function respondBattleCard(battleId: string, cardId: string) {
  return request<BattleState>(`/client/battles/${battleId}/actions`, {
    method: 'POST',
    body: {
      action: 'RESPOND_CARD',
      cardId
    }
  })
}

/** 放弃响应 —— 当敌方出杀时，玩家选择不出闪（受到伤害） */
export async function passBattleResponse(battleId: string) {
  return request<BattleState>(`/client/battles/${battleId}/actions`, {
    method: 'POST',
    body: {
      action: 'PASS_RESPONSE'
    }
  })
}

/** 结束回合 —— 玩家结束当前回合，轮到敌方行动 */
export async function endBattleTurn(battleId: string) {
  return request<BattleState>(`/client/battles/${battleId}/actions`, {
    method: 'POST',
    body: {
      action: 'END_TURN'
    }
  })
}

// ---------- 底层请求封装 ----------

type RequestOptions = {
  method: 'GET' | 'POST'
  body?: unknown
  skipAuth?: boolean  // 是否跳过认证（登录接口不需要携带 token）
}

/**
 * 通用请求函数 —— 所有 API 调用的底层实现
 *
 * 功能：
 * 1. 自动附加认证头（token + userId + role）
 * 2. 统一错误处理（非 200 或 code 非 200 都抛出异常）
 * 3. 自动解析 JSON 响应
 *
 * 泛型 T 是期望的业务数据类型
 *
 * @param path    接口路径（如 '/client/generals'）
 * @param options 请求配置（方法、请求体、是否跳过认证）
 * @returns 解析后的业务数据
 * @throws 当接口返回非正常状态时抛出 Error
 */
async function request<T>(path: string, options: RequestOptions): Promise<T> {
  // 从 localStorage 读取用户身份信息
  const identity = getStoredIdentity()

  // 构建请求头
  const headers = new Headers()
  headers.set('Content-Type', 'application/json')
  if (!options.skipAuth) {
    // 携带 JWT token（Bearer 认证方式）
    if (identity.token) {
      headers.set('Authorization', `Bearer ${identity.token}`)
    }
    // 同时通过自定义头传递用户 ID 和角色（方便后端快速获取）
    headers.set('X-User-Id', identity.userId)
    headers.set('X-User-Role', identity.role)
  }

  // 发起 HTTP 请求
  const response = await fetch(`${API_BASE}${path}`, {
    method: options.method,
    headers,
    credentials: 'include',  // 跨域请求时携带 cookie
    body: options.body == null ? undefined : JSON.stringify(options.body)
  })

  // 解析响应 JSON（解析失败时返回 null）
  const payload = await response.json().catch(() => null) as Result<T> | null

  // 检查响应是否成功：HTTP 状态码必须 ok，且业务 code 必须为 200
  if (!response.ok || !payload || payload.code !== 200) {
    throw new Error(payload?.msg || `接口请求失败：${response.status}`)
  }

  return payload.data
}

// ---------- 数据转换函数 ----------

/**
 * 将后端武将 VO 转换为前端 General 格式
 *
 * 为什么要转换？
 * - 后端返回的字段名和前端类型不完全一致
 * - 有部分前端展示需要的数据（如 avatar、tags、skill.rules）后端不返回，
 *   需要从本地静态数据（catalogGenerals）中合并
 *
 * @param vo 后端返回的武将基本信息
 * @returns 前端统一格式的 General 对象
 */
function toGeneral(vo: GeneralBasicVo): General {
  // 先从本地静态数据中查找对应武将（获取 avatar、tags、rules 等前端专有字段）
  const fallback = catalogGenerals.find((item) => item.id === vo.code) || catalogGenerals[0]
  return {
    ...fallback,          // 先展开本地数据（包含 avatar、tags、skill.rules 等）
    id: vo.code,          // 覆盖为后端返回的数据
    code: vo.code,
    backendId: vo.id,
    name: vo.name,
    title: vo.title,
    camp: vo.camp,
    hp: vo.hp,
    rarity: vo.rarity,
    unlockSource: normalizeUnlockSource(vo.unlockSource),
    skill: {
      ...fallback.skill,           // 保留本地技能规则
      name: vo.skillName,          // 覆盖为后端技能名
      summary: vo.skillSummary     // 覆盖为后端技能简介
    }
  }
}

/** 将后端 RAG 文档 VO 转换为前端 RagDocument 格式 */
function toRagDocument(vo: RagDocumentBasicVo): RagDocument {
  return {
    id: String(vo.id),
    generalId: vo.generalCode,
    generalCode: vo.generalCode,
    title: vo.title,
    content: vo.content,
    updatedAt: vo.updateTime
  }
}

/**
 * 容错处理：确保秒杀状态的数组字段不为 undefined
 *
 * 后端在某些情况下可能不返回这些字段，
 * 前端统一设为空数组，避免在模板中使用 v-for 时报错
 */
function normalizeSeckillState(state: SeckillState): SeckillState {
  return {
    ...state,
    targetGeneralId: state.targetGeneralId || 'zhao-yun',
    claimedUserIds: state.claimedUserIds || [],
    orders: state.orders || [],
    events: state.events || []
  }
}

/**
 * 统一解锁来源的格式
 *
 * 后端可能返回数字（0/1）或字符串（'DEFAULT'/'SECKILL'），
 * 统一转换为前端使用的格式（'default'/'seckill'）
 */
function normalizeUnlockSource(value: number | string) {
  if (value === 0 || value === '0' || value === 'DEFAULT') {
    return 'default'
  }
  if (value === 1 || value === '1' || value === 'SECKILL') {
    return 'seckill'
  }
  return value
}

/**
 * 本地降级数据 —— 当后端不可用时使用的默认 RAG 文档
 *
 * 这些文档与 data/catalog.ts 中的 ragSeeds 保持同步，
 * 确保即使后端未启动，前端也能展示示例知识库内容。
 */
export function fallbackRagDocuments() {
  return ragSeeds.map((item) => ({ ...item }))
}
