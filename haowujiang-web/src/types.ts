/**
 * types.ts — 全局类型定义
 *
 * 本文件定义了整个前端应用中使用的所有 TypeScript 类型和接口。
 * 包括：武将、卡牌、秒杀、对战、RAG 知识库、用户认证等领域的类型。
 *
 * 为什么要定义类型？
 * 1. TypeScript 在编译阶段就能发现拼写错误和类型不匹配，减少运行时 bug
 * 2. IDE 可以提供智能提示（自动补全、参数提示），大幅提升开发效率
 * 3. 接口定义本身就是最好的文档，团队成员一看就知道数据结构长什么样
 */

/** 视图标识 —— 对应左侧导航菜单的每个页面 */
export type ViewKey = 'plaza' | 'center' | 'detail' | 'seckill' | 'battle' | 'rag' | 'architecture'

/** 武将唯一标识 —— 目前只有赵云和张飞两个武将 */
export type GeneralId = 'zhao-yun' | 'zhang-fei'

/** 卡牌类型 —— 对应三国杀基础牌：杀、闪、桃、酒 */
export type CardKind = 'sha' | 'shan' | 'tao' | 'jiu'

/** 武将技能 —— 包含技能名称、简介和详细规则列表 */
export interface GeneralSkill {
  name: string        // 技能名称，如"龙胆"、"咆哮"
  summary: string     // 技能一句话简介
  rules: string[]     // 技能详细规则，每条一项
}

/**
 * 武将完整信息
 *
 * 这个接口同时用于：
 * - 前端本地数据（data/catalog.ts 中的静态武将定义）
 * - 后端接口返回的武将数据（通过 toGeneral() 转换后统一格式）
 * - 跨组件传递武将信息（props）
 */
export interface General {
  id: GeneralId           // 前端使用的武将 ID（如 'zhao-yun'）
  backendId: number       // 后端数据库中的武将 ID（数字）
  code: GeneralId         // 武将编码，与 id 一致，用于后端接口参数
  name: string            // 武将名称
  title: string           // 武将称号（如"常山赵子龙"）
  hp: number              // 体力值上限
  rarity: string          // 稀有度标签（如"秒杀限定"、"默认拥有"）
  avatar: string          // 武将头像图片路径
  camp: string            // 所属阵营（蜀、魏、吴、群）
  tags: string[]          // 标签列表（如 ['杀闪转换', 'RAG记忆', 'AI对战']）
  role: string            // 角色定位描述
  skill: GeneralSkill     // 武将技能详情
  unlockSource: 'default' | 'seckill' | number | string  // 获取方式：默认拥有 / 秒杀获得 / 其他
  apiHooks: string[]      // 关联的后端 API 端点列表（展示用）
}

/** 卡牌元数据 —— 描述每种卡牌的基础属性 */
export interface CardMeta {
  kind: CardKind     // 卡牌类型
  name: string       // 卡牌中文名
  color: string      // 卡牌主题色（CSS 颜色值）
  shortRule: string  // 卡牌规则简述
}

/**
 * RAG 知识库文档
 *
 * RAG（Retrieval-Augmented Generation，检索增强生成）：
 * 在对战中，AI 武将（赵云）出牌前会先从知识库中检索相关文档，
 * 将检索到的内容注入到大模型的提示词中，让 AI 做出更符合武将设定的决策。
 */
export interface RagDocument {
  id: string              // 文档唯一 ID
  generalId: GeneralId    // 关联的武将 ID
  generalCode?: GeneralId // 武将编码（与 generalId 相同，后端返回时使用此字段）
  title: string           // 知识标题
  content: string         // 知识内容（会被注入到 AI 提示词中）
  updatedAt: string       // 最后更新时间
}

/** 秒杀订单 —— 记录一次秒杀成功后的订单信息 */
export interface SeckillOrder {
  id: string                  // 订单号
  userId: number              // 用户 ID
  generalId: GeneralId | string  // 秒杀获得的武将 ID
  status: string              // 订单状态
  createdAt: string           // 创建时间
}

/** 秒杀事件日志 —— 记录秒杀过程中的每一步事件 */
export interface SeckillEvent {
  id: string                 // 事件 ID
  level: 'info' | 'success' | 'warn'  // 事件级别（用于不同颜色显示）
  message: string            // 事件描述
  createdAt: string          // 事件时间
}

/**
 * 秒杀状态 —— 实时反映秒杀活动的当前状态
 *
 * 秒杀的核心流程：
 * 1. 用户点击抢购 → 2. Sentinel 限流检查 → 3. Redis Lua 原子扣库存
 * → 4. Kafka 异步下单 → 5. MySQL 写入订单
 */
export interface SeckillState {
  targetGeneralId: GeneralId   // 秒杀目标武将
  stock: number                // 当前剩余库存
  total: number                // 总库存
  claimedUserIds: number[]     // 已成功秒杀的用户 ID 列表（用于防重复）
  orders: SeckillOrder[]       // 订单列表
  events: SeckillEvent[]       // 事件日志列表
}

/**
 * 产品全局状态 —— 聚合用户的核心数据
 *
 * 这个状态贯穿整个应用，通过 reactive() 实现响应式，
 * 任何修改都会自动更新所有使用到这些数据的组件。
 */
export interface ProductState {
  ownedGeneralIds: GeneralId[]   // 用户已拥有的武将 ID 列表
  seckill: SeckillState         // 秒杀状态
  ragDocuments: RagDocument[]   // RAG 知识库文档列表
}

/**
 * 秒杀结果 —— 一次秒杀请求的返回结果
 *
 * 包含是否成功、提示信息、执行步骤等，
 * 前端根据这些信息展示不同的结果页面。
 */
export interface SeckillResult {
  ok: boolean        // 是否秒杀成功
  title: string      // 结果标题
  message: string    // 结果描述
  steps: string[]    // 执行步骤列表（如 ["限流通过", "库存扣减成功", "订单已创建"]）
  state?: SeckillState  // 更新后的秒杀状态
}

/** 对战中的卡牌实例 —— 牌堆中的每张具体牌 */
export interface BattleCard {
  id: string     // 卡牌实例 ID（唯一，同一类型的牌每张 ID 不同）
  kind: CardKind // 卡牌类型
  name: string   // 卡牌名称
  suit: string   // 花色（♠ ♥ ♣ ♦）
  point: string  // 点数（A, 2-10, J, Q, K）
}

/**
 * 对战中的人物（玩家或 AI）
 *
 * 每场对战有两个 Actor：玩家侧（张飞）和敌方侧（赵云/AI）
 */
export interface BattleActor {
  side: 'player' | 'enemy'  // 阵营：玩家还是敌方
  generalId: GeneralId      // 使用的武将 ID
  name: string              // 显示名称
  skillName?: string        // 技能名称
  hp: number                // 当前体力值
  maxHp: number             // 体力上限
  hand: BattleCard[]        // 手牌列表
  handCount?: number        // 手牌数量（后端可能只返回数量不返回详情）
  drunk: boolean            // 是否处于"饮酒"状态（酒的效果：下一张杀伤害+1）
  shaUsed: number           // 本回合已使用杀的数量
  defeated: boolean         // 是否已阵亡
}

/** 对战日志 —— 记录对战中的每一步操作 */
export interface BattleLog {
  id: string                           // 日志 ID
  type: 'system' | 'player' | 'enemy' | 'ai' | 'damage' | 'heal'  // 日志类型
  text: string                         // 日志内容
}

/**
 * 待响应状态 —— 当敌方出杀时，系统等待玩家出闪响应
 *
 * 这是对战中的关键交互节点：
 * A 对 B 出杀 → B 需要选择出闪（抵消）或不出闪（受到伤害）
 */
export interface BattlePendingResponse {
  type: 'SHA'              // 响应类型：杀
  attackerSide: 'player' | 'enemy'  // 攻击方
  defenderSide: 'player' | 'enemy'  // 防御方
  attackerName: string     // 攻击者名称
  defenderName: string     // 防御者名称
  attackCardId: string     // 攻击用的卡牌 ID
  attackCardName: string   // 攻击卡牌名称
  attackCardSuit: string   // 攻击卡牌花色
  attackCardPoint: string  // 攻击卡牌点数
  damage: number           // 将造成的伤害
}

/**
 * 对战完整状态
 *
 * 对战的核心规则（三国杀简化版）：
 * - 每回合每个玩家默认只能出一张杀
 * - 张飞的"咆哮"技能可以突破这个限制
 * - 赵云的"龙胆"技能可以将杀当闪、闪当杀使用
 * - AI 决策由大模型 + RAG 知识库驱动
 */
export interface BattleState {
  id?: string                      // 对战 ID（后端创建后分配）
  round: number                    // 当前回合数
  current: 'player' | 'enemy'      // 当前行动方
  player: BattleActor              // 玩家状态
  enemy: BattleActor               // 敌方状态
  deck: BattleCard[]               // 牌堆（剩余未抽的牌）
  discard: BattleCard[]            // 弃牌堆
  deckCount?: number               // 牌堆数量
  discardCount?: number            // 弃牌堆数量
  logs: BattleLog[]                // 战斗日志
  aiThoughts: string[]             // AI 思考过程（展示大模型的决策链）
  pendingResponse?: BattlePendingResponse | null  // 待响应状态（有人出杀等待响应时不为空）
  winner: 'player' | 'enemy' | null  // 胜者（null 表示对战进行中）
}

/** 用户基本信息 —— 后端返回的用户数据 */
export interface UserBasicVo {
  id: number       // 用户 ID
  phone: string    // 手机号
  nickname: string // 昵称
  role: number     // 角色：1=管理员, 0=普通用户
  status: number   // 账号状态
}

/**
 * 登录结果
 *
 * 登录成功后后端返回 JWT token 和用户信息，
 * 前端将 token 存入 localStorage，后续请求携带 token 进行身份认证。
 */
export interface LoginResult {
  token: string            // JWT 令牌（后续请求放在 Authorization 头中）
  userBasicVo: UserBasicVo // 用户基本信息
}

/**
 * API 身份标识
 *
 * 存储在 localStorage 中的用户身份信息，
 * 每次发起 API 请求时从本地读取并附加到请求头中。
 */
export interface ApiIdentity {
  token: string   // JWT 令牌
  userId: string  // 用户 ID
  role: string    // 用户角色
}
