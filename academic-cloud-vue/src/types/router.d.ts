import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    requiresAuth?: boolean
    roles?: Array<'USER' | 'ADMIN'>
    hideInMenu?: boolean
    layout?: 'portal' | 'admin' | 'auth'
    menuKey?: string
  }
}

export {}
