import { Table, Tag, Alert, Typography, Button } from 'antd'
import { ReloadOutlined } from '@ant-design/icons'
import { useQuery } from '@tanstack/react-query'
import { useMemo } from 'react'
import { getHomeActivityLogs } from '../../api/activityLogs'
import { errMsg } from './constants'

const { Text } = Typography

const CATEGORY_COLORS = {
  HOME:       'gold',
  DEVICE:     'blue',
  SMART_LOCK: 'orange',
  CAMERA:     'cyan',
  MEMBER:     'green',
  ROOM:       'purple',
}

const ACTION_COLORS = {
  CREATED:      'success',
  UPDATED:      'processing',
  DELETED:      'error',
  LOCKED:       'warning',
  UNLOCKED:     'success',
  ARMED:        'error',
  DISARMED:     'default',
  ADDED:        'success',
  REMOVED:      'error',
  ROLE_CHANGED: 'processing',
}

export default function ActivityLogTab({ homeId }) {
  // TODO: All logs are fetched at once and paginated client-side.
  // When log volume grows, switch to server-side pagination — pass page/size params
  // to the API and drive the table's onChange to trigger new fetches.
  const { data: logs = [], isLoading, isFetching, error, refetch } = useQuery({
    queryKey:        ['activity-logs', homeId],
    queryFn:         () => getHomeActivityLogs(homeId),
    refetchInterval: 30_000,
  })

  const categoryFilters = useMemo(
    () => [...new Set(logs.map(l => l.category))].sort().map(c => ({ text: c, value: c })),
    [logs],
  )

  const columns = [
    {
      title: 'Time',
      dataIndex: 'occurredAt',
      key: 'occurredAt',
      width: 170,
      render: (val) => new Date(val).toLocaleString(),
      defaultSortOrder: 'descend',
      sorter: (a, b) => new Date(a.occurredAt) - new Date(b.occurredAt),
    },
    {
      title: 'Category',
      dataIndex: 'category',
      key: 'category',
      width: 120,
      render: (val) => <Tag color={CATEGORY_COLORS[val] ?? 'default'}>{val}</Tag>,
      filters: categoryFilters,
      onFilter: (value, record) => record.category === value,
    },
    {
      title: 'Action',
      dataIndex: 'action',
      key: 'action',
      width: 130,
      render: (val) => <Tag color={ACTION_COLORS[val] ?? 'default'}>{val}</Tag>,
    },
    {
      title: 'Description',
      dataIndex: 'description',
      key: 'description',
    },
    {
      title: 'Actor',
      dataIndex: 'actorEmail',
      key: 'actorEmail',
      width: 220,
      render: (val) => <Text type="secondary">{val}</Text>,
    },
  ]

  if (error) return (
    <Alert type="error" showIcon
      message="Could not load activity logs"
      description={errMsg(error)}
    />
  )

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Text type="secondary">
          {logs.length} event{logs.length !== 1 ? 's' : ''} &middot; auto-refreshes every 30s
        </Text>
        <Button icon={<ReloadOutlined />} size="small" loading={isFetching} onClick={() => refetch()}>
          Refresh
        </Button>
      </div>
      <Table
        rowKey="id"
        columns={columns}
        dataSource={logs}
        loading={isLoading}
        pagination={{ pageSize: 20, showSizeChanger: true }}
        size="small"
      />
    </div>
  )
}
