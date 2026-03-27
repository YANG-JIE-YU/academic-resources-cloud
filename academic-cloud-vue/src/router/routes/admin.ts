import type { RouteRecordRaw } from 'vue-router'

const adminRoutes: RouteRecordRaw = {
  path: '/admin',
  component: () => import('@/layouts/AdminLayout.vue'),
  meta: { layout: 'admin', requiresAuth: true, roles: ['ADMIN'] },
  redirect: '/admin/dashboard',
  children: [
    {
      path: 'dashboard',
      name: 'admin-dashboard',
      component: () => import('@/views/admin/dashboard/AdminDashboardPage.vue'),
      meta: { title: '管理工作台', requiresAuth: true, roles: ['ADMIN'], menuKey: 'dashboard' }
    },
    {
      path: 'users',
      name: 'admin-user',
      component: () => import('@/views/admin/user/AdminUserManagePage.vue'),
      meta: { title: '用户管理', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'papers',
      name: 'admin-paper',
      component: () => import('@/views/admin/paper/AdminPaperManagePage.vue'),
      meta: { title: '论文管理', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'notices',
      name: 'admin-notice',
      component: () => import('@/views/admin/notice/AdminNoticeManagePage.vue'),
      meta: { title: '公告管理', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'activities',
      name: 'admin-activity',
      component: () => import('@/views/admin/activity/AdminActivityManagePage.vue'),
      meta: { title: '活动管理', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'categories',
      name: 'admin-category',
      component: () => import('@/views/admin/category/AdminCategoryManagePage.vue'),
      meta: { title: '分类标签', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'comments',
      name: 'admin-comment',
      component: () => import('@/views/admin/comment/AdminCommentManagePage.vue'),
      meta: { title: '评论管理', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'forum',
      name: 'admin-forum',
      component: () => import('@/views/admin/forum/AdminForumManagePage.vue'),
      meta: { title: '论坛管理', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'ai-logs',
      name: 'admin-ai-logs',
      component: () => import('@/views/admin/ai/AdminAiLogPage.vue'),
      meta: { title: 'AI 日志', requiresAuth: true, roles: ['ADMIN'] }
    },
    {
      path: 'settings',
      name: 'admin-settings',
      component: () => import('@/views/admin/settings/AdminSystemInfoPage.vue'),
      meta: { title: '系统信息', requiresAuth: true, roles: ['ADMIN'] }
    }
  ]
}

export default adminRoutes
