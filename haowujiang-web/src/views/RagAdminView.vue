<!--
  RagAdminView.vue — RAG 知识库后台管理页

  管理员专用页面，用于维护武将的 RAG（检索增强生成）知识库。

  功能：
  - 新增知识文档（选择武将 + 填写标题和内容）
  - 按武将分组展示已有的知识文档

  RAG 的作用：
  在对战中，赵云（AI 驱动）出牌前会先从这个知识库检索相关文档，
  将检索结果注入到大模型的提示词中，让 AI 做出更符合武将设定的决策。
  例如：知识库里写了"血量低于 2 时优先留闪防御"，AI 就会遵守这个策略。
-->
<script setup lang="ts">
import { computed, reactive } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import type { General, GeneralId, RagDocument } from '../types'

const props = defineProps<{
  generals: General[]    // 所有武将列表（用于选择器）
  documents: RagDocument[]  // 所有 RAG 文档
}>()

// 向父组件发出"添加文档"事件
const emit = defineEmits<{
  add: [payload: Pick<RagDocument, 'generalId' | 'title' | 'content'>]
}>()

// 录入表单 —— reactive 响应式对象，双向绑定到表单控件
const form = reactive({
  generalId: 'zhao-yun' as GeneralId,  // 默认选中赵云
  title: '',
  content: ''
})

/**
 * 按武将分组展示文档
 *
 * computed 自动根据 generals 和 documents 的变化重新计算分组结果。
 * 每个分组包含武将信息和该武将的所有文档。
 */
const groupedDocuments = computed(() => {
  return props.generals.map((general) => ({
    general,
    documents: props.documents.filter((doc) => doc.generalId === general.id)
  }))
})

/** 提交新知识文档 */
function submit() {
  // 前端基础校验：标题和内容不能为空
  if (!form.title.trim() || !form.content.trim()) {
    return
  }

  // 向父组件发出事件，由 App.vue 调用后端 API
  emit('add', {
    generalId: form.generalId,
    title: form.title,
    content: form.content
  })

  // 清空表单，方便继续录入
  form.title = ''
  form.content = ''
}
</script>

<template>
  <section class="view-stack">
    <div class="page-heading">
      <div>
        <p class="eyebrow">RAG 后台</p>
        <h1>维护武将专属知识库</h1>
      </div>
    </div>

    <section class="admin-layout">
      <!-- 左侧：知识录入表单 -->
      <el-card shadow="never">
        <template #header>
          <div>
            <p class="eyebrow">知识录入</p>
            <h2>新增 RAG 文档</h2>
          </div>
        </template>

        <!--
          录入表单
          label-position="top"：标签显示在输入框上方
        -->
        <el-form class="editor-form" label-position="top">
          <!-- 武将选择器 -->
          <el-form-item label="武将">
            <el-select v-model="form.generalId" class="full-width">
              <!-- v-for 遍历生成下拉选项 -->
              <el-option
                v-for="general in generals"
                :key="general.id"
                :label="general.name"
                :value="general.id"
              />
            </el-select>
          </el-form-item>

          <!-- 知识标题 -->
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="例如：赵云濒危防御策略" />
          </el-form-item>

          <!-- 知识内容（多行文本） -->
          <el-form-item label="知识内容">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="8"
              placeholder="写入武将技能、出牌偏好、历史对局记忆或提示词片段"
            />
          </el-form-item>

          <el-button type="primary" :icon="Plus" @click="submit">
            添加知识
          </el-button>
        </el-form>
      </el-card>

      <!-- 右侧：按武将分组的文档列表 -->
      <div class="rag-columns">
        <!-- 每个武将一个分组卡片 -->
        <el-card v-for="group in groupedDocuments" :key="group.general.id" shadow="never">
          <template #header>
            <div class="card-header">
              <div>
                <p class="eyebrow">{{ group.general.name }}</p>
                <h2>{{ group.documents.length }} 条知识</h2>
              </div>
              <el-tag type="success" effect="plain">{{ group.general.skill.name }}</el-tag>
            </div>
          </template>

          <el-empty v-if="!group.documents.length" description="暂无知识" />
          <!-- 文档列表 -->
          <div v-else class="rag-list">
            <el-card v-for="doc in group.documents" :key="doc.id" class="rag-item" shadow="never">
              <strong>{{ doc.title }}</strong>
              <span>{{ doc.content }}</span>
              <small>{{ doc.updatedAt }}</small>
            </el-card>
          </div>
        </el-card>
      </div>
    </section>
  </section>
</template>
