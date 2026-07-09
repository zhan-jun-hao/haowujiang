<!--
  ArchitectureView.vue — 系统蓝图页

  展示后端技术栈架构，包括：
  - 各技术组件的职责描述（SpringBoot, Redis, Kafka, LangChain4j, Sentinel）
  - 前后端 API 联调清单

  这是一个纯展示页面，不需要交互逻辑。
-->
<script setup lang="ts">
import { backendBlueprint } from '../data/catalog'

// 后端 API 端点清单（展示用）
const apiRows = [
  'GET /api/client/generals',
  'GET /api/client/users/me/generals',
  'POST /api/client/seckill/generals/{id}',
  'POST /api/client/battles',
  'POST /api/client/battles/{id}/actions',
  'POST /api/client/battles/{id}/ai-action',
  'GET /api/admin/rag/generals/{id}/documents',
  'POST /api/admin/rag/generals/{id}/documents'
].map((api) => ({ api }))
</script>

<template>
  <section class="view-stack">
    <div class="page-heading">
      <div>
        <p class="eyebrow">系统蓝图</p>
        <h1>后端技术栈与前端对接边界</h1>
      </div>
    </div>

    <!-- 技术栈架构卡片网格 -->
    <section class="architecture-grid">
      <!-- v-for 遍历 backendBlueprint 数组，每个技术组件渲染一张卡片 -->
      <el-card v-for="item in backendBlueprint" :key="item.title" shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">模块</p>
            <h2>{{ item.title }}</h2>
          </div>
        </template>
        <p class="lead">{{ item.body }}</p>
      </el-card>
    </section>

    <!-- API 联调清单 -->
    <el-card shadow="never">
      <template #header>
        <div>
          <p class="eyebrow">推荐接口</p>
          <h2>前后端联调清单</h2>
        </div>
      </template>
      <!-- stripe：斑马纹表格，提升可读性 -->
      <el-table :data="apiRows" stripe>
        <el-table-column prop="api" label="API" />
      </el-table>
    </el-card>
  </section>
</template>
