import { Form, Input, Button, Card, Typography, message, Divider } from 'antd'
import { UserOutlined, LockOutlined, MailOutlined, HomeOutlined } from '@ant-design/icons'
import { useMutation } from '@tanstack/react-query'
import { useNavigate, Link } from 'react-router-dom'
import { register } from '../api/auth'

const { Title, Text } = Typography

export default function Register() {
  const navigate = useNavigate()
  const [messageApi, contextHolder] = message.useMessage()

  const mutation = useMutation({
    mutationFn: register,
    onSuccess: () => {
      messageApi.success('Account created! Please sign in.')
      setTimeout(() => navigate('/login'), 1200)
    },
    onError: (err) => {
      messageApi.error(err.response?.data?.message || 'Registration failed. Try again.')
    },
  })

  return (
    <div style={styles.page}>
      {contextHolder}
      <div style={styles.brand}>
        <HomeOutlined style={{ fontSize: 28, color: '#1677ff' }} />
        <Title level={3} style={{ margin: 0, color: '#1677ff' }}>SecureHome</Title>
      </div>

      <Card style={styles.card} variant="outlined">
        <Title level={4} style={{ marginBottom: 4, textAlign: 'center' }}>Create account</Title>
        <Text type="secondary" style={{ display: 'block', textAlign: 'center', marginBottom: 28 }}>
          Join SecureHome — your private smart home hub
        </Text>

        <Form layout="vertical" onFinish={(v) => mutation.mutate(v)} size="large">
          <div style={{ display: 'flex', gap: 12 }}>
            <Form.Item
              name="firstName"
              label="First name"
              style={{ flex: 1 }}
              rules={[{ required: true, message: 'Required' }]}
            >
              <Input prefix={<UserOutlined />} placeholder="Jane" />
            </Form.Item>
            <Form.Item
              name="lastName"
              label="Last name"
              style={{ flex: 1 }}
              rules={[{ required: true, message: 'Required' }]}
            >
              <Input placeholder="Doe" />
            </Form.Item>
          </div>

          <Form.Item
            name="email"
            label="Email"
            rules={[
              { required: true, message: 'Email is required' },
              { type: 'email', message: 'Enter a valid email' },
            ]}
          >
            <Input prefix={<MailOutlined />} placeholder="you@example.com" />
          </Form.Item>

          <Form.Item
            name="password"
            label="Password"
            rules={[
              { required: true, message: 'Password is required' },
              { min: 8, message: 'At least 8 characters' },
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Min. 8 characters" />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            label="Confirm password"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Please confirm your password' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) return Promise.resolve()
                  return Promise.reject(new Error('Passwords do not match'))
                },
              }),
            ]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="Repeat password" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 12 }}>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={mutation.isPending}
            >
              Create Account
            </Button>
          </Form.Item>
        </Form>

        <Divider style={{ margin: '12px 0' }} />
        <Text type="secondary" style={{ display: 'block', textAlign: 'center' }}>
          Already have an account?{' '}
          <Link to="/login">Sign in</Link>
        </Text>
      </Card>
    </div>
  )
}

const styles = {
  page: {
    minHeight: '100vh',
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    justifyContent: 'center',
    background: '#f0f2f5',
    padding: 24,
  },
  brand: {
    display: 'flex',
    alignItems: 'center',
    gap: 10,
    marginBottom: 28,
  },
  card: {
    width: '100%',
    maxWidth: 460,
    borderRadius: 12,
    boxShadow: '0 4px 24px rgba(0,0,0,0.08)',
  },
}
