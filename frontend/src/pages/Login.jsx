import { Form, Input, Button, Card, Typography, message, Divider } from 'antd'
import { LockOutlined, MailOutlined, HomeOutlined } from '@ant-design/icons'
import { useMutation } from '@tanstack/react-query'
import { useNavigate, Link } from 'react-router-dom'
import { login } from '../api/auth'
import useAuthStore from '../store/authStore'

const { Title, Text } = Typography

export default function Login() {
  const navigate = useNavigate()
  const setToken = useAuthStore((s) => s.setToken)
  const [messageApi, contextHolder] = message.useMessage()

  const mutation = useMutation({
    mutationFn: login,
    onSuccess: (data) => {
      setToken(data.token)
      navigate('/')
    },
    onError: (err) => {
      messageApi.error(err.response?.data?.message || 'Invalid email or password.')
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
        <Title level={4} style={{ marginBottom: 4, textAlign: 'center' }}>Welcome back</Title>
        <Text type="secondary" style={{ display: 'block', textAlign: 'center', marginBottom: 28 }}>
          Sign in to manage your smart home
        </Text>

        <Form layout="vertical" onFinish={(v) => mutation.mutate(v)} size="large">
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
            rules={[{ required: true, message: 'Password is required' }]}
          >
            <Input.Password prefix={<LockOutlined />} placeholder="••••••••" />
          </Form.Item>

          <Form.Item style={{ marginBottom: 12 }}>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={mutation.isPending}
            >
              Sign In
            </Button>
          </Form.Item>
        </Form>

        <Divider style={{ margin: '12px 0' }} />
        <Text type="secondary" style={{ display: 'block', textAlign: 'center' }}>
          Don't have an account?{' '}
          <Link to="/register">Create one</Link>
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
    maxWidth: 420,
    borderRadius: 12,
    boxShadow: '0 4px 24px rgba(0,0,0,0.08)',
  },
}
