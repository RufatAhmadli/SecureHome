import api from './axios'

export const login = (data) => api.post('/auth/login', data).then((r) => r.data)
export const register = (data) => api.post('/auth/register', data).then((r) => r.data)
export const getMe = () => api.get('/auth/me').then((r) => r.data)
export const updateMyName = (data) => api.patch('/auth/me', data).then((r) => r.data)
export const changePassword = (data) => api.patch('/auth/me/password', data)
export const deleteMyAccount = () => api.delete('/auth/me')
