import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { App as AntApp, ConfigProvider, theme } from 'antd';
import type { ThemeConfig } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { AnimatePresence } from 'framer-motion';
import Login from './pages/login';
import MainLayout from './components/Layout';
import ChannelList from './pages/channel';
import ServiceList from './pages/service';
import PortList from './pages/port';
import IpFilter from './pages/ipFilter';
import HttpApp from './pages/httpApp';
import ServiceControl from './pages/serviceControl';
import { useAuthStore } from './stores/auth';

const appTheme: ThemeConfig = {
  algorithm: theme.defaultAlgorithm,
  token: {
    colorPrimary: '#3370FF',
    colorInfo: '#3370FF',
    colorSuccess: '#00B578',
    colorWarning: '#FFB020',
    colorError: '#F54A45',
    colorText: '#1F2329',
    colorTextSecondary: '#646A73',
    colorTextTertiary: '#8F959E',
    colorBorder: '#DEE0E3',
    colorSplit: '#EFF0F1',
    colorBgLayout: '#F7F9FC',
    colorBgContainer: '#FFFFFF',
    borderRadius: 8,
    fontSize: 14,
    wireframe: false,
  },
  components: {
    Button: {
      borderRadius: 8,
      controlHeight: 36,
    },
    Input: {
      borderRadius: 8,
      controlHeight: 36,
    },
    Select: {
      borderRadius: 8,
      controlHeight: 36,
    },
    Card: {
      borderRadiusLG: 12,
      paddingLG: 24,
    },
    Table: {
      headerBg: '#F7F9FC',
      headerColor: '#1F2329',
      rowHoverBg: '#F5F8FF',
    },
    Menu: {
      itemSelectedBg: '#EAF1FF',
      itemSelectedColor: '#3370FF',
    },
  },
};

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const token = localStorage.getItem('auth-token');

  if ((!isAuthenticated && !token) || token === '403') {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

const AnimatedRoutes: React.FC = () => {
  const location = useLocation();

  return (
    <AnimatePresence mode="wait">
      <Routes location={location} key={location.pathname}>
        <Route path="/login" element={<Login />} />
        <Route
          path="/management"
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/management/channel" replace />} />
          <Route path="channel" element={<ChannelList />} />
          <Route path="services" element={<ServiceList />} />
          <Route path="serviceControl" element={<ServiceControl />} />
          <Route path="serverPortList" element={<PortList />} />
          <Route path="ipFilter" element={<IpFilter />} />
          <Route path="httpApp" element={<HttpApp />} />
        </Route>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </AnimatePresence>
  );
};

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN} theme={appTheme}>
      <AntApp>
        <BrowserRouter>
          <AnimatedRoutes />
        </BrowserRouter>
      </AntApp>
    </ConfigProvider>
  );
};

export default App;
