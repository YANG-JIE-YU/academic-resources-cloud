import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { normalizeRole } from '@/utils/auth'
import portalRoutes from '@/router/routes/portal'
import adminRoutes from '@/router/routes/admin'
import authRoutes from '@/router/routes/auth'

const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/403',
    name: 'system-403',
    component: () => import('@/views/system/ForbiddenPage.vue'),
    meta: { title: '无权限访问', hideInMenu: true }
  },
  {
    path: '/404',
    name: 'system-404',
    component: () => import('@/views/system/NotFoundPage.vue'),
    meta: { title: '页面不存在', hideInMenu: true }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/404',
    meta: { hideInMenu: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes: [portalRoutes, adminRoutes, authRoutes, ...systemRoutes],
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach(async (to) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (userStore.isLoggedIn && !userStore.profile && !to.path.startsWith('/auth/')) {
    try {
      await userStore.fetchProfile()
    } catch {
      userStore.clearLogin()
    }
  }

  if (requiresAuth && !userStore.isLoggedIn) {
    return { name: 'auth-login', query: { redirect: to.fullPath } }
  }

  const requiredRoles = to.matched.flatMap((record) => (record.meta.roles as string[] | undefined) || [])
  if (requiredRoles.length > 0) {
    const normalizedRequiredRoles = requiredRoles.map((item) => normalizeRole(item))
    if (!normalizedRequiredRoles.includes(userStore.roleCode)) {
      return { name: 'system-403' }
    }
  }

  if (to.path.startsWith('/auth/') && userStore.isLoggedIn) {
    return userStore.isAdmin ? { name: 'admin-dashboard' } : { name: 'portal-home' }
  }

  return true
})

router.afterEach((to) => {
  const title = (to.meta.title as string) || '学术资源门户系统'
  document.title = `${title} - 学术资源门户系统`
})

export default router
