import api from './axios'

export const getRooms    = (homeId)       => api.get(`/homes/${homeId}/rooms`).then(r => r.data)
export const createRoom  = (data)         => api.post('/rooms', data).then(r => r.data)
export const updateRoom  = (id, data)     => api.put(`/rooms/${id}`, data).then(r => r.data)
export const deleteRoom  = (id)           => api.delete(`/rooms/${id}`)
