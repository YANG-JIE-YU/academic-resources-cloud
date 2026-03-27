<template>
  <div class="page-container">
    <n-space vertical :size="16">
      <page-header-card title="我的收藏" description="按时间查看你收藏的论文或资源。">
        <template #extra>
          <n-button @click="$router.push('/profile')">返回个人中心</n-button>
        </template>
      </page-header-card>

      <n-card :bordered="false">
        <n-spin :show="loading">
          <n-space v-if="list.length > 0" vertical :size="10">
            <n-card v-for="item in list" :key="item.id" embedded>
              <n-space justify="space-between" align="center">
                <n-space vertical :size="4">
                  <strong>{{ item.targetTitle }}</strong>
                  <span class="meta">类型：{{ item.targetType }} · 收藏时间：{{ formatDateTime(item.createTime) }}</span>
                </n-space>
                <n-space>
                  <n-button size="small" type="primary" ghost @click="openTarget(item)">查看</n-button>
                  <n-popconfirm @positive-click="remove(item.id)">
                    <template #trigger>
                      <n-button size="small" type="error" ghost>取消收藏</n-button>
                    </template>
                    确认取消收藏？
                  </n-popconfirm>
                </n-space>
              </n-space>
            </n-card>
          </n-space>
          <n-empty v-else description="你还没有收藏记录" />
        </n-spin>
      </n-card>
    </n-space>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useMessage } from 'naive-ui'
import { deleteFavorite, fetchMyFavorites } from '@/api/user'
import type { FavoriteItem } from '@/types/user'
import { formatDateTime } from '@/utils/format'
import PageHeaderCard from '@/components/common/PageHeaderCard.vue'

const router = useRouter()
const message = useMessage()
const loading = ref(false)
const list = ref<FavoriteItem[]>([])

async function load() {
  loading.value = true
  try {
    list.value = await fetchMyFavorites()
  } finally {
    loading.value = false
  }
}

async function remove(id: number) {
  await deleteFavorite(id)
  message.success('已取消收藏')
  await load()
}

function openTarget(item: FavoriteItem) {
  if (item.targetType.toUpperCase() === 'PAPER') {
    router.push(`/papers/${item.targetId}`)
    return
  }
  router.push('/')
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.meta {
  color: var(--text-sub);
  font-size: 13px;
}
</style>
