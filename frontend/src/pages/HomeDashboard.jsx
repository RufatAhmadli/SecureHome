import { Typography, Button, Tabs, Spin, Alert, Breadcrumb } from 'antd'
import { HomeOutlined, ArrowLeftOutlined, AppstoreOutlined, TeamOutlined, KeyOutlined, HistoryOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { getHome } from '../api/homes'
import { getMyMemberships } from '../api/members'
import RoomsTab       from '../components/home/RoomsTab'
import MembersTab     from '../components/home/MembersTab'
import DevicesTab     from '../components/home/DevicesTab'
import ActivityLogTab from '../components/home/ActivityLogTab'
import { errMsg } from '../components/home/constants'
import useAuthStore from '../store/authStore'

const { Title, Text } = Typography

export default function HomeDashboard() {
  const { homeId } = useParams()
  const navigate   = useNavigate()
  const id         = Number(homeId)
  const user       = useAuthStore(s => s.user)

  const { data: home, isLoading, error } = useQuery({
    queryKey: ['home', id],
    queryFn:  () => getHome(id),
  })

  const { data: myMemberships = [] } = useQuery({
    queryKey: ['my-memberships'],
    queryFn:  getMyMemberships,
  })

  const myRole      = myMemberships.find(m => m.homeId === id)?.role
  const canViewLogs = myRole === 'OWNER' || myRole === 'ADMIN'

  if (isLoading) return <div style={styles.center}><Spin size="large" /></div>

  if (error) return (
    <div style={styles.center}>
      <Alert
        type="error"
        message="Could not load home"
        description={errMsg(error, 'You may not have access to this home.')}
        showIcon
      />
    </div>
  )

  const tabs = [
    { key: 'rooms',   label: <span><AppstoreOutlined /> Rooms</span>,   children: <RoomsTab   homeId={id} myRole={myRole} /> },
    { key: 'members', label: <span><TeamOutlined />     Members</span>, children: <MembersTab homeId={id} myRole={myRole} /> },
    { key: 'devices', label: <span><KeyOutlined />      Devices</span>, children: <DevicesTab homeId={id} myRole={myRole} /> },
    ...(canViewLogs ? [{ key: 'activity', label: <span><HistoryOutlined /> Activity Log</span>, children: <ActivityLogTab homeId={id} /> }] : []),
  ]

  return (
    <div style={styles.page}>
      <header style={styles.header}>
        <div style={styles.headerLeft}>
          <Button type="text" icon={<ArrowLeftOutlined />} onClick={() => navigate('/')} />
          <HomeOutlined style={{ fontSize: 20, color: '#1677ff' }} />
          <Title level={4} style={{ margin: 0, color: '#1677ff' }}>SecureHome</Title>
        </div>
      </header>

      <main style={styles.main}>
        <Breadcrumb
          style={{ marginBottom: 20 }}
          items={[
            { title: <Link to="/">My Homes</Link> },
            { title: home.name },
          ]}
        />

        <Title level={3} style={{ margin: '0 0 4px' }}>{home.name}</Title>
        {home.city && (
          <Text type="secondary" style={{ marginBottom: 24, display: 'block' }}>
            {[home.address, home.city].filter(Boolean).join(', ')}
          </Text>
        )}

        <Tabs defaultActiveKey="rooms" size="large" items={tabs} />
      </main>
    </div>
  )
}

const styles = {
  page:      { minHeight: '100vh', background: '#f0f2f5' },
  header:    { display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 32px', height: 64, background: '#fff', borderBottom: '1px solid #f0f0f0', position: 'sticky', top: 0, zIndex: 100 },
  headerLeft:{ display: 'flex', alignItems: 'center', gap: 10 },
  main:      { maxWidth: 1200, margin: '0 auto', padding: '32px 24px' },
  center:    { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' },
}
