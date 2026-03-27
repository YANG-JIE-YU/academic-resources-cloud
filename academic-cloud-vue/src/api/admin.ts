import {
  createAdminUser,
  deleteAdminUser,
  fetchAdminUsers,
  fetchComments,
  fetchForumPosts,
  fetchForumReplies,
  replyComment,
  updateAdminUser,
  updateAdminUserRole,
  updateAdminUserStatus
} from '@/api/user'
import {
  createActivity,
  createNotice,
  createPaper,
  createPaperCategory,
  deleteActivity,
  deleteNotice,
  deletePaper,
  deletePaperCategory,
  fetchAdminActivityList,
  fetchAdminNoticeList,
  fetchAdminPaperCategories,
  fetchPaperPage,
  updateActivity,
  updateNotice,
  updatePaper,
  updatePaperCategory
} from '@/api/content'
import type {
  ActivitySaveRequest,
  CategorySaveRequest,
  NoticeSaveRequest,
  PaperQuery,
  PaperSaveRequest
} from '@/types/content'
import type { AdminCommentReplyRequest, AdminUserCreateRequest, AdminUserUpdateRequest, UserRoleCode } from '@/types/user'

export const adminApi = {
  fetchUsers: fetchAdminUsers,
  createUser: (data: AdminUserCreateRequest) => createAdminUser(data),
  updateUser: (userId: number, data: AdminUserUpdateRequest) => updateAdminUser(userId, data),
  deleteUser: (userId: number) => deleteAdminUser(userId),
  updateUserStatus: (userId: number, status: number) => updateAdminUserStatus(userId, status),
  updateUserRole: (userId: number, role: UserRoleCode) => updateAdminUserRole(userId, role),

  fetchPapers: (params: PaperQuery) => fetchPaperPage(params),
  createPaper: (data: PaperSaveRequest) => createPaper(data),
  updatePaper: (id: number, data: PaperSaveRequest) => updatePaper(id, data),
  deletePaper: (id: number) => deletePaper(id),

  fetchCategories: () => fetchAdminPaperCategories(),
  createCategory: (data: CategorySaveRequest) => createPaperCategory(data),
  updateCategory: (id: number, data: CategorySaveRequest) => updatePaperCategory(id, data),
  deleteCategory: (id: number) => deletePaperCategory(id),

  fetchNotices: (keyword?: string) => fetchAdminNoticeList(keyword),
  createNotice: (data: NoticeSaveRequest) => createNotice(data),
  updateNotice: (id: number, data: NoticeSaveRequest) => updateNotice(id, data),
  deleteNotice: (id: number) => deleteNotice(id),

  fetchActivities: (keyword?: string) => fetchAdminActivityList(keyword),
  createActivity: (data: ActivitySaveRequest) => createActivity(data),
  updateActivity: (id: number, data: ActivitySaveRequest) => updateActivity(id, data),
  deleteActivity: (id: number) => deleteActivity(id),

  fetchComments: (targetType: string, targetId: number) => fetchComments({ targetType, targetId }),
  replyComment: (commentId: number, data: AdminCommentReplyRequest) => replyComment(commentId, data),

  fetchForumPosts: () => fetchForumPosts(),
  fetchForumReplies: (parentId: number) => fetchForumReplies(parentId)
}
