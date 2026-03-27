<template>
  <n-layout class="portal-layout">
    <n-layout-header bordered class="portal-header">
      <div class="page-container header-inner">
        <div class="brand" @click="goHome">
          <n-icon :size="24"><LibraryOutline /></n-icon>
          <span>学术云门户</span>
        </div>

        <n-space :size="16" align="center" class="nav-list">
          <n-button
            v-for="item in navItems"
            :key="item.key"
            text
            :type="isActive(item.path) ? 'primary' : 'default'"
            @click="router.push(item.path)"
          >
            <template #icon>
              <n-icon :size="16">
                <component :is="item.icon" />
              </n-icon>
            </template>
            {{ item.label }}
          </n-button>
        </n-space>

        <n-space :size="12" align="center">
          <n-input
            v-model:value="keyword"
            clearable
            placeholder="请输入论文关键词"
            style="width: 220px"
            @keyup.enter="doSearch"
          />
          <n-button type="primary" ghost @click="doSearch">搜索</n-button>

          <n-dropdown v-if="userStore.isLoggedIn" :options="userOptions" @select="handleUserAction">
            <n-button>
              {{ userStore.username || '用户' }}
              <template #icon>
                <n-icon><PersonCircleOutline /></n-icon>
              </template>
            </n-button>
          </n-dropdown>
          <n-button v-else type="primary" @click="router.push('/auth/login')">登录</n-button>
        </n-space>
      </div>
    </n-layout-header>

    <n-layout-content content-style="min-height: calc(100vh - 144px);">
      <router-view />
    </n-layout-content>

    <n-layout-footer bordered class="portal-footer">
      <div class="page-container footer-inner">基于微服务架构的学术资源门户系统 | Vue 3 + Naive UI</div>
    </n-layout-footer>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { DropdownOption } from 'naive-ui'
import {
  BookOutline,
  ChatbubblesOutline,
  HomeOutline,
  LibraryOutline,
  MegaphoneOutline,
  PeopleOutline,
  PersonCircleOutline
} from '@vicons/ionicons5'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const appStore = useAppStore()
const keyword = ref(appStore.portalKeyword)

const navItems = [
  { key: 'home', label: '首页', path: '/', icon: HomeOutline },
  { key: 'notices', label: '公告', path: '/notices', icon: MegaphoneOutline },
  { key: 'activities', label: '活动', path: '/activities', icon: PeopleOutline },
  { key: 'papers', label: '论文', path: '/papers', icon: BookOutline },
  { key: 'forum', label: '论坛', path: '/forum', icon: ChatbubblesOutline }
]

const userOptions = computed<DropdownOption[]>(() => {
  const options: DropdownOption[] = [
    { key: 'profile', label: '个人中心' },
    { key: 'favorites', label: '我的收藏' }
  ]
  if (userStore.isAdmin) {
    options.push({ key: 'admin', label: '管理后台' })
  }
  options.push({ key: 'logout', label: '退出登录' })
  return options
})

function isActive(path: string) {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

function goHome() {
  router.push('/')
}

function doSearch() {
  appStore.setPortalKeyword(keyword.value.trim())
  router.push({
    path: '/search',
    query: { keyword: keyword.value.trim() }
  })
}

async function handleUserAction(action: string) {
  if (action === 'logout') {
    await userStore.logout()
    router.push('/')
    return
  }
  if (action === 'admin') {
    router.push('/admin/dashboard')
    return
  }
  router.push(`/${action}`)
}
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
}

.portal-header {
  position: sticky;
  top: 0;
  z-index: 20;
  background: #ffffff;
}

.header-inner {
  display: grid;
  grid-template-columns: 220px 1fr auto;
  gap: 16px;
  align-items: center;
  min-height: 68px;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--primary);
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
}

.nav-list {
  overflow-x: auto;
  white-space: nowrap;
}

.portal-footer {
  background: #f3f5f8;
}

.footer-inner {
  min-height: 76px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-sub);
}
</style>
