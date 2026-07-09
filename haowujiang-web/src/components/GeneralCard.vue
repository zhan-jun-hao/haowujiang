<!--
  GeneralCard.vue — 武将卡片组件

  这是一个可复用的展示组件，用于在广场页和中心页展示武将信息。
  每个武将渲染为一张卡片，包含头像、名称、技能简介、标签和操作按钮。

  Vue 3 组件设计要点：
  - 使用 <script setup> 语法糖，代码更简洁
  - defineProps 定义组件接收的属性（父组件传入的数据）
  - defineEmits 定义组件发出的事件（向父组件通信）
  - 纯展示组件，不包含业务逻辑，只负责渲染和转发用户操作

  数据流方向：父组件 → props → 子组件渲染 → emit 事件 → 父组件处理
-->
<script setup lang="ts">
import { View, VideoPlay } from '@element-plus/icons-vue'
import type { General } from '../types'

// 父组件传入的数据
defineProps<{
  general: General   // 要展示的武将信息
  owned: boolean     // 当前用户是否已拥有该武将
}>()

// 向父组件发出的事件
// 'detail' — 用户点击"详情"按钮
// 'battle' — 用户点击"对战"按钮
defineEmits<{
  detail: [id: General['id']]
  battle: [id: General['id']]
}>()
</script>

<template>
  <!--
    el-card：Element Plus 的卡片组件
    shadow="hover"：鼠标悬停时显示阴影，提升交互感
  -->
  <el-card class="general-card" shadow="hover">
    <!-- 卡片上部：头像 + 拥有状态标签 -->
    <div class="general-card__media">
      <img class="general-card__avatar" :src="general.avatar" :alt="general.name" />
      <!-- effect="dark"：深色填充风格标签 -->
      <el-tag class="general-card__owned" :type="owned ? 'success' : 'warning'" effect="dark">
        {{ owned ? '已拥有' : '未拥有' }}
      </el-tag>
    </div>

    <!-- 卡片下部：武将信息 + 操作按钮 -->
    <div class="general-card__body">
      <!-- 武将基本信息 -->
      <div class="general-card__header">
        <div>
          <p class="eyebrow">{{ general.camp }} · {{ general.rarity }}</p>
          <h3>{{ general.name }}</h3>
          <p class="muted">{{ general.title }}</p>
        </div>
        <el-tag size="large" type="danger" effect="plain">{{ general.hp }} 血</el-tag>
      </div>

      <!-- 技能简介 -->
      <p class="skill-line">
        <strong>{{ general.skill.name }}</strong>
        {{ general.skill.summary }}
      </p>

      <!-- 标签行 —— 使用 v-for 遍历生成标签 -->
      <div class="tag-row">
        <el-tag v-for="tag in general.tags" :key="tag" type="success" effect="plain">
          {{ tag }}
        </el-tag>
      </div>

      <!-- 操作按钮行 -->
      <div class="action-row">
        <!--
          $emit('detail', general.id)：
          向父组件发出 'detail' 事件，携带武将 ID，
          父组件中通过 @detail="openDetail" 接收并处理
        -->
        <el-button :icon="View" @click="$emit('detail', general.id)">详情</el-button>
        <el-button type="primary" :icon="VideoPlay" @click="$emit('battle', general.id)">对战</el-button>
      </div>
    </div>
  </el-card>
</template>
