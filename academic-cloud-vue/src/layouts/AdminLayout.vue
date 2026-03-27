<template>
  <n-layout has-sider class="admin-layout">
    <n-layout-sider
      bordered
      collapse-mode="width"
      :collapsed-width="72"
      :width="228"
      :collapsed="appStore.adminSiderCollapsed"
    >
      <div class="sider-brand">
        <n-icon :size="22"><LayersOutline /></n-icon>
        <span v-if="!appStore.adminSiderCollapsed">管理后台</span>
      </div>

      <n-menu
        :collapsed="appStore.adminSiderCollapsed"
        :collapsed-width="72"
        :collapsed-icon-size="20"
        :options="menuOptions"
        :value="activePath"
        @update:value="onMenuChange"
      />
    </n-layout-sider>

    <n-layout>
      <n-layout-header bordered class="admin-header">
        <n-space justify="space-between" align="center" style="width: 100%">
          <n-space align="center">
            <n-button quaternary @click="appStore.toggleAdminSider()">
              {{ appStore.adminSiderCollapsed ? '展开' : '收起' }}
            </n-button>
            <n-breadcrumb>
              <n-breadcrumb-item>后台管理</n-breadcrumb-item>
              <n-breadcrumb-item>{{ String(route.meta.title || '管理工作台') }}</n-breadcrumb-item>
            </n-breadcrumb>
          </n-space>

          <n-space align="center">
            <n-tag type="success">ADMIN</n-tag>
            <n-button text @click="router.push('/')">返回门户</n-button>
            <n-button text @click="logout">退出登录</n-button>
          </n-space>
        </n-space>
      </n-layout-header>

      <n-layout-content content-style="padding: 18px 20px;">
        <router-view />
      </n-layout-content>
    </n-layout>
  </n-layout>
</template>

<script setup lang="ts">
import { computed, h } from 'vue'
import type { Component } from 'vue'
import type { MenuOption } from 'naive-ui'
import {
  BookOutline,
  ChatbubbleEllipsesOutline,
  ChatbubblesOutline,
  GridOutline,
  LayersOutline,
  ListOutline,
  MegaphoneOutline,
  PeopleOutline,
  PersonOutline,
  SettingsOutline,
  SparklesOutline
} from '@vicons/ionicons5'
import { NIcon } from 'naive-ui'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { useUserStore } from '@/stores/user'

const appStore = useAppStore()
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

const menuOptions: MenuOption[] = [
  createMenu('/admin/dashboard', '管理工作台', GridOutline),
  createMenu('/admin/users', '用户管理', PersonOutline),
  createMenu('/admin/papers', '论文管理', BookOutline),
  createMenu('/admin/notices', '公告管理', MegaphoneOutline),
  createMenu('/admin/activities', '活动管理', PeopleOutline),
  createMenu('/admin/categories', '分类标签', ListOutline),
  createMenu('/admin/comments', '评论管理', ChatbubbleEllipsesOutline),
  createMenu('/admin/forum', '论坛管理', ChatbubblesOutline),
  createMenu('/admin/ai-logs', 'AI 日志', SparklesOutline),
  createMenu('/admin/settings', '系统信息', SettingsOutline)
]

const activePath = computed(() => route.path)

function createMenu(path: string, label: string, icon: Component): MenuOption {
  return {
    key: path,
    label,
    icon: renderIcon(icon)
  }
}

function renderIcon(icon: Component) {
  return () => h(NIcon, null, { default: () => h(icon) })
}

function onMenuChange(path: string) {
  router.push(path)
}

async function logout() {
  await userStore.logout()
  router.push('/auth/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.sider-brand {
  height: 54px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 18px;
  color: var(--primary);
  font-size: 16px;
  font-weight: 700;
}

.admin-header {
  height: 58px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  background: #ffffff;
}
</style>
