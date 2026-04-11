import {
  Typography, Button, Table, Modal, Form, Input, Select,
  Switch, InputNumber, Tag, Space, Popconfirm, Alert,
  Badge, Tooltip, Row, Col,
} from 'antd'
import {
  PlusOutlined, EditOutlined, DeleteOutlined,
  LockOutlined, UnlockOutlined, FilterOutlined,
  EyeOutlined, EyeInvisibleOutlined,
} from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useState, useMemo } from 'react'
import { getCameras, createCamera, updateCamera, deleteCamera, armCamera, disarmCamera } from '../../api/cameras'
import { getSmartLocks, createSmartLock, updateSmartLock, deleteSmartLock, lockDevice, unlockDevice } from '../../api/smartlocks'
import { errMsg, tabHeader, formFooter, PROTOCOLS, LOCK_COLORS, canManage, canOperate } from './constants'

const { Text } = Typography

export default function DevicesTab({ homeId, myRole }) {
  const qc = useQueryClient()

  const [typeFilter, setTypeFilter] = useState('ALL')
  const [search, setSearch]         = useState('')
  const [open, setOpen]             = useState(false)
  const [deviceType, setDeviceType] = useState(null)
  const [editing, setEditing]       = useState(null)
  const [error, setError]           = useState(null)
  const [form] = Form.useForm()

  const { data: cameras = [], isLoading: camLoading }  = useQuery({ queryKey: ['cameras', homeId], queryFn: () => getCameras(homeId) })
  const { data: locks   = [], isLoading: lockLoading } = useQuery({ queryKey: ['locks',   homeId], queryFn: () => getSmartLocks(homeId) })

  const isLoading = camLoading || lockLoading

  const allDevices = useMemo(() => [
    ...cameras.map(d => ({ ...d, _type: 'CAMERA' })),
    ...locks.map(d =>   ({ ...d, _type: 'SMART_LOCK' })),
  ], [cameras, locks])

  const filtered = useMemo(() => allDevices
    .filter(d => typeFilter === 'ALL' || d._type === typeFilter)
    .filter(d => !search || d.displayName.toLowerCase().includes(search.toLowerCase()) || d.deviceName.toLowerCase().includes(search.toLowerCase())),
    [allDevices, typeFilter, search])

  const invalidate = () => {
    qc.invalidateQueries({ queryKey: ['cameras', homeId] })
    qc.invalidateQueries({ queryKey: ['locks',   homeId] })
  }

  const createCamMut  = useMutation({ mutationFn: (v) => createCamera({ ...v, homeId }),              onSuccess: () => { invalidate(); closeModal() }, onError: e => setError(errMsg(e)) })
  const updateCamMut  = useMutation({ mutationFn: ({ id, v }) => updateCamera(id, { ...v, homeId }),  onSuccess: () => { invalidate(); closeModal() }, onError: e => setError(errMsg(e)) })
  const deleteCamMut  = useMutation({ mutationFn: deleteCamera,                                        onSuccess: invalidate,                           onError: e => setError(errMsg(e)) })
  const createLockMut = useMutation({ mutationFn: (v) => createSmartLock({ ...v, homeId }),            onSuccess: () => { invalidate(); closeModal() }, onError: e => setError(errMsg(e)) })
  const updateLockMut = useMutation({ mutationFn: ({ id, v }) => updateSmartLock(id, { ...v, homeId }),onSuccess: () => { invalidate(); closeModal() }, onError: e => setError(errMsg(e)) })
  const deleteLockMut = useMutation({ mutationFn: deleteSmartLock,                                     onSuccess: invalidate,                           onError: e => setError(errMsg(e)) })
  const lockMut       = useMutation({ mutationFn: lockDevice,    onSuccess: invalidate, onError: e => setError(errMsg(e)) })
  const unlockMut     = useMutation({ mutationFn: unlockDevice,  onSuccess: invalidate, onError: e => setError(errMsg(e)) })
  const armMut        = useMutation({ mutationFn: armCamera,     onSuccess: invalidate, onError: e => setError(errMsg(e)) })
  const disarmMut     = useMutation({ mutationFn: disarmCamera,  onSuccess: invalidate, onError: e => setError(errMsg(e)) })

  const openCreate = () => { setDeviceType(null); setEditing(null); form.resetFields(); setError(null); setOpen(true) }
  const openEdit   = (row) => { setDeviceType(row._type); setEditing(row); form.setFieldsValue({ ...row, roomId: row.room?.id }); setError(null); setOpen(true) }
  const closeModal = () => { setOpen(false); setEditing(null); form.resetFields(); setError(null) }

  const handleDelete = (row) => row._type === 'CAMERA' ? deleteCamMut.mutate(row.id) : deleteLockMut.mutate(row.id)

  const handleSubmit = (values) => {
    if (editing) {
      editing._type === 'CAMERA'
        ? updateCamMut.mutate({ id: editing.id, v: values })
        : updateLockMut.mutate({ id: editing.id, v: values })
    } else {
      deviceType === 'CAMERA' ? createCamMut.mutate(values) : createLockMut.mutate(values)
    }
  }

  const isPending = createCamMut.isPending || updateCamMut.isPending || createLockMut.isPending || updateLockMut.isPending

  const manage  = canManage(myRole)
  const operate = canOperate(myRole)

  const columns = [
    {
      title: 'Type', dataIndex: '_type', key: '_type', width: 120,
      render: t => t === 'CAMERA' ? <Tag color="purple">Camera</Tag> : <Tag color="cyan">Smart Lock</Tag>,
      sorter: (a, b) => a._type.localeCompare(b._type),
    },
    {
      title: 'Name', dataIndex: 'displayName', key: 'displayName',
      render: v => <Text strong>{v}</Text>,
      sorter: (a, b) => a.displayName.localeCompare(b.displayName),
    },
    {
      title: 'Device ID', dataIndex: 'deviceName', key: 'deviceName',
      render: v => <Text code>{v}</Text>,
      sorter: (a, b) => a.deviceName.localeCompare(b.deviceName),
    },
    {
      title: 'Protocol', dataIndex: 'protocol', key: 'protocol',
      render: v => <Tag>{v}</Tag>, width: 110,
      filters: PROTOCOLS.map(p => ({ text: p, value: p })),
      onFilter: (value, record) => record.protocol === value,
    },
    {
      title: 'Details', key: 'details',
      render: (_, row) => row._type === 'CAMERA' ? (
        <Space size={4}>
          <Text type="secondary">{row.resolution}</Text>
          {row.motionDetection && <Tag color="orange">Motion</Tag>}
          {row.nightVision     && <Tag color="geekblue">Night</Tag>}
          <Tag color={row.armed ? 'red' : 'default'}>{row.armed ? 'Armed' : 'Disarmed'}</Tag>
        </Space>
      ) : (
        <Space size={4}>
          <Badge status={LOCK_COLORS[row.lockStatus] || 'default'} text={row.lockStatus || '—'} />
          {row.autoLock    && <Tag color="cyan">{row.autoLockDelaySeconds}s auto</Tag>}
          {row.tamperAlert && <Tag color="red">Tamper</Tag>}
        </Space>
      ),
    },
    {
      title: 'Room', key: 'room',
      render: (_, row) => row.room?.roomName || <Text type="secondary">—</Text>,
      sorter: (a, b) => (a.room?.roomName || '').localeCompare(b.room?.roomName || ''),
    },
    {
      title: 'Actions', key: 'actions', width: 150,
      render: (_, row) => (
        <Space>
          {row._type === 'SMART_LOCK' && operate && <>
            <Tooltip title="Lock">
              <Button size="small" icon={<LockOutlined />} loading={lockMut.isPending} disabled={row.lockStatus === 'LOCKED'} onClick={() => lockMut.mutate(row.id)} />
            </Tooltip>
            <Tooltip title="Unlock">
              <Button size="small" icon={<UnlockOutlined />} loading={unlockMut.isPending} disabled={row.lockStatus === 'UNLOCKED'} onClick={() => unlockMut.mutate(row.id)} />
            </Tooltip>
          </>}
          {row._type === 'CAMERA' && operate && <>
            <Tooltip title="Arm">
              <Button size="small" icon={<EyeOutlined />} loading={armMut.isPending} disabled={row.armed} onClick={() => armMut.mutate(row.id)} />
            </Tooltip>
            <Tooltip title="Disarm">
              <Button size="small" icon={<EyeInvisibleOutlined />} loading={disarmMut.isPending} disabled={!row.armed} onClick={() => disarmMut.mutate(row.id)} />
            </Tooltip>
          </>}
          {manage && <>
            <Tooltip title="Edit"><Button type="text" icon={<EditOutlined />} onClick={() => openEdit(row)} /></Tooltip>
            <Popconfirm title="Delete this device?" onConfirm={() => handleDelete(row)} okText="Delete" okButtonProps={{ danger: true }}>
              <Tooltip title="Delete"><Button type="text" danger icon={<DeleteOutlined />} /></Tooltip>
            </Popconfirm>
          </>}
        </Space>
      ),
    },
  ]

  return (
    <div>
      {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} closable onClose={() => setError(null)} />}

      <div style={tabHeader}>
        <Space wrap>
          <FilterOutlined style={{ color: '#8c8c8c' }} />
          <Select
            value={typeFilter}
            onChange={setTypeFilter}
            style={{ width: 140 }}
            options={[
              { value: 'ALL',        label: 'All devices' },
              { value: 'CAMERA',     label: 'Cameras only' },
              { value: 'SMART_LOCK', label: 'Locks only' },
            ]}
          />
          <Input.Search
            placeholder="Search by name or ID"
            allowClear
            style={{ width: 220 }}
            onSearch={setSearch}
            onChange={e => !e.target.value && setSearch('')}
          />
          <Text type="secondary">{filtered.length} device{filtered.length !== 1 ? 's' : ''}</Text>
        </Space>
        {manage && <Button type="primary" icon={<PlusOutlined />} onClick={openCreate}>Add Device</Button>}
      </div>

      <Table
        dataSource={filtered}
        columns={columns}
        rowKey="id"
        loading={isLoading}
        pagination={{ pageSize: 10, showSizeChanger: true, showTotal: t => `${t} total` }}
        locale={{ emptyText: 'No devices match the current filter' }}
        scroll={{ x: 900 }}
      />

      <Modal
        title={editing ? `Edit ${editing._type === 'CAMERA' ? 'Camera' : 'Smart Lock'}` : 'Add Device'}
        open={open} onCancel={closeModal} footer={null} destroyOnClose width={560}
      >
        {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
        <Form form={form} layout="vertical" onFinish={handleSubmit} style={{ marginTop: 12 }}>

          {!editing && (
            <Form.Item name="_type" label="Device type" rules={[{ required: true, message: 'Select a type' }]}>
              <Select
                placeholder="Select type"
                onChange={setDeviceType}
                options={[
                  { value: 'CAMERA',     label: 'Camera' },
                  { value: 'SMART_LOCK', label: 'Smart Lock' },
                ]}
              />
            </Form.Item>
          )}

          {deviceType && <>
            <Row gutter={12}>
              <Col span={12}>
                <Form.Item name="displayName" label="Display name" rules={[{ required: true, message: 'Required' }]}>
                  <Input placeholder={deviceType === 'CAMERA' ? 'Front Door Cam' : 'Front Door Lock'} />
                </Form.Item>
              </Col>
              <Col span={12}>
                <Form.Item name="deviceName" label="Device ID" rules={[{ required: true, message: 'Required' }]}>
                  <Input placeholder={deviceType === 'CAMERA' ? 'cam-001' : 'lock-001'} />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="protocol" label="Protocol" rules={[{ required: true, message: 'Required' }]}>
              <Select options={PROTOCOLS.map(p => ({ value: p, label: p }))} />
            </Form.Item>

            {deviceType === 'CAMERA' && <>
              <Row gutter={12}>
                <Col span={12}>
                  <Form.Item name="resolution" label="Resolution" rules={[{ required: true, message: 'Required' }]}>
                    <Input placeholder="1080p" />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item name="storageLocation" label="Storage location">
                    <Input placeholder="/storage/cam1" />
                  </Form.Item>
                </Col>
              </Row>
              <div style={{ display: 'flex', gap: 24, marginBottom: 16 }}>
                <Form.Item name="motionDetection" label="Motion detection" valuePropName="checked" style={{ margin: 0 }}>
                  <Switch />
                </Form.Item>
                <Form.Item name="nightVision" label="Night vision" valuePropName="checked" style={{ margin: 0 }}>
                  <Switch />
                </Form.Item>
              </div>
            </>}

            {deviceType === 'SMART_LOCK' && <>
              <Form.Item name="autoLockDelaySeconds" label="Auto-lock delay (seconds)" rules={[{ required: true, message: 'Required' }]}>
                <InputNumber style={{ width: '100%' }} min={0} placeholder="30" />
              </Form.Item>
              <div style={{ display: 'flex', gap: 24, marginBottom: 16 }}>
                <Form.Item name="autoLock" label="Auto-lock" valuePropName="checked" style={{ margin: 0 }}>
                  <Switch />
                </Form.Item>
                <Form.Item name="tamperAlert" label="Tamper alert" valuePropName="checked" style={{ margin: 0 }}>
                  <Switch />
                </Form.Item>
              </div>
            </>}
          </>}

          <div style={formFooter}>
            <Button onClick={closeModal}>Cancel</Button>
            <Button type="primary" htmlType="submit" loading={isPending}>{editing ? 'Save' : 'Create'}</Button>
          </div>
        </Form>
      </Modal>
    </div>
  )
}
