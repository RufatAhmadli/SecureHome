import {
  Typography, Button, Card, Row, Col, Modal, Form, Input,
  Dropdown, Avatar, Tag, Empty, Spin, message, Popconfirm, Tooltip,
} from 'antd'
import {
  PlusOutlined, HomeOutlined, EditOutlined, DeleteOutlined,
  EnvironmentOutlined, LogoutOutlined, UserOutlined, EllipsisOutlined,
  ArrowRightOutlined,
} from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getHomes, createHome, updateHome, deleteHome } from '../api/homes'
import useAuthStore from '../store/authStore'

const { Title, Text } = Typography

const TIMEZONE_OPTIONS = [
  'UTC', 'Europe/Baku', 'Europe/London', 'Europe/Berlin',
  'America/New_York', 'America/Los_Angeles', 'Asia/Tokyo',
]

export default function Dashboard() {
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { user, logout } = useAuthStore()
  const [messageApi, contextHolder] = message.useMessage()

  const [modalOpen, setModalOpen] = useState(false)
  const [editingHome, setEditingHome] = useState(null)
  const [form] = Form.useForm()

  const { data: homes = [], isLoading } = useQuery({
    queryKey: ['homes'],
    queryFn: getHomes,
  })

  const createMutation = useMutation({
    mutationFn: createHome,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['homes'] })
      messageApi.success('Home created')
      closeModal()
    },
    onError: (err) => messageApi.error(err.response?.data?.message || 'Failed to create home'),
  })

  const updateMutation = useMutation({
    mutationFn: ({ id, data }) => updateHome(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['homes'] })
      messageApi.success('Home updated')
      closeModal()
    },
    onError: (err) => messageApi.error(err.response?.data?.message || 'Failed to update home'),
  })

  const deleteMutation = useMutation({
    mutationFn: deleteHome,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['homes'] })
      messageApi.success('Home removed')
    },
    onError: (err) => messageApi.error(err.response?.data?.message || 'Failed to delete home'),
  })

  const openCreate = () => {
    setEditingHome(null)
    form.resetFields()
    setModalOpen(true)
  }

  const openEdit = (home) => {
    setEditingHome(home)
    form.setFieldsValue(home)
    setModalOpen(true)
  }

  const closeModal = () => {
    setModalOpen(false)
    setEditingHome(null)
    form.resetFields()
  }

  const handleSubmit = (values) => {
    if (editingHome) {
      updateMutation.mutate({ id: editingHome.id, data: values })
    } else {
      createMutation.mutate(values)
    }
  }

  const userMenuItems = [
    { key: 'email', label: <Text type="secondary">{user?.email}</Text>, disabled: true },
    { type: 'divider' },
    {
      key: 'logout',
      label: 'Sign out',
      icon: <LogoutOutlined />,
      danger: true,
      onClick: () => { logout(); navigate('/login') },
    },
  ]

  return (
    <div style={styles.page}>
      {contextHolder}

      {/* Header */}
      <header style={styles.header}>
        <div style={styles.headerLeft}>
          <HomeOutlined style={{ fontSize: 22, color: '#1677ff' }} />
          <Title level={4} style={{ margin: 0, color: '#1677ff' }}>SecureHome</Title>
        </div>
        <Dropdown menu={{ items: userMenuItems }} placement="bottomRight" trigger={['click']}>
          <Avatar
            style={{ background: '#1677ff', cursor: 'pointer' }}
            icon={<UserOutlined />}
          />
        </Dropdown>
      </header>

      {/* Body */}
      <main style={styles.main}>
        <div style={styles.titleRow}>
          <div>
            <Title level={3} style={{ margin: 0 }}>My Homes</Title>
            <Text type="secondary">Manage your smart home environments</Text>
          </div>
          <Button type="primary" icon={<PlusOutlined />} onClick={openCreate} size="large">
            Add Home
          </Button>
        </div>

        {isLoading ? (
          <div style={styles.center}><Spin size="large" /></div>
        ) : homes.length === 0 ? (
          <div style={styles.center}>
            <Empty
              image={Empty.PRESENTED_IMAGE_SIMPLE}
              description={
                <span>
                  No homes yet.{' '}
                  <a onClick={openCreate}>Add your first home</a>
                </span>
              }
            />
          </div>
        ) : (
          <Row gutter={[20, 20]}>
            {homes.map((home) => (
              <Col xs={24} sm={12} lg={8} xl={6} key={home.id}>
                <HomeCard
                  home={home}
                  onEdit={() => openEdit(home)}
                  onDelete={() => deleteMutation.mutate(home.id)}
                  onOpen={() => navigate(`/homes/${home.id}`)}
                />
              </Col>
            ))}
          </Row>
        )}
      </main>

      {/* Create / Edit Modal */}
      <Modal
        title={editingHome ? 'Edit Home' : 'Add New Home'}
        open={modalOpen}
        onCancel={closeModal}
        footer={null}
        width={500}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={handleSubmit} style={{ marginTop: 16 }}>
          <Form.Item name="name" label="Home name" rules={[{ required: true, message: 'Required' }]}>
            <Input placeholder="e.g. Main Residence" />
          </Form.Item>

          <Form.Item name="address" label="Address" rules={[{ required: true, message: 'Required' }]}>
            <Input placeholder="123 Main St" />
          </Form.Item>

          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item name="city" label="City" style={{ flex: 1 }} rules={[{ required: true, message: 'Required' }]}>
              <Input placeholder="Baku" />
            </Form.Item>
            <Form.Item name="timezone" label="Timezone" style={{ flex: 1 }}>
              <Input placeholder="Europe/Baku" list="timezones" />
              <datalist id="timezones">
                {TIMEZONE_OPTIONS.map((tz) => <option key={tz} value={tz} />)}
              </datalist>
            </Form.Item>
          </div>

          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} placeholder="Optional notes about this home" />
          </Form.Item>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
            <Button onClick={closeModal}>Cancel</Button>
            <Button
              type="primary"
              htmlType="submit"
              loading={createMutation.isPending || updateMutation.isPending}
            >
              {editingHome ? 'Save Changes' : 'Create Home'}
            </Button>
          </div>
        </Form>
      </Modal>
    </div>
  )
}

function HomeCard({ home, onEdit, onDelete, onOpen }) {
  const menuItems = [
    { key: 'edit', label: 'Edit', icon: <EditOutlined />, onClick: onEdit },
    {
      key: 'delete',
      label: (
        <Popconfirm
          title="Remove this home?"
          description="This will remove the home and its memberships."
          onConfirm={onDelete}
          okText="Remove"
          okButtonProps={{ danger: true }}
        >
          <span style={{ color: '#ff4d4f' }}>
            <DeleteOutlined style={{ marginRight: 6 }} />
            Delete
          </span>
        </Popconfirm>
      ),
    },
  ]

  return (
    <Card
      style={styles.homeCard}
      styles={{ body: { padding: '20px 20px 16px' } }}
      hoverable
    >
      {/* Top row: icon + menu */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div style={styles.homeIcon}>
          <HomeOutlined style={{ fontSize: 20, color: '#1677ff' }} />
        </div>
        <Dropdown menu={{ items: menuItems }} trigger={['click']} placement="bottomRight">
          <Button
            type="text"
            icon={<EllipsisOutlined />}
            size="small"
            onClick={(e) => e.stopPropagation()}
          />
        </Dropdown>
      </div>

      {/* Name */}
      <Title level={5} style={{ margin: '12px 0 4px', lineHeight: 1.3 }}>
        {home.name}
      </Title>

      {/* Location */}
      {(home.address || home.city) && (
        <div style={{ display: 'flex', alignItems: 'center', gap: 4, marginBottom: 8 }}>
          <EnvironmentOutlined style={{ color: '#8c8c8c', fontSize: 12 }} />
          <Text type="secondary" style={{ fontSize: 12 }}>
            {[home.address, home.city].filter(Boolean).join(', ')}
          </Text>
        </div>
      )}

      {/* Description */}
      {home.description && (
        <Text
          type="secondary"
          style={{ fontSize: 13, display: 'block', marginBottom: 8 }}
          ellipsis={{ tooltip: home.description }}
        >
          {home.description}
        </Text>
      )}

      {/* Timezone tag */}
      {home.timezone && (
        <Tag color="blue" style={{ marginBottom: 16, fontSize: 11 }}>
          {home.timezone}
        </Tag>
      )}

      {/* Open button */}
      <Button
        type="primary"
        ghost
        block
        icon={<ArrowRightOutlined />}
        onClick={onOpen}
        style={{ marginTop: 4 }}
      >
        Open
      </Button>
    </Card>
  )
}

const styles = {
  page: {
    minHeight: '100vh',
    background: '#f0f2f5',
  },
  header: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '0 32px',
    height: 64,
    background: '#fff',
    borderBottom: '1px solid #f0f0f0',
    position: 'sticky',
    top: 0,
    zIndex: 100,
  },
  headerLeft: {
    display: 'flex',
    alignItems: 'center',
    gap: 10,
  },
  main: {
    maxWidth: 1200,
    margin: '0 auto',
    padding: '32px 24px',
  },
  titleRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
    marginBottom: 28,
    flexWrap: 'wrap',
    gap: 12,
  },
  center: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 300,
  },
  homeCard: {
    borderRadius: 12,
    border: '1px solid #f0f0f0',
    boxShadow: '0 2px 8px rgba(0,0,0,0.04)',
  },
  homeIcon: {
    width: 40,
    height: 40,
    borderRadius: 10,
    background: '#e6f4ff',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
}
