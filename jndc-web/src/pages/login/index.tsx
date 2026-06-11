import React, { useState } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { authApi } from '../../api/auth';
import { useAuthStore } from '../../stores/auth';
import { LoginParams } from '../../types';
import { slideUpVariants, fadeVariants } from '../../utils/motion';
import './index.css';

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const setToken = useAuthStore((state) => state.setToken);

  const onFinish = async (values: LoginParams) => {
    setLoading(true);
    try {
      const result = await authApi.login(values);
      setToken(result.token);
      message.success('登录成功');
      navigate('/management/channel');
    } catch (error) {
      // Error is handled by interceptor
    } finally {
      setLoading(false);
    }
  };

  return (
    <motion.div
      className="login-container"
      variants={fadeVariants}
      initial="initial"
      animate="animate"
    >
      <motion.div variants={slideUpVariants} initial="initial" animate="animate">
        <Card title="JNDC 内网穿透管理系统" className="login-card">
          <Form
            name="login"
            initialValues={{ remember: true }}
            onFinish={onFinish}
            size="large"
          >
            <Form.Item
              name="name"
              rules={[{ required: true, message: '请输入用户名' }]}
            >
              <Input prefix={<UserOutlined />} placeholder="用户名" />
            </Form.Item>

            <Form.Item
              name="passWord"
              rules={[{ required: true, message: '请输入密码' }]}
            >
              <Input.Password prefix={<LockOutlined />} placeholder="密码" />
            </Form.Item>

            <Form.Item style={{ marginBottom: 0 }}>
              <Button type="primary" htmlType="submit" loading={loading} block>
                登 录
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </motion.div>

      <div className="login-brand">NO DISTANCE CONNECTION</div>
    </motion.div>
  );
};

export default Login;
