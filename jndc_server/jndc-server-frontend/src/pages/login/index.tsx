import React, { useState } from 'react';
import { Form, Input, Button, message } from 'antd';
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
      if (!result?.token || result.token === '403') {
        useAuthStore.getState().logout();
        message.error('用户名或密码错误');
        return;
      }
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
    <div className="login-container">
      <motion.div 
        className="login-left"
        variants={fadeVariants}
        initial="initial"
        animate="animate"
      >
        <div className="login-left-content">
          <motion.div variants={slideUpVariants} initial="initial" animate="animate">
            <h1 className="login-slogan">连接每一次沟通</h1>
            <p className="login-sub-slogan">高效、稳定、安全的内网穿透管理平台</p>
          </motion.div>
          <motion.div className="login-image-wrapper" variants={fadeVariants} initial="initial" animate="animate" transition={{ delay: 0.2 }}>
            <img src="/cloud.jpg" alt="Cloud" className="login-cloud-img" />
          </motion.div>
        </div>
      </motion.div>

      <motion.div 
        className="login-right"
        variants={fadeVariants}
        initial="initial"
        animate="animate"
      >
        <div className="login-form-container">
          <motion.div variants={slideUpVariants} initial="initial" animate="animate" transition={{ delay: 0.1 }}>
            <div className="login-header">
              <h2>欢迎登录 JNDC</h2>
              <p>请使用您的管理员账号登录</p>
            </div>
            
            <Form
              name="login"
              initialValues={{ remember: true }}
              onFinish={onFinish}
              size="large"
              layout="vertical"
              className="login-form"
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
                <Button type="primary" htmlType="submit" loading={loading} block className="login-btn">
                  登 录
                </Button>
              </Form.Item>
            </Form>
          </motion.div>
        </div>
        
        <div className="login-brand">NO DISTANCE CONNECTION</div>
      </motion.div>
    </div>
  );
};

export default Login;
