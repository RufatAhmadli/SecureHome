import api from './axios'

// Returns null (204) if the user has no profile yet.
export const getMyProfile    = () => api.get('/profiles/me').then(r => r.status === 204 ? null : r.data)
export const createMyProfile = (data) => api.post('/profiles/me', data).then(r => r.data)
export const updateMyProfile = (data) => api.put('/profiles/me', data).then(r => r.data)
