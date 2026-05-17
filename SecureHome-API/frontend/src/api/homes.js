import api from './axios'

export const getHomes = () => api.get('/homes').then((r) => r.data)
export const getHome = (id) => api.get(`/homes/${id}`).then((r) => r.data)
export const createHome = (data) => api.post('/homes', data).then((r) => r.data)
export const updateHome = (id, data) => api.put(`/homes/${id}`, data).then((r) => r.data)
export const deleteHome = (id) => api.delete(`/homes/${id}`)
