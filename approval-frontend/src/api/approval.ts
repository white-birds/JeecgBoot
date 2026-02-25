import { http } from '@/utils/http'

export interface ApprovalItem {
  id?: string
  title: string
  type: string
  content: string
  applicant: string
  currentApprover: string
  status: string
  createTime?: string
  updateTime?: string
}

export interface ApprovalRecord {
  id?: string
  operator: string
  action: string
  comment: string
  updateTime?: string
}

export interface PageParams {
  pageNo: number
  pageSize: number
  [key: string]: any
}

export interface PageResult<T> {
  records: T[]
  total: number
}

// 获取审批列表
export const getApprovalList = (params: PageParams) => {
  return http.get<PageResult<ApprovalItem>>('/approval/appProject/list', { params })
}

// 新增审批
export const addApproval = (data: ApprovalItem) => {
  return http.post('/approval/appProject/add', data)
}

// 编辑审批
export const editApproval = (data: ApprovalItem) => {
  return http.post('/approval/appProject/edit', data)
}

// 删除审批
export const deleteApproval = (id: string) => {
  return http.delete(`/approval/appProject/delete?id=${id}`)
}

// 查询审批记录
export const getApprovalRecords = (id: string) => {
  return http.get<ApprovalRecord[]>(`/approval/appProject/queryAppRecordByMainId?id=${id}`)
}

// 审批操作（通过/驳回）
export const approveAction = (data: ApprovalItem & { appRecordList: ApprovalRecord[] }) => {
  return http.post('/approval/appProject/edit', data)
}

// 批量删除审批
export const deleteBatchApproval = (ids: string) => {
  return http.delete(`/approval/appProject/deleteBatch?ids=${ids}`)
}

// 删除了 getStatistics，直接用 getApprovalList 查询全部数据，前端自己统计

