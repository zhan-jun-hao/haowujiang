/**
 * catalog.ts — 前端静态数据目录
 *
 * 本文件定义了应用的前端静态数据，包括：
 * - 武将基础信息（赵云、张飞）
 * - 卡牌元数据（杀、闪、桃、酒）
 * - RAG 知识库种子数据
 * - 后端技术栈架构蓝图
 *
 * 为什么需要静态数据？
 * 1. 前端独立开发：后端还没写好时，前端可以用这些数据开发界面
 * 2. 降级方案：后端不可用时，前端展示静态数据而不是空白页
 * 3. 数据合并：后端返回的武将数据只有基础字段，详细信息（avatar、tags、rules）
 *    从本文件合并，避免后端维护冗余数据
 */

import type { CardMeta, General, RagDocument } from '../types'

// 导入武将头像（Vite 会处理图片资源的路径和打包）
import zhaoYunAvatar from '../assets/zhao-yun-classic.png'
import zhangFeiAvatar from '../assets/zhang-fei-classic.png'

// ============================================================
// 武将数据
// ============================================================

/**
 * 武将静态数据
 *
 * 每个武将对象包含：
 * - 基础信息：名称、称号、体力、阵营、稀有度
 * - 技能：技能名 + 简介 + 详细规则列表
 * - API 端点：该武将关联的后端接口（展示用，非功能性）
 */
export const generals: General[] = [
  {
    id: 'zhao-yun',         // 前端唯一标识（用于路由、组件 key、接口参数）
    backendId: 1,           // 后端数据库主键 ID
    code: 'zhao-yun',       // 编码（与 id 一致）
    name: '赵云',
    title: '常山赵子龙',
    hp: 4,                  // 体力上限为 4
    rarity: '秒杀限定',      // 获取方式标签
    avatar: zhaoYunAvatar,
    camp: '蜀',
    role: '大模型托管武将',   // 赵云由 AI 大模型驱动（非玩家操作）
    tags: ['杀闪转换', 'RAG记忆', 'AI对战'],
    unlockSource: 'seckill',  // 需要通过秒杀获得
    skill: {
      name: '龙胆',
      summary: '杀当闪，闪当杀。',  // 一句话概括技能效果
      rules: [
        '受到杀时，可以将手牌中的杀作为闪使用。',
        '出杀阶段，可以将手牌中的闪作为杀使用。',
        '大模型决策会优先读取赵云专属 RAG 记忆，再决定是否转换牌。'
      ]
    },
    apiHooks: [
      'GET /api/client/generals/1',
      'POST /api/client/battles/{battleId}/ai-action',
      'POST /api/admin/rag/generals/zhao-yun/documents'
    ]
  },
  {
    id: 'zhang-fei',
    backendId: 2,
    code: 'zhang-fei',
    name: '张飞',
    title: '燕人张翼德',
    hp: 4,
    rarity: '默认拥有',      // 登录即拥有，不需要额外获取
    avatar: zhangFeiAvatar,
    camp: '蜀',
    role: '玩家默认武将',     // 张飞由玩家手动操作
    tags: ['无限出杀', '默认拥有', '爆发'],
    unlockSource: 'default',  // 默认拥有
    skill: {
      name: '咆哮',
      summary: '出杀次数不受每回合一次限制。',
      rules: [
        '基础规则为每方每回合只能出一张杀。',
        '张飞技能覆盖基础限制，可以在同一回合连续使用杀。',
        '前端会把技能结果传给后端战斗引擎校验。'
      ]
    },
    apiHooks: [
      'GET /api/client/users/me/generals',
      'POST /api/client/battles',
      'POST /api/client/battles/{battleId}/actions'
    ]
  }
]

// ============================================================
// 卡牌元数据
// ============================================================

/**
 * 卡牌元数据
 *
 * 描述三国杀的四张基础牌，用于：
 * - 在对战界面渲染手牌的颜色和样式
 * - 展示卡牌规则说明
 *
 * 每张卡的 color 通过 CSS 自定义属性（--card-color）注入到手牌按钮中
 */
export const cardMetas: CardMeta[] = [
  {
    kind: 'sha',
    name: '杀',
    color: '#bb2f2a',  // 红色系 —— 攻击牌
    shortRule: '对目标造成 1 点伤害，可被闪抵消。'
  },
  {
    kind: 'shan',
    name: '闪',
    color: '#2f7d68',  // 绿色系 —— 防御牌
    shortRule: '抵消一次杀；赵云可将其转化为杀。'
  },
  {
    kind: 'tao',
    name: '桃',
    color: '#c25b78',  // 粉色系 —— 恢复牌
    shortRule: '回复 1 点体力，不超过上限。'
  },
  {
    kind: 'jiu',
    name: '酒',
    color: '#c0822d',  // 橙色系 —— 辅助牌
    shortRule: '本回合下一张杀伤害 +1。'
  }
]

// ============================================================
// RAG 知识库种子数据
// ============================================================

/**
 * RAG 知识库种子数据
 *
 * 这些是预设的 AI 知识文档，用于指导赵云（大模型）在对战中的出牌策略。
 *
 * RAG 工作原理：
 * 1. 轮到赵云出牌时，后端从知识库检索相关文档
 * 2. 将检索到的文档内容注入到大模型的提示词（Prompt）中
 * 3. 大模型根据提示词中的策略指导做出出牌决策
 * 4. 这样赵云的行为就符合预设的策略偏好，而不是随机出牌
 */
export const ragSeeds: RagDocument[] = [
  {
    id: 'rag-zhao-yun-1',
    generalId: 'zhao-yun',
    title: '龙胆出牌偏好',
    content: '赵云在手牌同时存在杀和闪时，若血量低于 2，优先保留一张可防御的牌；若目标无闪概率高，则可将闪当杀压低血线。',
    updatedAt: '2026-07-01 16:30'
  },
  {
    id: 'rag-zhao-yun-2',
    generalId: 'zhao-yun',
    title: '酒杀策略',
    content: '赵云使用酒后应尽快出杀，避免回合结束浪费增伤；当手牌只有闪时，可借助龙胆视为杀。',
    updatedAt: '2026-07-01 16:31'
  },
  {
    id: 'rag-zhang-fei-1',
    generalId: 'zhang-fei',
    title: '咆哮压制',
    content: '张飞拥有多张杀时，应优先连续进攻，迫使对手消耗闪；若自身血量偏低，先使用桃再继续输出。',
    updatedAt: '2026-07-01 16:32'
  }
]

// ============================================================
// 后端技术栈架构蓝图
// ============================================================

/**
 * 后端技术栈架构蓝图
 *
 * 这些数据在 ArchitectureView（系统蓝图页）中展示，
 * 描述后端各个技术组件的职责和边界。
 *
 * 架构概览：
 * - SpringBoot：主业务逻辑
 * - Redis + Lua：秒杀库存原子操作
 * - Kafka：异步下单，削峰填谷
 * - LangChain4j + RAG：AI 对战决策
 * - Sentinel：接口限流与降级
 */
export const backendBlueprint = [
  {
    title: 'SpringBoot API',
    body: '承接武将、用户背包、战斗房间、RAG 管理与秒杀下单接口。'
  },
  {
    title: 'Redis + Lua',
    body: '秒杀入口先通过 Lua 原子判断库存与一人一单，成功后写入 Kafka 事件。'
  },
  {
    title: 'Kafka',
    body: '异步创建武将领取订单，削峰并隔离用户请求和 MySQL 写入压力。'
  },
  {
    title: 'LangChain4j + RAG',
    body: '战斗 AI 调用大模型前检索武将专属知识库，把技能、偏好和历史对局记忆注入提示词。'
  },
  {
    title: 'Sentinel',
    body: '适合用于秒杀接口 QPS 限流、热点参数限流和降级兜底；不适合替代 Redis Lua 的库存一致性。'
  }
]
