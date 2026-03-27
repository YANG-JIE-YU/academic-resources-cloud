import type { RouteRecordRaw } from 'vue-router'

const authRoutes: RouteRecordRaw = {
  path: '/auth',
  component: () => import('@/layouts/AuthLayout.vue'),
  meta: { layout: 'auth' },
  redirect: '/auth/login',
  children: [
    {
      path: 'login',
      name: 'auth-login',
      component: () => import('@/views/auth/login/LoginPage.vue'),
      meta: { title: '登录', hideInMenu: true }
    },
    {
      path: 'register',
      name: 'auth-register',
      component: () => import('@/views/auth/register/RegisterPage.vue'),
      meta: { title: '注册', hideInMenu: true }
    }
  ]
}

export default authRoutes
