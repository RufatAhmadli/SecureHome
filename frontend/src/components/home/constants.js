export const canManage  = (role) => role === 'OWNER' || role === 'ADMIN'
export const canOperate = (role) => role === 'OWNER' || role === 'ADMIN' || role === 'MEMBER'

export const PROTOCOLS    = ['WIFI', 'ZIGBEE', 'MQTT', 'BLUETOOTH', 'WEBSOCKET']
export const MEMBER_ROLES = ['OWNER', 'ADMIN', 'MEMBER', 'GUEST']
export const ROLE_COLORS  = { OWNER: 'gold', ADMIN: 'blue', MEMBER: 'green', GUEST: 'default' }
export const LOCK_COLORS  = { LOCKED: 'error', UNLOCKED: 'success', JAMMED: 'warning' }

export const errMsg = (err, fallback = 'Operation failed') =>
  err?.response?.data?.message || fallback

export const tabHeader  = { display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16, flexWrap: 'wrap', gap: 12 }
export const formFooter = { display: 'flex', justifyContent: 'flex-end', gap: 8, marginTop: 8 }
