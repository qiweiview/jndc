import React, { useEffect } from 'react';
import { Layout, Menu, Button, message } from 'antd';
import {
  ApiOutlined,
  CloudServerOutlined,
  DesktopOutlined,
  SafetyOutlined,
  GlobalOutlined,
  LogoutOutlined,
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../../stores/auth';
import { wsClient } from '../../utils/websocket';
import './index.css';

const { Header, Sider, Content } = Layout;

const menuItems = [
  {
    key: '/management/channel',
    icon: <ApiOutlined />,
    label: '隧道列表',
  },
  {
    key: '/management/services',
    icon: <CloudServerOutlined />,
    label: '服务注册信息',
  },
  {
    key: '/management/serverPortList',
    icon: <DesktopOutlined />,
    label: '端口监听',
  },
  {
    key: '/management/ipFilter',
    icon: <SafetyOutlined />,
    label: 'IP访问管控',
  },
  {
    key: '/management/httpApp',
    icon: <GlobalOutlined />,
    label: 'HTTP应用',
  },
];

const MainLayout: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const logout = useAuthStore((state) => state.logout);

  useEffect(() => {
    // Connect WebSocket
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const wsUrl = `${protocol}//${window.location.host}/ws`;
    wsClient.connect(wsUrl);

    // Listen for notifications
    const unsubNotification = wsClient.onNotification((data) => {
      message.info(data);
    });

    return () => {
      unsubNotification();
      wsClient.disconnect();
    };
  }, []);

  const handleMenuClick = (info: { key: string }) => {
    navigate(info.key);
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
    message.success('已安全退出');
  };

  return (
    <Layout className="main-layout">
      <Sider width={200} className="main-sider">
        <div className="logo">JNDC</div>
        <Menu
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={handleMenuClick}
          className="main-menu"
        />
      </Sider>
      <Layout>
        <Header className="main-header">
          <div className="header-content">
            <h2>JNDC 内网穿透管理系统</h2>
            <Button
              type="text"
              icon={<LogoutOutlined />}
              onClick={handleLogout}
            >
              安全退出
            </Button>
          </div>
        </Header>
        <Content className="main-content">
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
