import api from './axios'

export const getHomeActivityLogs = (homeId) =>
  api.get(`/homes/${homeId}/activity-logs`).then(r => r.data)

export const getSecurityLogs = () =>
  api.get('/activity-logs/security').then(r => r.data)
