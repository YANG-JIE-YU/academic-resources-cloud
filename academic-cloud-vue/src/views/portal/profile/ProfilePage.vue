<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="个人中心" description="查看个人资料、收藏、评论与论坛互动记录。">
        <template #extra>
          <n-space>
            <n-button @click="$router.push('/favorites')">我的收藏</n-button>
            <n-button @click="$router.push('/my-comments')">我的评论</n-button>
            <n-button @click="$router.push('/my-forum')">我的论坛</n-button>
          </n-space>
        </template>
      </page-header-card>

      <n-grid cols="1 m:3" responsive="screen" :x-gap="16" :y-gap="16">
        <n-grid-item :span="2">
          <n-card title="基础信息" :bordered="false">
            <n-descriptions label-placement="left" :column="1">
              <n-descriptions-item label="用户名">{{ profile?.username || '-' }}</n-descriptions-item>
              <n-descriptions-item label="姓名">{{ profile?.realName || '-' }}</n-descriptions-item>
              <n-descriptions-item label="手机号">{{ profile?.phone || '-' }}</n-descriptions-item>
              <n-descriptions-item label="性别">{{ profile?.gender || '-' }}</n-descriptions-item>
              <n-descriptions-item label="角色">{{ userStore.role || '-' }}</n-descriptions-item>
              <n-descriptions-item label="注册时间">{{ formatDateTime(profile?.createTime) }}</n-descriptions-item>
            </n-descriptions>
          </n-card>
        </n-grid-item>
        <n-grid-item>
          <n-card title="快捷入口" :bordered="false">
            <n-space vertical>
              <n-button block tertiary @click="$router.push('/ai/chat')">AI 对话助手</n-button>
              <n-button block tertiary @click="$router.push('/ai/recommend')">智能推荐</n-button>
              <n-button block tertiary @click="$router.push('/papers')">论文资源库</n-button>
              <n-button block @click="logout">退出登录</n-button>
            </n-space>
          </n-card>
        </n-grid-item>
      </n-grid>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { formatDateTime } from '@/utils/format'
import { useUserStore } from '@/stores/user'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const router = useRouter()
const userStore = useUserStore()
const profile = computed(() => userStore.profile)

async function logout() {
  await userStore.logout()
  router.push('/auth/login')
}

onMounted(() => {
  if (!userStore.profile) {
    userStore.fetchProfile().catch(() => null)
  }
})
</script>
