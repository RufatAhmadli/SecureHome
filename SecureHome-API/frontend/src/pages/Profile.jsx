import {
  Typography, Form, Input, Switch, DatePicker, Button,
  Card, Alert, Spin, Breadcrumb, Divider, Row, Col, Tag, Modal,
} from 'antd'
import {
  UserOutlined, HomeOutlined, ArrowLeftOutlined, SaveOutlined,
  CrownOutlined, LockOutlined, DeleteOutlined, ExclamationCircleOutlined,
} from '@ant-design/icons'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { useNavigate, Link } from 'react-router-dom'
import { useEffect } from 'react'
import { getMyProfile, createMyProfile, updateMyProfile } from '../api/profile'
import { getMe, updateMyName, changePassword, deleteMyAccount } from '../api/auth'
import useAuthStore from '../store/authStore'
import dayjs from 'dayjs'

const { Title, Text } = Typography

const errMsg = (err, fallback = 'Operation failed') =>
  err?.response?.data?.message || fallback

const TIMEZONE_OPTIONS = [
  'UTC', 'Europe/Baku', 'Europe/London', 'Europe/Berlin',
  'America/New_York', 'America/Los_Angeles', 'Asia/Tokyo',
]

export default function Profile() {
  const navigate      = useNavigate()
  const qc            = useQueryClient()
  const { user, logout } = useAuthStore()
  const isAdmin          = user?.roles?.includes('ROLE_ADMIN')

  const [nameForm]    = Form.useForm()
  const [prefForm]    = Form.useForm()
  const [pwForm]      = Form.useForm()

  // ── Account data (firstName, lastName) ──────────────────────────────────
  const { data: account, isLoading: accountLoading } = useQuery({
    queryKey: ['auth', 'me'],
    queryFn: getMe,
  })

  // Pre-fill name form once account data is available
  useEffect(() => {
    if (account) {
      nameForm.setFieldsValue({ firstName: account.firstName, lastName: account.lastName })
    }
  }, [account, nameForm])

  const nameMut = useMutation({
    mutationFn: updateMyName,
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['auth', 'me'] }),
  })

  // ── Profile preferences ──────────────────────────────────────────────────
  const { data: profile, isLoading: profileLoading, error: profileError } = useQuery({
    queryKey: ['profile', 'me'],
    queryFn:  getMyProfile,
  })

  // Pre-fill preferences form once profile data is available
  useEffect(() => {
    if (profile) {
      prefForm.setFieldsValue({
        ...profile,
        birthDate: profile.birthDate ? dayjs(profile.birthDate) : null,
      })
    }
  }, [profile, prefForm])

  const isNew = profile === null

  const prefMut = useMutation({
    mutationFn: (data) => isNew ? createMyProfile(data) : updateMyProfile(data),
    onSuccess:  () => qc.invalidateQueries({ queryKey: ['profile', 'me'] }),
  })

  // ── Change password ──────────────────────────────────────────────────────
  const pwMut = useMutation({
    mutationFn: changePassword,
    onSuccess: () => pwForm.resetFields(),
    onError: (err) => {
      const msg = err?.response?.data?.message || ''
      if (msg.toLowerCase().includes('current password')) {
        pwForm.setFields([{ name: 'currentPassword', errors: [msg] }])
      }
    },
  })

  // ── Delete account ───────────────────────────────────────────────────────
  const deleteMut = useMutation({
    mutationFn: deleteMyAccount,
    onSuccess:  () => { logout(); navigate('/login') },
  })

  const confirmDelete = () => {
    Modal.confirm({
      title:   'Delete your account?',
      icon:    <ExclamationCircleOutlined style={{ color: '#ff4d4f' }} />,
      content: 'This will permanently delete your account, profile, and all home memberships. This cannot be undone.',
      okText:  'Delete my account',
      okButtonProps: { danger: true },
      cancelText: 'Cancel',
      onOk: () => deleteMut.mutateAsync(),
    })
  }

  const handleNameSubmit = (values) => nameMut.mutate(values)

  const handlePrefSubmit = (values) => {
    prefMut.mutate({
      ...values,
      birthDate: values.birthDate ? values.birthDate.format('YYYY-MM-DD') : null,
    })
  }

  if (accountLoading || profileLoading) {
    return <div style={styles.center}><Spin size="large" /></div>
  }

  return (
    <div style={styles.page}>
      {/* Header */}
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
            { title: 'Profile' },
          ]}
        />

        <div style={{ marginBottom: 24 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <Title level={3} style={{ margin: 0 }}>My Profile</Title>
            {/* Admin badge — only visible to admins */}
            {isAdmin && (
              <Tag icon={<CrownOutlined />} color="gold">Admin</Tag>
            )}
          </div>
          <Text type="secondary">Manage your account details and notification preferences</Text>
        </div>

        <Row gutter={[24, 24]}>
          {/* ── Left: Account Info ────────────────────────────────────────── */}
          <Col xs={24} md={10}>
            <Card style={styles.card} title="Account Info">
              <div style={styles.avatarBlock}>
                <div style={styles.avatarCircle}>
                  <UserOutlined style={{ fontSize: 32, color: '#1677ff' }} />
                </div>
                <Text strong style={{ marginTop: 10 }}>
                  {account?.firstName} {account?.lastName}
                </Text>
                <Text type="secondary" style={{ fontSize: 13 }}>{user?.email}</Text>
              </div>

              <Divider />

              {nameMut.isSuccess && (
                <Alert type="success" message="Name updated" showIcon style={{ marginBottom: 16 }} closable />
              )}
              {nameMut.isError && (
                <Alert type="error" message={errMsg(nameMut.error, 'Failed to update name')} showIcon style={{ marginBottom: 16 }} closable />
              )}

              {/* firstName / lastName form */}
              <Form
                form={nameForm}
                layout="vertical"
                initialValues={{ firstName: account?.firstName, lastName: account?.lastName }}
                onFinish={handleNameSubmit}
              >
                <Row gutter={12}>
                  <Col xs={24} sm={12}>
                    <Form.Item
                      name="firstName"
                      label="First name"
                      rules={[{ min: 2, message: 'Min 2 characters' }]}
                    >
                      <Input placeholder="John" />
                    </Form.Item>
                  </Col>
                  <Col xs={24} sm={12}>
                    <Form.Item
                      name="lastName"
                      label="Last name"
                      rules={[{ min: 2, message: 'Min 2 characters' }]}
                    >
                      <Input placeholder="Doe" />
                    </Form.Item>
                  </Col>
                </Row>

                <Form.Item label="Email">
                  <Input value={user?.email} disabled />
                </Form.Item>

                <Button
                  type="primary"
                  htmlType="submit"
                  icon={<SaveOutlined />}
                  loading={nameMut.isPending}
                  block
                >
                  Save Name
                </Button>
              </Form>

              {/* Admin-only section */}
              {isAdmin && (
                <>
                  <Divider />
                  <div style={styles.adminSection}>
                    <CrownOutlined style={{ color: '#d48806', marginRight: 6 }} />
                    <Text strong style={{ color: '#d48806' }}>Admin Access</Text>
                    <Text type="secondary" style={{ fontSize: 12, display: 'block', marginTop: 4 }}>
                      You can view all user profiles and manage system-wide settings.
                    </Text>
                    <Button
                      type="dashed"
                      style={{ marginTop: 10, width: '100%' }}
                      onClick={() => navigate('/admin/profiles')}
                    >
                      View All Profiles
                    </Button>
                  </div>
                </>
              )}
            </Card>
          </Col>

          {/* ── Right: Preferences ───────────────────────────────────────── */}
          <Col xs={24} md={14}>
            <Card
              style={styles.card}
              title={isNew ? 'Create your profile' : 'Preferences'}
            >
              {profileError && (
                <Alert type="error" message={errMsg(profileError, 'Could not load preferences')} showIcon style={{ marginBottom: 16 }} />
              )}
              {prefMut.isSuccess && (
                <Alert type="success" message={isNew ? 'Profile created' : 'Preferences saved'} showIcon style={{ marginBottom: 16 }} closable />
              )}
              {prefMut.isError && (
                <Alert type="error" message={errMsg(prefMut.error, 'Failed to save preferences')} showIcon style={{ marginBottom: 16 }} closable />
              )}

              <Form
                form={prefForm}
                layout="vertical"
                initialValues={profile ? {
                  ...profile,
                  birthDate: profile.birthDate ? dayjs(profile.birthDate) : null,
                } : {
                  emailNotifications: true,
                  smsNotifications:   false,
                }}
                onFinish={handlePrefSubmit}
              >
                <Row gutter={16}>
                  <Col xs={24} sm={12}>
                    <Form.Item name="phoneNumber" label="Phone number">
                      <Input placeholder="+994 50 000 00 00" />
                    </Form.Item>
                  </Col>
                  <Col xs={24} sm={12}>
                    <Form.Item name="birthDate" label="Date of birth">
                      <DatePicker style={{ width: '100%' }} placeholder="Select date" />
                    </Form.Item>
                  </Col>
                </Row>

                <Form.Item name="address" label="Address">
                  <Input placeholder="123 Main St" />
                </Form.Item>

                <Row gutter={16}>
                  <Col xs={24} sm={12}>
                    <Form.Item name="city" label="City">
                      <Input placeholder="Baku" />
                    </Form.Item>
                  </Col>
                  <Col xs={24} sm={12}>
                    <Form.Item name="timezone" label="Timezone">
                      <Input placeholder="Europe/Baku" list="tz-options" />
                      <datalist id="tz-options">
                        {TIMEZONE_OPTIONS.map(tz => <option key={tz} value={tz} />)}
                      </datalist>
                    </Form.Item>
                  </Col>
                </Row>

                <Divider orientation="left" plain style={{ fontSize: 13 }}>Notifications</Divider>

                <div style={styles.switchRow}>
                  <Form.Item name="emailNotifications" valuePropName="checked" style={{ margin: 0 }}>
                    <Switch />
                  </Form.Item>
                  <div>
                    <Text strong>Email notifications</Text>
                    <br />
                    <Text type="secondary" style={{ fontSize: 12 }}>Receive security alerts and updates by email</Text>
                  </div>
                </div>

                <div style={{ ...styles.switchRow, marginTop: 12 }}>
                  <Form.Item name="smsNotifications" valuePropName="checked" style={{ margin: 0 }}>
                    <Switch />
                  </Form.Item>
                  <div>
                    <Text strong>SMS notifications</Text>
                    <br />
                    <Text type="secondary" style={{ fontSize: 12 }}>Receive critical alerts via SMS</Text>
                  </div>
                </div>

                <div style={{ marginTop: 24, display: 'flex', justifyContent: 'flex-end' }}>
                  <Button
                    type="primary"
                    htmlType="submit"
                    icon={<SaveOutlined />}
                    loading={prefMut.isPending}
                    size="large"
                  >
                    {isNew ? 'Create Profile' : 'Save Preferences'}
                  </Button>
                </div>
              </Form>
            </Card>
          </Col>
        </Row>

        {/* ── Security ──────────────────────────────────────────────────── */}
        <Card
          style={{ ...styles.card, marginTop: 24 }}
          title={<span><LockOutlined style={{ marginRight: 8 }} />Change Password</span>}
        >
          {pwMut.isSuccess && (
            <Alert type="success" message="Password changed successfully" showIcon style={{ marginBottom: 16 }} closable />
          )}
          {pwMut.isError && !pwMut.error?.response?.data?.message?.toLowerCase().includes('current password') && (
            <Alert type="error" message={errMsg(pwMut.error, 'Failed to change password')} showIcon style={{ marginBottom: 16 }} closable />
          )}

          <Form form={pwForm} layout="vertical" onFinish={(v) => pwMut.mutate(v)} style={{ maxWidth: 480 }}>
            <Form.Item
              name="currentPassword"
              label="Current password"
              rules={[{ required: true, message: 'Required' }]}
            >
              <Input.Password placeholder="Enter your current password" />
            </Form.Item>

            <Form.Item
              name="newPassword"
              label="New password"
              rules={[
                { required: true, message: 'Required' },
                {
                  pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}$/,
                  message: 'Min 8 chars with uppercase, lowercase, digit and special character',
                },
              ]}
            >
              <Input.Password placeholder="New password" />
            </Form.Item>

            <Form.Item
              name="confirmPassword"
              label="Confirm new password"
              dependencies={['newPassword']}
              rules={[
                { required: true, message: 'Required' },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('newPassword') === value) return Promise.resolve()
                    return Promise.reject(new Error('Passwords do not match'))
                  },
                }),
              ]}
            >
              <Input.Password placeholder="Repeat new password" />
            </Form.Item>

            <Button
              type="primary"
              htmlType="submit"
              icon={<SaveOutlined />}
              loading={pwMut.isPending}
            >
              Change Password
            </Button>
          </Form>
        </Card>

        {/* ── Danger Zone ───────────────────────────────────────────────── */}
        <Card style={{ ...styles.card, marginTop: 24, borderColor: '#ffa39e' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 16 }}>
            <div>
              <Text strong style={{ color: '#cf1322', fontSize: 15 }}>
                <DeleteOutlined style={{ marginRight: 8 }} />Delete Account
              </Text>
              <Text type="secondary" style={{ display: 'block', fontSize: 13, marginTop: 4 }}>
                Permanently deletes your account, profile, and all home memberships. This action cannot be undone.
              </Text>
            </div>
            <Button
              danger
              icon={<DeleteOutlined />}
              loading={deleteMut.isPending}
              onClick={confirmDelete}
            >
              Delete My Account
            </Button>
          </div>
        </Card>
      </main>
    </div>
  )
}

const styles = {
  page:   { minHeight: '100vh', background: '#f0f2f5' },
  header: {
    display: 'flex', alignItems: 'center', justifyContent: 'space-between',
    padding: '0 32px', height: 64, background: '#fff',
    borderBottom: '1px solid #f0f0f0', position: 'sticky', top: 0, zIndex: 100,
  },
  headerLeft:  { display: 'flex', alignItems: 'center', gap: 10 },
  main:        { maxWidth: 1000, margin: '0 auto', padding: '32px 24px' },
  center:      { display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '60vh' },
  card:        { borderRadius: 12, border: '1px solid #f0f0f0' },
  avatarBlock: { display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '8px 0 12px' },
  avatarCircle: {
    width: 72, height: 72, borderRadius: '50%',
    background: '#e6f4ff', display: 'flex', alignItems: 'center', justifyContent: 'center',
  },
  switchRow:    { display: 'flex', alignItems: 'center', gap: 12 },
  adminSection: {
    background: '#fffbe6', border: '1px solid #ffe58f',
    borderRadius: 8, padding: '12px 16px',
  },
}
