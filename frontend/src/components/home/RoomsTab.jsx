import { Typography, Button, Table, Modal, Form, Input, InputNumber, Space, Tooltip, Popconfirm, Alert } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState } from 'react'
import { getRooms, createRoom, updateRoom, deleteRoom } from '../../api/rooms'
import { errMsg, tabHeader, formFooter, canManage } from './constants'

const { Text } = Typography

export default function RoomsTab({ homeId, myRole }) {
  const qc = useQueryClient()
  const [open, setOpen]       = useState(false)
  const [editing, setEditing] = useState(null)
  const [error, setError]     = useState(null)
  const [form] = Form.useForm()

  const { data: rooms = [], isLoading } = useQuery({
    queryKey: ['rooms', homeId],
    queryFn:  () => getRooms(homeId),
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['rooms', homeId] })

  const createMut = useMutation({
    mutationFn: (v) => createRoom({ ...v, homeId }),
    onSuccess:  () => { invalidate(); closeModal() },
    onError:    (e) => setError(errMsg(e, 'Failed to create room')),
  })

  const updateMut = useMutation({
    mutationFn: ({ id, data }) => updateRoom(id, { ...data, homeId }),
    onSuccess:  () => { invalidate(); closeModal() },
    onError:    (e) => setError(errMsg(e, 'Failed to update room')),
  })

  const deleteMut = useMutation({
    mutationFn: deleteRoom,
    onSuccess:  invalidate,
    onError:    (e) => setError(errMsg(e, 'Failed to delete room')),
  })

  const openCreate = () => { setEditing(null); form.resetFields(); setError(null); setOpen(true) }
  const openEdit   = (r) => { setEditing(r); form.setFieldsValue(r); setError(null); setOpen(true) }
  const closeModal = () => { setOpen(false); setEditing(null); form.resetFields(); setError(null) }

  const manage = canManage(myRole)

  const columns = [
    { title: 'Room Name',   dataIndex: 'roomName',    key: 'roomName',    render: v => <Text strong>{v}</Text>, sorter: (a, b) => a.roomName.localeCompare(b.roomName) },
    { title: 'Floor',       dataIndex: 'floor',       key: 'floor',       render: v => v ?? '—', width: 80, sorter: (a, b) => (a.floor ?? -1) - (b.floor ?? -1) },
    { title: 'Description', dataIndex: 'description', key: 'description', render: v => v || '—' },
    ...(manage ? [{
      title: 'Actions', key: 'actions', width: 100,
      render: (_, row) => (
        <Space>
          <Tooltip title="Edit"><Button type="text" icon={<EditOutlined />} onClick={() => openEdit(row)} /></Tooltip>
          <Popconfirm title="Delete this room?" description="Devices will become unassigned." onConfirm={() => deleteMut.mutate(row.id)} okText="Delete" okButtonProps={{ danger: true }}>
            <Tooltip title="Delete"><Button type="text" danger icon={<DeleteOutlined />} /></Tooltip>
          </Popconfirm>
        </Space>
      ),
    }] : []),
  ]

  return (
    <div>
      <div style={tabHeader}>
        <Text type="secondary">{rooms.length} room{rooms.length !== 1 ? 's' : ''}</Text>
        {manage && <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Add Room</Button>}
      </div>

      <Table dataSource={rooms} columns={columns} rowKey="id" loading={isLoading} pagination={false} locale={{ emptyText: 'No rooms yet' }} />

      {manage && (
        <Modal title={editing ? 'Edit Room' : 'Add Room'} open={open} onCancel={closeModal} footer={null} destroyOnClose>
          {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
          <Form form={form} layout="vertical" onFinish={v => editing ? updateMut.mutate({ id: editing.id, data: v }) : createMut.mutate(v)} style={{ marginTop: 12 }}>
            <Form.Item name="roomName" label="Room name" rules={[{ required: true, message: 'Required' }]}>
              <Input placeholder="e.g. Living Room" />
            </Form.Item>
            <Form.Item name="floor" label="Floor">
              <InputNumber style={{ width: '100%' }} placeholder="0" />
            </Form.Item>
            <Form.Item name="description" label="Description">
              <Input.TextArea rows={2} />
            </Form.Item>
            <div style={formFooter}>
              <Button onClick={closeModal}>Cancel</Button>
              <Button type="primary" htmlType="submit" loading={createMut.isPending || updateMut.isPending}>{editing ? 'Save' : 'Create'}</Button>
            </div>
          </Form>
        </Modal>
      )}
    </div>
  )
}
