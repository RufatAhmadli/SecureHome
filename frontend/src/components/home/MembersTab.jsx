import { Typography, Button, Table, Modal, Form, Input, Select, Tag, Space, Tooltip, Popconfirm, Alert } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined, UserOutlined } from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { getMembers, addMember, updateMember, deleteMember } from '../../api/members'
import { errMsg, tabHeader, formFooter, MEMBER_ROLES, ROLE_COLORS } from './constants'

const { Text } = Typography

export default function MembersTab({ homeId }) {
  const qc = useQueryClient()
  const [addOpen, setAddOpen]   = useState(false)
  const [roleOpen, setRoleOpen] = useState(false)
  const [editing, setEditing]   = useState(null)
  const [error, setError]       = useState(null)
  const [form]     = Form.useForm()
  const [roleForm] = Form.useForm()

  const { data: members = [], isLoading } = useQuery({
    queryKey: ['members', homeId],
    queryFn:  () => getMembers(homeId),
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['members', homeId] })

  const addMut = useMutation({
    mutationFn: (data) => addMember(homeId, data),
    onSuccess:  () => { invalidate(); setAddOpen(false); form.resetFields(); setError(null) },
    onError:    (e) => setError(errMsg(e, 'Failed to add member')),
  })

  const updateMut = useMutation({
    mutationFn: ({ memberId, role }) => updateMember(homeId, memberId, { role }),
    onSuccess:  () => { invalidate(); setRoleOpen(false); roleForm.resetFields(); setError(null) },
    onError:    (e) => setError(errMsg(e, 'Failed to update role')),
  })

  const deleteMut = useMutation({
    mutationFn: (memberId) => deleteMember(homeId, memberId),
    onSuccess:  invalidate,
    onError:    (e) => setError(errMsg(e, 'Failed to remove member')),
  })

  const openRoleEdit = (m) => {
    setEditing(m); roleForm.setFieldsValue({ role: m.role }); setError(null); setRoleOpen(true)
  }

  const columns = [
    {
      title: 'Member', key: 'user',
      render: (_, row) => (
        <Space>
          <UserOutlined style={{ color: '#8c8c8c' }} />
          <div>
            <Text strong>{row.userFirstName} {row.userLastName}</Text>
            <br />
            <Text type="secondary" style={{ fontSize: 12 }}>{row.userEmail}</Text>
          </div>
        </Space>
      ),
      sorter: (a, b) => `${a.userFirstName}${a.userLastName}`.localeCompare(`${b.userFirstName}${b.userLastName}`),
    },
    {
      title: 'Role', dataIndex: 'role', key: 'role', width: 120,
      render: role => <Tag color={ROLE_COLORS[role] || 'default'}>{role}</Tag>,
      filters: MEMBER_ROLES.map(r => ({ text: r, value: r })),
      onFilter: (value, record) => record.role === value,
    },
    {
      title: 'Actions', key: 'actions', width: 100,
      render: (_, row) => (
        <Space>
          <Tooltip title="Change role"><Button type="text" icon={<EditOutlined />} onClick={() => openRoleEdit(row)} /></Tooltip>
          <Popconfirm title="Remove this member?" onConfirm={() => deleteMut.mutate(row.id)} okText="Remove" okButtonProps={{ danger: true }}>
            <Tooltip title="Remove"><Button type="text" danger icon={<DeleteOutlined />} /></Tooltip>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} closable onClose={() => setError(null)} />}

      <div style={tabHeader}>
        <Text type="secondary">{members.length} member{members.length !== 1 ? 's' : ''}</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => { setError(null); form.resetFields(); setAddOpen(true) }}>Add Member</Button>
      </div>

      <Table dataSource={members} columns={columns} rowKey="id" loading={isLoading} pagination={false} locale={{ emptyText: 'No members yet' }} />

      {/* Add member modal */}
      <Modal title="Add Member" open={addOpen} onCancel={() => { setAddOpen(false); setError(null) }} footer={null} destroyOnClose>
        {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
        <Text type="secondary" style={{ display: 'block', marginBottom: 16, fontSize: 13 }}>
          Enter the user's email address and assign a role.
        </Text>
        <Form form={form} layout="vertical" onFinish={v => addMut.mutate(v)}>
          <Form.Item name="email" label="Email address" rules={[{ required: true, type: 'email', message: 'Enter a valid email' }]}>
            <Input placeholder="user@example.com" />
          </Form.Item>
          <Form.Item name="role" label="Role" rules={[{ required: true, message: 'Required' }]}>
            <Select options={MEMBER_ROLES.map(r => ({ value: r, label: r }))} placeholder="Select role" />
          </Form.Item>
          <div style={formFooter}>
            <Button onClick={() => { setAddOpen(false); setError(null) }}>Cancel</Button>
            <Button type="primary" htmlType="submit" loading={addMut.isPending}>Add</Button>
          </div>
        </Form>
      </Modal>

      {/* Change role modal */}
      <Modal title="Change Role" open={roleOpen} onCancel={() => { setRoleOpen(false); setError(null) }} footer={null} destroyOnClose>
        {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
        <Form form={roleForm} layout="vertical" onFinish={v => updateMut.mutate({ memberId: editing.id, role: v.role })}>
          <Form.Item name="role" label="New Role" rules={[{ required: true }]}>
            <Select options={MEMBER_ROLES.map(r => ({ value: r, label: r }))} />
          </Form.Item>
          <div style={formFooter}>
            <Button onClick={() => { setRoleOpen(false); setError(null) }}>Cancel</Button>
            <Button type="primary" htmlType="submit" loading={updateMut.isPending}>Save</Button>
          </div>
        </Form>
      </Modal>
    </div>
  )
}
