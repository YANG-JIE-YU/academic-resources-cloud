export interface ApiResult<T> {
  code: number
  msg: string
  data: T
  timestamp: number
}

export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

export interface OptionItem {
  label: string
  value: string | number
}
