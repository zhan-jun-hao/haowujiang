<!--
  PlazaView.vue — 武将广场页

  应用的主页面，展示所有武将的卡片列表。
  用户可以浏览武将、查看详情、发起对战、进入秒杀页。

  这是用户登录后看到的第一个页面。
-->
<script setup lang="ts">
import { Lightning } from '@element-plus/icons-vue'
import GeneralCard from '../components/GeneralCard.vue'
import type { General, GeneralId } from '../types'

// 父组件传入的数据
defineProps<{
  generals: General[]     // 所有武将列表
  ownedIds: GeneralId[]   // 已拥有的武将 ID 列表（用于显示拥有状态）
}>()

// 向父组件发出的事件
defineEmits<{
  detail: [id: GeneralId]  // 查看详情
  battle: [id: GeneralId]  // 发起对战
  seckill: []              // 跳转秒杀页
}>()
</script>

<template>
  <section class="view-stack">
    <!-- 页面标题栏 -->
    <div class="page-heading">
      <div>
        <p class="eyebrow">武将广场</p>
        <h1>选择武将，进入轻量三国杀对战</h1>
      </div>
      <!-- 快速入口：免费秒杀赵云 -->
      <el-button type="primary" size="large" :icon="Lightning" @click="$emit('seckill')">
        免费抢赵云
      </el-button>
    </div>

    <!--
      武将卡片网格
      v-for 遍历 generals 数组，为每个武将渲染一个 GeneralCard 组件
      :key 是 Vue 列表渲染的必要属性，用于高效更新 DOM

      数据流：父 (App.vue) → PlazaView (props) → GeneralCard (props)
      事件流：GeneralCard (emit) → PlazaView (emit) → 父 (App.vue)
    -->
    <div class="general-grid">
      <GeneralCard
        v-for="general in generals"
        :key="general.id"
        :general="general"
        :owned="ownedIds.includes(general.id)"
        @detail="$emit('detail', $event)"
        @battle="$emit('battle', $event)"
      />
    </div>

    <!-- 底部规则速览 -->
    <section class="rule-strip">
      <el-card shadow="never">
        <p class="eyebrow">基础牌堆</p>
        <h2>杀 / 闪 / 桃 / 酒</h2>
      </el-card>
      <el-card shadow="never">
        <p class="eyebrow">对战规则</p>
        <h2>张飞可突破出杀限制</h2>
      </el-card>
      <el-card shadow="never">
        <p class="eyebrow">AI 决策</p>
        <h2>RAG 记忆增强</h2>
      </el-card>
    </section>
  </section>
</template>
