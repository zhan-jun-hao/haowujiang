<!--
  SeckillView.vue — 秒杀页

  核心业务页面，展示秒杀活动的实时状态和操作入口。

  秒杀架构（后端实现，前端展示）：
  ┌─────────┐    ┌──────────┐    ┌───────────┐    ┌───────┐    ┌───────┐
  │ 用户点击 │ → │ Sentinel │ → │ Redis Lua │ → │ Kafka │ → │ MySQL │
  │  抢购   │    │  限流    │    │ 原子扣库存 │    │ 异步下单│    │ 持久化 │
  └─────────┘    └──────────┘    └───────────┘    └───────┘    └───────┘

  关键设计：
  - Redis Lua 脚本保证库存扣减的原子性（不会超卖）
  - Sentinel 做前置限流（QPS 控制），不替代库存逻辑
  - Kafka 异步下单，削峰填谷（秒杀高峰时不直接冲击 MySQL）
-->
<script setup lang="ts">
import { computed } from 'vue'
import { Lightning } from '@element-plus/icons-vue'
import type { General, SeckillResult, SeckillState } from '../types'

const props = defineProps<{
  general: General           // 秒杀目标武将（赵云）
  seckill: SeckillState      // 当前秒杀实时状态
  lastResult: SeckillResult | null  // 最近一次秒杀操作的结果
  owned: boolean             // 用户是否已拥有该武将
}>()

// 向父组件发出"抢购"事件
defineEmits<{
  claim: []
}>()

/**
 * 库存百分比 —— 用于进度条展示
 *
 * computed 会根据 seckill.stock 和 seckill.total 的变化自动重新计算
 */
const stockPercent = computed(() => {
  if (!props.seckill.total) {
    return 0  // 总库存为 0 时百分比为 0，避免除以零
  }
  return Math.round((props.seckill.stock / props.seckill.total) * 100)
})
</script>

<template>
  <section class="view-stack">
    <!-- 页面标题 + 抢购按钮 -->
    <div class="page-heading">
      <div>
        <p class="eyebrow">免费秒杀</p>
        <h1>抢限定武将 {{ general.name }}</h1>
      </div>
      <!--
        按钮状态逻辑：
        - owned（已拥有）→ 禁用，显示"已拥有"
        - stock <= 0（已抢完）→ 禁用，显示"已抢完"
        - 否则 → 可点击，显示"立即抢"
      -->
      <el-button
        type="primary"
        size="large"
        :icon="Lightning"
        :disabled="owned || seckill.stock <= 0"
        @click="$emit('claim')"
      >
        {{ owned ? '已拥有' : seckill.stock > 0 ? '立即抢' : '已抢完' }}
      </el-button>
    </div>

    <!-- 库存实时展示 -->
    <el-card class="seckill-hero" shadow="never">
      <img :src="general.avatar" :alt="general.name" />
      <div class="seckill-hero__content">
        <p class="eyebrow">Redis 预热库存</p>
        <!-- 库存数字：当前 / 总计 -->
        <strong>{{ seckill.stock }} / {{ seckill.total }}</strong>
        <!--
          el-progress：进度条组件
          :percentage 绑定库存百分比
          status="success" 绿色进度条
        -->
        <el-progress :percentage="stockPercent" :stroke-width="14" status="success" />
        <span>核心一致性由 Redis Lua 保证，Kafka 异步创建订单。</span>
      </div>
    </el-card>

    <!--
      秒杀结果展示
      v-if="lastResult" —— 只有执行过秒杀操作后才显示
    -->
    <el-result
      v-if="lastResult"
      :icon="lastResult.ok ? 'success' : 'warning'"
      :title="lastResult.title"
      :sub-title="lastResult.message"
    >
      <template #extra>
        <!--
          el-steps：步骤条组件
          direction="vertical"：竖向展示
          :active="lastResult.steps.length"：所有步骤都标记为已完成
          finish-status="success"：完成状态为绿色对勾
        -->
        <el-steps direction="vertical" :active="lastResult.steps.length" finish-status="success">
          <el-step v-for="step in lastResult.steps" :key="step" :title="step" />
        </el-steps>
      </template>
    </el-result>

    <!-- 双栏说明：秒杀链路 + Sentinel 说明 -->
    <section class="two-column">
      <!-- 秒杀链路步骤 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">秒杀链路</p>
            <h2>秒杀系统链路架构</h2>
          </div>
        </template>
        <!-- 5 个步骤全部高亮（active=5） -->
        <el-steps direction="vertical" :active="5" finish-status="success">
          <el-step title="用户点击" />
          <el-step title="Sentinel 限流" />
          <el-step title="Redis Lua 扣库存" />
          <el-step title="Kafka 下单事件" />
          <el-step title="MySQL 发放武将" />
        </el-steps>
      </el-card>

      <!-- Sentinel 技术说明 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">Sentinel 结论</p>
            <h2>保护入口，不替代库存逻辑</h2>
          </div>
        </template>
        <p class="lead">
          Sentinel 适合 QPS 限流、热点参数限流、熔断降级和兜底提示；库存扣减和一人一单仍应放在 Redis Lua 的原子脚本里。
        </p>
      </el-card>
    </section>

    <!-- 双栏数据：秒杀日志 + Kafka 订单 -->
    <section class="two-column">
      <!-- 秒杀事件日志 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">事件</p>
            <h2>秒杀日志</h2>
          </div>
        </template>
        <!--
          el-table：表格组件
          height="320"：固定高度，超出滚动
          empty-text：无数据时的提示文字
        -->
        <el-table :data="seckill.events" stripe height="320" empty-text="暂无事件">
          <el-table-column prop="message" label="事件" min-width="180" />
          <el-table-column prop="createdAt" label="时间" width="180" />
          <!-- 自定义列：根据 level 显示不同颜色的标签 -->
          <el-table-column prop="level" label="级别" width="100">
            <template #default="{ row }">
              <el-tag :type="row.level === 'success' ? 'success' : row.level === 'warn' ? 'warning' : 'info'">
                {{ row.level }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- Kafka 消费生成的订单 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">订单</p>
            <h2>Kafka 消费结果</h2>
          </div>
        </template>
        <el-table :data="seckill.orders" stripe height="320" empty-text="暂无订单">
          <el-table-column prop="generalId" label="武将" width="120" />
          <el-table-column prop="status" label="状态" width="120" />
          <el-table-column prop="createdAt" label="时间" min-width="180" />
          <el-table-column prop="id" label="订单号" min-width="180" />
        </el-table>
      </el-card>
    </section>
  </section>
</template>
