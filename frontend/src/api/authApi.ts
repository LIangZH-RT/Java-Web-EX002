import request from './request'

export interface LoginUser {
  id: number
  username: string
}

export function login(username: string, password: string) {
  return request.post<any, { success: boolean; message: string; data: LoginUser }>('/auth/login', null, {
    params: { username, password },
  })
}

export function register(username: string, password: string) {
  return request.post<any, { success: boolean; message: string; data: LoginUser }>('/auth/register', null, {
    params: { username, password },
  })
}

export function logout() {
  return request.post<any, { success: boolean; message: string; data: null }>('/auth/logout')
}

export function getCurrentUser() {
  return request.get<any, { success: boolean; message: string; data: LoginUser | null }>('/auth/current')
}
