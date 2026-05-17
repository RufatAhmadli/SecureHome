import api from './axios'

export const getSmartLocks    = (homeId)   => api.get(`/smart-locks/home/${homeId}`).then(r => r.data)
export const createSmartLock  = (data)     => api.post('/smart-locks', data).then(r => r.data)
export const updateSmartLock  = (id, data) => api.put(`/smart-locks/${id}`, data).then(r => r.data)
export const deleteSmartLock  = (id)       => api.delete(`/smart-locks/${id}`)
export const lockDevice       = (id)       => api.patch(`/smart-locks/${id}/lock`).then(r => r.data)
export const unlockDevice     = (id)       => api.patch(`/smart-locks/${id}/unlock`).then(r => r.data)
