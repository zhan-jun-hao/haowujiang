<!--
  BattleView.vue — 对战页面

  三国杀轻量对战的完整交互界面，包含：
  - 对战双方信息展示（头像、名称、体力、手牌数）
  - 牌堆 / 弃牌堆计数
  - 当前回合指示
  - 手牌区域（可点击出牌）
  - 响应区域（被攻击时选择出闪或不出闪）
  - AI 思考过程展示（赵云的决策链）
  - 战斗日志

  对战流程：
  1. 用户选择敌方武将，点击"开始"
  2. 后端创建对战，洗牌，发初始手牌
  3. 玩家回合：选择手牌出牌 → 点击"结束回合"
  4. AI 回合：大模型决策出牌（前端等待后端返回）
  5. 当有人出杀时，对方需要响应（出闪或不出闪）
  6. 一方体力归零 → 对战结束

  Vue 3 交互模式：
  - 按钮点击 → emit 事件到父组件 → 父组件调用 API → 更新 battleState → 视图自动刷新
-->
<script setup lang="ts">
import { computed } from 'vue'
import { Refresh, VideoPlay } from '@element-plus/icons-vue'
import { cardMetas } from '../data/catalog'
import type { BattleActor, BattleCard, BattleState, General } from '../types'

const props = defineProps<{
  playerGeneral: General         // 玩家武将信息
  enemyGeneral: General          // 敌方武将信息
  battle: BattleState | null     // 对战完整状态（null=未开始）
  loading: boolean              // 是否正在与后端通信
}>()

// 对战相关事件
const emit = defineEmits<{
  create: []                   // 创建对战
  playCard: [cardId: string]  // 出牌
  respondCard: [cardId: string]  // 出闪响应
  passResponse: []             // 不出闪
  endTurn: []                  // 结束回合
}>()

/**
 * 卡牌元数据映射表
 *
 * 将 cardMetas 数组转为 { sha: {...}, shan: {...}, ... } 格式，
 * 方便通过 card.kind 快速查找卡牌的颜色和规则。
 */
const cardMetaMap = computed(() => Object.fromEntries(cardMetas.map((card) => [card.kind, card])))

/** 对战状态快捷访问 */
const state = computed(() => props.battle)

/** 当前是否有待响应的攻击（敌方出杀等待玩家响应） */
const pendingResponse = computed(() => state.value?.pendingResponse ?? null)

/**
 * 玩家是否需要响应杀
 *
 * 条件：
 * 1. 防御方是玩家
 * 2. 游戏还没结束
 * 3. 不在 loading 状态
 */
const playerNeedsResponse = computed(() => (
  pendingResponse.value?.defenderSide === 'player'
  && !state.value?.winner
  && !props.loading
))

/**
 * 玩家是否可以主动出牌
 *
 * 条件：
 * 1. 当前是玩家回合
 * 2. 游戏还没结束
 * 3. 不在 loading 状态
 * 4. 没有待响应的攻击（先响应再出牌）
 */
const playerCanAct = computed(() => (
  state.value?.current === 'player'
  && !state.value.winner
  && !props.loading
  && !playerNeedsResponse.value
))

/** 玩家当前手牌 */
const playerHand = computed(() => state.value?.player.hand || [])

/**
 * 可用于响应的手牌
 *
 * 什么牌可以用来响应杀？
 * - 闪：标准防御牌
 * - 杀（赵云龙胆技能）：赵云可以将杀当闪使用
 */
const responseCards = computed(() => playerHand.value.filter(canRespondWithCard))

/** 牌堆剩余数量 */
const deckCount = computed(() => state.value?.deckCount ?? state.value?.deck?.length ?? 0)

/** 弃牌堆数量 */
const discardCount = computed(() => state.value?.discardCount ?? state.value?.discard?.length ?? 0)

/**
 * 回合状态文本
 *
 * 根据对战的不同阶段显示不同的文字：
 * - 未开始 → "等待开战"
 * - 已结束 → "XX 获胜"
 * - 需要响应 → "请响应杀"
 * - 玩家回合 → "你的回合"
 * - 敌方回合 → "XX 回合"
 */
const turnText = computed(() => {
  if (!state.value) {
    return '等待开战'
  }
  if (state.value.winner) {
    return `${state.value.winner === 'player' ? '你' : props.enemyGeneral.name} 获胜`
  }
  if (playerNeedsResponse.value) {
    return '请响应杀'
  }
  return state.value.current === 'player' ? '你的回合' : `${props.enemyGeneral.name} 回合`
})

/**
 * 生成体力格子数组
 *
 * 例如 maxHp=4, hp=3 → [true, true, true, false]
 * 用于渲染体力指示器（实心=有体力, 空心=已损失）
 */
function hpCells(actor: BattleActor) {
  return Array.from({ length: actor.maxHp }, (_, index) => index < actor.hp)
}

/**
 * 卡牌样式 —— 通过 CSS 自定义属性设置卡牌颜色
 *
 * --card-color 在 CSS 中通过 var() 读取，实现不同颜色的卡牌
 */
function cardStyle(card: BattleCard) {
  return {
    '--card-color': cardMetaMap.value[card.kind]?.color ?? '#333'
  }
}

/**
 * 判断一张卡牌是否可用于响应杀
 *
 * 标准规则：只有"闪"可以响应杀
 * 赵云龙胆：杀也可以当闪使用
 */
function canRespondWithCard(card: BattleCard) {
  return card.kind === 'shan' || (state.value?.player.generalId === 'zhao-yun' && card.kind === 'sha')
}

/**
 * 判断卡牌是否禁用（不可点击）
 *
 * 需要响应时：只有能用来响应的牌才可点击
 * 正常出牌时：不可出牌的牌禁用
 */
function cardDisabled(card: BattleCard) {
  if (playerNeedsResponse.value) {
    return !canRespondWithCard(card)
  }
  return !playerCanAct.value
}

/**
 * 卡牌点击处理 —— 根据当前状态决定是出牌还是响应
 *
 * 如果处于"需要响应"状态 → 尝试用这张牌响应（出闪）
 * 否则 → 正常出牌
 */
function handleCardClick(card: BattleCard) {
  if (playerNeedsResponse.value) {
    if (canRespondWithCard(card)) {
      emit('respondCard', card.id)
    }
    return
  }
  emit('playCard', card.id)
}
</script>

<template>
  <section class="battle-shell">
    <!-- 顶部标题栏 -->
    <div class="battle-topbar">
      <div>
        <p class="eyebrow">三国杀轻量对战</p>
        <h1>{{ playerGeneral.name }} VS {{ enemyGeneral.name }}</h1>
      </div>
      <div class="action-row action-row--right">
        <el-tag size="large" type="danger" effect="plain">第 {{ state?.round || 1 }} 回合</el-tag>
        <!--
          按钮文字根据状态切换：
          - 无对战：显示"开始"（VideoPlay 图标）
          - 已有对战：显示"重开"（Refresh 图标）
        -->
        <el-button size="large" :icon="state ? Refresh : VideoPlay" :loading="loading" @click="$emit('create')">
          {{ state ? '重开' : '开始' }}
        </el-button>
      </div>
    </div>

    <!-- 未开始对战时显示空状态 -->
    <el-empty v-if="!state" :description="loading ? '正在创建对战' : '还没有对战'">
      <el-button type="primary" :icon="VideoPlay" :loading="loading" @click="$emit('create')">
        开始对战
      </el-button>
    </el-empty>

    <!-- 对战棋盘 -->
    <div v-else class="battle-board">
      <!-- 敌方区域（AI 赵云） -->
      <section class="fighter fighter--enemy">
        <img :src="enemyGeneral.avatar" :alt="enemyGeneral.name" />
        <div>
          <p class="eyebrow">大模型托管</p>
          <h2>{{ state.enemy.name }}</h2>
          <!-- 体力指示器 -->
          <div class="hp-row">
            <span v-for="(alive, index) in hpCells(state.enemy)" :key="index" :class="{ empty: !alive }"></span>
          </div>
          <p class="muted">手牌 {{ state.enemy.handCount ?? state.enemy.hand.length }} 张 · {{ state.enemy.skillName || enemyGeneral.skill.name }}</p>
        </div>
      </section>

      <!-- 中间牌堆信息 -->
      <section class="arena-center">
        <!-- el-statistic：数字统计组件 -->
        <el-statistic title="牌堆" :value="deckCount" />
        <!--
          回合指示器
          不同 class 控制不同颜色：
          - turn-banner--enemy：敌方回合（红色）
          - turn-banner--pending：等待响应（橙色）
        -->
        <div
          class="turn-banner"
          :class="{
            'turn-banner--enemy': state.current === 'enemy',
            'turn-banner--pending': playerNeedsResponse
          }"
        >
          {{ turnText }}
        </div>
        <el-statistic title="弃牌" :value="discardCount" />
      </section>

      <!-- 玩家区域（张飞） -->
      <section class="fighter">
        <img :src="playerGeneral.avatar" :alt="playerGeneral.name" />
        <div>
          <p class="eyebrow">玩家</p>
          <h2>{{ state.player.name }}</h2>
          <div class="hp-row">
            <span v-for="(alive, index) in hpCells(state.player)" :key="index" :class="{ empty: !alive }"></span>
          </div>
          <p class="muted">本回合已出杀 {{ state.player.shaUsed }} 张 · {{ state.player.skillName || playerGeneral.skill.name }}</p>
        </div>
      </section>
    </div>

    <!--
      响应面板 —— 仅当玩家需要响应杀时显示

      这里展示攻击详情，让玩家决定：
      - 选择一张可用的牌来响应（出闪抵消）
      - 或者不出闪（承受伤害）
    -->
    <el-card v-if="playerNeedsResponse && pendingResponse" class="response-panel" shadow="never">
      <div class="response-summary">
        <el-tag type="warning" effect="dark">等待响应</el-tag>
        <div>
          <h2>请决定是否出闪</h2>
          <p>
            {{ pendingResponse.attackerName }} 对你使用
            {{ pendingResponse.attackCardName }}（{{ pendingResponse.attackCardSuit }} {{ pendingResponse.attackCardPoint }}）。
            不响应将受到 {{ pendingResponse.damage }} 点伤害。
          </p>
        </div>
      </div>

      <!-- 可用的响应牌列表 -->
      <div class="response-card-list">
        <el-button
          v-for="card in responseCards"
          :key="card.id"
          type="warning"
          plain
          @click="emit('respondCard', card.id)"
        >
          出 {{ card.name }} · {{ card.suit }} {{ card.point }}
        </el-button>
        <el-button type="danger" :loading="loading" @click="emit('passResponse')">
          不出闪
        </el-button>
      </div>

      <!-- 无可用牌时提示 -->
      <el-alert
        v-if="!responseCards.length"
        class="response-empty"
        title="当前没有可用作闪的手牌"
        type="info"
        show-icon
        :closable="false"
      />
    </el-card>

    <!--
      手牌面板 —— 玩家可操作区域

      每张手牌渲染为一个按钮，点击后根据当前状态：
      - 需要响应时 → 尝试出闪
      - 正常出牌时 → 出这张牌

      :disabled 绑定 cardDisabled() 判断该牌是否可点击
      :style 绑定 cardStyle() 设置卡牌颜色
    -->
    <el-card v-if="state" class="hand-panel" shadow="never">
      <template #header>
        <div class="panel-heading">
          <div>
            <p class="eyebrow">手牌</p>
            <h2>{{ playerNeedsResponse ? '请响应杀' : '选择牌行动' }}</h2>
          </div>
          <!-- 结束回合按钮：仅在玩家可以主动出牌时可用 -->
          <el-button type="primary" :disabled="!playerCanAct" @click="$emit('endTurn')">
            结束回合
          </el-button>
        </div>
      </template>

      <el-empty v-if="!playerHand.length" description="暂无手牌" />
      <div v-else class="card-hand">
        <!-- 每张手牌一个按钮 -->
        <button
          v-for="card in playerHand"
          :key="card.id"
          class="battle-card"
          type="button"
          :style="cardStyle(card)"
          :disabled="cardDisabled(card)"
          @click="handleCardClick(card)"
        >
          <span>{{ card.suit }} {{ card.point }}</span>
          <strong>{{ card.name }}</strong>
          <small>{{ cardMetaMap[card.kind]?.shortRule }}</small>
        </button>
      </div>
    </el-card>

    <!-- 底部信息：AI 思考 + 战斗日志 -->
    <section v-if="state" class="battle-bottom">
      <!-- AI 思考过程 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">AI 思考</p>
            <h2>赵云决策链</h2>
          </div>
        </template>
        <el-timeline class="compact-timeline">
          <el-timeline-item v-for="thought in state.aiThoughts" :key="thought">
            {{ thought }}
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 战斗日志 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">战斗日志</p>
            <h2>最近行动</h2>
          </div>
        </template>
        <!-- 最多显示最近 8 条日志 -->
        <div class="log-list">
          <el-tag
            v-for="log in state.logs.slice(0, 8)"
            :key="log.id"
            class="log-item"
            :type="log.type === 'damage' ? 'danger' : log.type === 'heal' ? 'success' : log.type === 'ai' ? 'warning' : 'info'"
            effect="plain"
          >
            {{ log.text }}
          </el-tag>
        </div>
      </el-card>
    </section>
  </section>
</template>
