import { defineStore } from 'pinia'

interface AppState {
  adminSiderCollapsed: boolean
  portalKeyword: string
}

export const useAppStore = defineStore('app', {
  state: (): AppState => ({
    adminSiderCollapsed: false,
    portalKeyword: ''
  }),
  actions: {
    toggleAdminSider() {
      this.adminSiderCollapsed = !this.adminSiderCollapsed
    },
    setPortalKeyword(keyword: string) {
      this.portalKeyword = keyword
    }
  }
})
