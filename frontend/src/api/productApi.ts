import request from './request'

export interface Product {
  id: number
  name: string
  price: number
  stock: number
  imageUrl: string
  description: string
  status: number
  createTime: string
}

export interface PageResult<T> {
  records: T[]
  page: number
  pageSize: number
  total: number
  totalPages: number
}

export function getProducts(page: number, pageSize: number = 8) {
  return request.get<any, { success: boolean; message: string; data: PageResult<Product> }>('/products', {
    params: { page, pageSize },
  })
}

export function getProductDetail(productId: number) {
  return request.get<any, { success: boolean; message: string; data: Product }>('/products/detail', {
    params: { productId },
  })
}
