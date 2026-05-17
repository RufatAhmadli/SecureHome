import api from './axios'

export const getCameras    = (homeId)   => api.get(`/cameras/home/${homeId}`).then(r => r.data)
export const createCamera  = (data)     => api.post('/cameras', data).then(r => r.data)
export const updateCamera  = (id, data) => api.put(`/cameras/${id}`, data).then(r => r.data)
export const deleteCamera  = (id)       => api.delete(`/cameras/${id}`)
export const armCamera     = (id)       => api.patch(`/cameras/${id}/arm`).then(r => r.data)
export const disarmCamera  = (id)       => api.patch(`/cameras/${id}/disarm`).then(r => r.data)
