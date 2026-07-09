<!--
  CenterView.vue — 武将中心页

  展示当前用户已拥有的武将列表。
  如果还没有武将，引导用户去秒杀页获取赵云。

  核心逻辑很简单：遍历 ownedGenerals 数组渲染卡片。
-->
<script setup lang="ts">
import { ShoppingCart } from '@element-plus/icons-vue'
import GeneralCard from '../components/GeneralCard.vue'
import type { General, GeneralId } from '../types'

defineProps<{
  ownedGenerals: General[]  // 已拥有的武将列表（由父组件的 computed 属性计算得出）
}>()

defineEmits<{
  detail: [id: GeneralId]
  battle: [id: GeneralId]
  seckill: []
}>()
</script>

<template>
  <section class="view-stack">
    <div class="page-heading">
      <div>
        <p class="eyebrow">武将中心</p>
        <h1>你已拥有的武将</h1>
      </div>
      <el-button size="large" :icon="ShoppingCart" @click="$emit('seckill')">去秒杀</el-button>
    </div>

    <!-- 有武将时渲染卡片列表 -->
    <div v-if="ownedGenerals.length" class="general-grid">
      <GeneralCard
        v-for="general in ownedGenerals"
        :key="general.id"
        :general="general"
        owned
        @detail="$emit('detail', $event)"
        @battle="$emit('battle', $event)"
      />
    </div>

    <!-- 没有武将时显示空状态提示 -->
    <!-- el-empty：Element Plus 的空状态组件 -->
    <el-empty v-else description="还没有武将">
      <template #default>
        <p class="muted">默认演示账号会拥有张飞；如果数据被清空，可以从秒杀页重新领取赵云。</p>
        <el-button type="primary" :icon="ShoppingCart" @click="$emit('seckill')">去秒杀</el-button>
      </template>
    </el-empty>
  </section>
</template>
