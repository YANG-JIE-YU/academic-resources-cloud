import type { RouteRecordRaw } from 'vue-router'

const portalRoutes: RouteRecordRaw = {
  path: '/',
  component: () => import('@/layouts/PortalLayout.vue'),
  meta: { layout: 'portal' },
  children: [
    {
      path: '',
      name: 'portal-home',
      component: () => import('@/views/portal/home/HomePage.vue'),
      meta: { title: '首页', menuKey: 'home' }
    },
    {
      path: 'notices',
      name: 'portal-notice-list',
      component: () => import('@/views/portal/notice/NoticeListPage.vue'),
      meta: { title: '公告列表', menuKey: 'notices' }
    },
    {
      path: 'notices/:id',
      name: 'portal-notice-detail',
      component: () => import('@/views/portal/notice/NoticeDetailPage.vue'),
      meta: { title: '公告详情', hideInMenu: true }
    },
    {
      path: 'activities',
      name: 'portal-activity-list',
      component: () => import('@/views/portal/activity/ActivityListPage.vue'),
      meta: { title: '学术活动', menuKey: 'activities' }
    },
    {
      path: 'activities/:id',
      name: 'portal-activity-detail',
      component: () => import('@/views/portal/activity/ActivityDetailPage.vue'),
      meta: { title: '活动详情', hideInMenu: true }
    },
    {
      path: 'papers',
      name: 'portal-paper-list',
      component: () => import('@/views/portal/paper/PaperListPage.vue'),
      meta: { title: '论文资源', menuKey: 'papers' }
    },
    {
      path: 'papers/:id',
      name: 'portal-paper-detail',
      component: () => import('@/views/portal/paper/PaperDetailPage.vue'),
      meta: { title: '论文详情', hideInMenu: true }
    },
    {
      path: 'search',
      name: 'portal-search',
      component: () => import('@/views/portal/search/SearchResultPage.vue'),
      meta: { title: '搜索结果', hideInMenu: true }
    },
    {
      path: 'forum',
      name: 'portal-forum',
      component: () => import('@/views/portal/forum/ForumPage.vue'),
      meta: { title: '论坛交流', menuKey: 'forum' }
    },
    {
      path: 'profile',
      name: 'portal-profile',
      component: () => import('@/views/portal/profile/ProfilePage.vue'),
      meta: { title: '个人中心', requiresAuth: true, roles: ['USER', 'ADMIN'], menuKey: 'profile' }
    },
    {
      path: 'favorites',
      name: 'portal-favorites',
      component: () => import('@/views/portal/profile/FavoritesPage.vue'),
      meta: { title: '我的收藏', requiresAuth: true, roles: ['USER', 'ADMIN'], hideInMenu: true }
    },
    {
      path: 'my-comments',
      name: 'portal-my-comments',
      component: () => import('@/views/portal/profile/MyCommentsPage.vue'),
      meta: { title: '我的评论', requiresAuth: true, roles: ['USER', 'ADMIN'], hideInMenu: true }
    },
    {
      path: 'my-forum',
      name: 'portal-my-forum',
      component: () => import('@/views/portal/profile/MyForumPage.vue'),
      meta: { title: '我的论坛', requiresAuth: true, roles: ['USER', 'ADMIN'], hideInMenu: true }
    },
    {
      path: 'ai/chat',
      name: 'portal-ai-chat',
      component: () => import('@/views/portal/ai/AiChatPage.vue'),
      meta: { title: 'AI 对话', requiresAuth: true, roles: ['USER', 'ADMIN'], menuKey: 'ai' }
    },
    {
      path: 'ai/summary',
      name: 'portal-ai-summary',
      component: () => import('@/views/portal/ai/AiSummaryPage.vue'),
      meta: { title: '摘要生成', requiresAuth: true, roles: ['USER', 'ADMIN'], hideInMenu: true }
    },
    {
      path: 'ai/keywords',
      name: 'portal-ai-keywords',
      component: () => import('@/views/portal/ai/AiKeywordsPage.vue'),
      meta: { title: '关键词提取', requiresAuth: true, roles: ['USER', 'ADMIN'], hideInMenu: true }
    },
    {
      path: 'ai/recommend',
      name: 'portal-ai-recommend',
      component: () => import('@/views/portal/ai/AiRecommendPage.vue'),
      meta: { title: '智能推荐', requiresAuth: true, roles: ['USER', 'ADMIN'] }
    }
  ]
}

export default portalRoutes
