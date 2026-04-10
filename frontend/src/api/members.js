import api from './axios'

export const getMembers      = (homeId)                   => api.get(`/homes/${homeId}/members`).then(r => r.data)
export const addMember       = (homeId, data)             => api.post(`/homes/${homeId}/members/addMember`, data).then(r => r.data)
export const updateMember    = (homeId, memberId, data)   => api.put(`/homes/${homeId}/members/${memberId}`, data).then(r => r.data)
export const deleteMember    = (homeId, memberId)         => api.delete(`/homes/${homeId}/members/${memberId}`)
