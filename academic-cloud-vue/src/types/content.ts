import type { PageData } from '@/types/common'

export interface PaperCategory {
  id: number
  name: string
  sortNo?: number
  status?: number
  createTime?: string
}

export interface PaperItem {
  id: number
  title: string
  abstractText?: string
  content?: string
  keywords?: string
  authorInfo?: string
  publishTime?: string
  coverUrl?: string
  categoryId: number
  status?: number
  viewCount?: number
  favoriteCount?: number
  commentCount?: number
  createTime?: string
  updateTime?: string
}

export interface PaperQuery {
  current?: number
  size?: number
  keyword?: string
  categoryId?: number
}

export interface PaperSaveRequest {
  title: string
  abstractText: string
  content: string
  keywords: string
  authorInfo?: string
  coverUrl?: string
  categoryId: number
  publishTime?: string
}

export interface CategorySaveRequest {
  name: string
  sortNo?: number
  status?: number
}

export type PaperPageData = PageData<PaperItem>

export interface NoticeItem {
  id: number
  title: string
  summary: string
  content: string
  publishTime: string
  status?: number
  createTime?: string
  updateTime?: string
}

export interface NoticeSaveRequest {
  title: string
  summary?: string
  content: string
  publishTime?: string
  status?: number
}

export interface ActivityItem {
  id: number
  title: string
  location: string
  organizer: string
  startTime: string
  endTime: string
  content: string
  status?: number
  createTime?: string
  updateTime?: string
}

export interface ActivitySaveRequest {
  title: string
  location?: string
  organizer?: string
  startTime: string
  endTime: string
  content: string
  status?: number
}
