import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import Login from './pages/login';
import MainLayout from './components/Layout';
import ChannelList from './pages/channel';
import ServiceList from './pages/service';
import PortList from './pages/port';
import IpFilter from './pages/ipFilter';
import HttpApp from './pages/httpApp';
import { useAuthStore } from './stores/auth';

const ProtectedRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const token = localStorage.getItem('auth-token');

  if (!isAuthenticated && !token) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

const App: React.FC = () => {
  return (
    <ConfigProvider locale={zhCN}>
      <BrowserRouter>
        <Routes>
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
            <Route path="serverPortList" element={<PortList />} />
            <Route path="ipFilter" element={<IpFilter />} />
            <Route path="httpApp" element={<HttpApp />} />
          </Route>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
};

export default App;
