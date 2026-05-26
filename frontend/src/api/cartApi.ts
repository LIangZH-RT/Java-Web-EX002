import request from './request'
import type { PageResult } from './productApi'

export interface CartItem {
  id: number
  userId: number
  productId: number
  productName: string
  productPrice: number
  productImage: string
  quantity: number
  selected: boolean
  stock: number
  createTime: string
  updateTime: string
}

export function getCartList(page: number, pageSize: number = 8) {
  return request.get<any, { success: boolean; message: string; data: PageResult<CartItem> }>('/cart/list', {
    params: { page, pageSize },
  })
}

export function addToCart(productId: number, quantity: number = 1) {
  return request.post<any, { success: boolean; message: string; data: any }>('/cart/add', null, {
    params: { productId, quantity },
  })
}

export function updateCartItem(cartItemId: number, quantity: number) {
  return request.post<any, { success: boolean; message: string; data: any }>('/cart/update', null, {
    params: { cartItemId, quantity },
  })
}

export function updateCartSelected(cartItemId: number, selected: boolean) {
  return request.post<any, { success: boolean; message: string; data: any }>('/cart/select', null, {
    params: { cartItemId, selected },
  })
}

export function deleteCartItem(cartItemId: number) {
  return request.post<any, { success: boolean; message: string; data: any }>('/cart/delete', null, {
    params: { cartItemId },
  })
}
