import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Spin, message, Typography, Button } from 'antd';
import { EyeOutlined, EyeInvisibleOutlined } from '@ant-design/icons';
import { getServerRuntimeInfo, ServerRuntimeInfoVO } from '../../api/serverInfo';
import { motion } from 'framer-motion';

const { Title } = Typography;

const ServerInfoPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [serverInfo, setServerInfo] = useState<ServerRuntimeInfoVO | null>(null);
  const [showPassword, setShowPassword] = useState<boolean>(false);

  const fetchServerInfo = async () => {
    try {
      setLoading(true);
      const res = await getServerRuntimeInfo();
      // Assuming res data is the actual payload if interceptors handle it, otherwise adjust based on your interceptor logic
      setServerInfo(res as unknown as ServerRuntimeInfoVO);
    } catch (error) {
      message.error('获取服务端运行信息失败');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchServerInfo();
  }, []);

  return (
    <div className="server-info-page">
      <div className="page-header">
        <Title level={4} style={{ margin: 0 }}>服务端运行信息</Title>
      </div>

      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.3 }}
      >
        <Card className="glass-card" bordered={false}>
          <Spin spinning={loading}>
            {serverInfo && (
              <Descriptions
                bordered
                column={1}
                labelStyle={{ width: '250px', fontWeight: 500 }}
                contentStyle={{ background: '#fafafa' }}
              >
                <Descriptions.Item label="绑定IP">{serverInfo.bindIp || '-'}</Descriptions.Item>
                <Descriptions.Item label="服务端口">{serverInfo.servicePort || '-'}</Descriptions.Item>
                <Descriptions.Item label="管理API端口">{serverInfo.managementApiPort || '-'}</Descriptions.Item>
                <Descriptions.Item label="HTTP应用端口">{serverInfo.httpPort || '-'}</Descriptions.Item>
                <Descriptions.Item label="服务端认证密码（secrete）">
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <span>{showPassword ? serverInfo.secrete : '••••••••'}</span>
                    <Button 
                      type="text" 
                      size="small" 
                      icon={showPassword ? <EyeInvisibleOutlined /> : <EyeOutlined />} 
                      onClick={() => setShowPassword(!showPassword)}
                    />
                  </div>
                </Descriptions.Item>
              </Descriptions>
            )}
          </Spin>
        </Card>
      </motion.div>
    </div>
  );
};

export default ServerInfoPage;
