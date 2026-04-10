import api from './axios'

export const getCameras    = (homeId)   => api.get(`/cameras/home/${homeId}`).then(r => r.data)
export const createCamera  = (data)     => api.post('/cameras', data).then(r => r.data)
export const updateCamera  = (id, data) => api.put(`/cameras/${id}`, data).then(r => r.data)
export const deleteCamera  = (id)       => api.delete(`/cameras/${id}`)
