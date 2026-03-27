import type { PaperCategory, PaperItem } from '@/types/content'
import type { CommentItem, ForumPostItem, UserInfo } from '@/types/user'

export interface DashboardSummary {
  userCount: number
  paperCount: number
  noticeCount: number
  activityCount: number
}

export interface AdminUserItem extends UserInfo {}
export interface AdminPaperItem extends PaperItem {}
export interface AdminCategoryItem extends PaperCategory {}
export interface AdminCommentItem extends CommentItem {}
export interface AdminForumItem extends ForumPostItem {}
