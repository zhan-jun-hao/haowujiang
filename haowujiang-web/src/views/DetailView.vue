<!--
  DetailView.vue — 武将详情页

  展示单个武将的完整信息：
  - 武将头像和基本属性
  - 技能详情（名称、简介、详细规则列表）
  - 关联的后端 API 端点
  - RAG 知识库文档

  同时提供快捷操作：去秒杀（未拥有且是秒杀武将时）和进入对战。
-->
<script setup lang="ts">
import { Lightning, VideoPlay } from '@element-plus/icons-vue'
import type { General, RagDocument } from '../types'

defineProps<{
  general: General              // 要展示的武将
  owned: boolean               // 是否已拥有
  ragDocuments: RagDocument[]  // 该武将的 RAG 知识文档
}>()

defineEmits<{
  battle: []   // 进入对战
  seckill: []  // 跳转秒杀页
}>()
</script>

<template>
  <section class="detail-layout">
    <!-- 左侧：武将头像面板 -->
    <aside class="portrait-panel">
      <img :src="general.avatar" :alt="general.name" />
      <el-tag class="portrait-panel__hp" size="large" type="danger" effect="dark">
        {{ general.hp }} 血
      </el-tag>
    </aside>

    <!-- 右侧：详细信息 -->
    <div class="view-stack">
      <!-- 标题栏 -->
      <div class="page-heading">
        <div>
          <p class="eyebrow">{{ general.camp }} · {{ general.role }}</p>
          <h1>{{ general.name }} · {{ general.title }}</h1>
        </div>
        <div class="action-row action-row--right">
          <!--
            条件渲染：未拥有 + 秒杀武将 → 显示"去秒杀"按钮
            v-if 后面的表达式为 true 时才渲染这个按钮
          -->
          <el-button
            v-if="!owned && general.unlockSource === 'seckill'"
            size="large"
            :icon="Lightning"
            @click="$emit('seckill')"
          >
            去秒杀
          </el-button>
          <el-button type="primary" size="large" :icon="VideoPlay" @click="$emit('battle')">
            进入对战
          </el-button>
        </div>
      </div>

      <!-- 技能详情卡片 -->
      <el-card shadow="never">
        <template #header>
          <div class="card-header">
            <div>
              <p class="eyebrow">技能</p>
              <h2>{{ general.skill.name }}</h2>
            </div>
            <el-tag :type="owned ? 'success' : 'warning'">
              {{ owned ? '已拥有' : '未拥有' }}
            </el-tag>
          </div>
        </template>
        <p class="lead">{{ general.skill.summary }}</p>
        <!-- el-timeline：时间线组件，用于展示技能的详细规则列表 -->
        <el-timeline class="compact-timeline">
          <el-timeline-item v-for="rule in general.skill.rules" :key="rule">
            {{ rule }}
          </el-timeline-item>
        </el-timeline>
      </el-card>

      <!-- 双栏布局：API 端点 + RAG 知识 -->
      <section class="two-column">
        <!-- API 端点列表 -->
        <el-card shadow="never">
          <template #header>
            <div>
              <p class="eyebrow">接口占位</p>
              <h2>后端接入点</h2>
            </div>
          </template>
          <!-- 将 apiHooks 字符串数组转为对象数组，以便 el-table 渲染 -->
          <el-table :data="general.apiHooks.map((hook) => ({ hook }))" stripe>
            <el-table-column prop="hook" label="API" />
          </el-table>
        </el-card>

        <!-- RAG 知识文档列表 -->
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <p class="eyebrow">RAG 记忆</p>
                <h2>{{ ragDocuments.length }} 条知识</h2>
              </div>
            </div>
          </template>
          <el-empty v-if="!ragDocuments.length" description="暂无知识" />
          <div v-else class="rag-list rag-list--compact">
            <el-card v-for="doc in ragDocuments" :key="doc.id" class="rag-item" shadow="never">
              <strong>{{ doc.title }}</strong>
              <span>{{ doc.content }}</span>
            </el-card>
          </div>
        </el-card>
      </section>
    </div>
  </section>
</template>
