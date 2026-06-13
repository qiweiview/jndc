import React, { useEffect, useState } from 'react';
import { Card, Descriptions, Spin, message, Typography, Button } from 'antd';
import { EyeOutlined, EyeInvisibleOutlined, DownloadOutlined } from '@ant-design/icons';
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

  const handleDownloadConfig = () => {
    if (!serverInfo) return;
    const serverIp = window.location.hostname;
    const yamlContent = `secrete: "${serverInfo.secrete}" # 服务端密钥
loglevel: "info"
serverIp: "${serverIp}" # 服务端监听IP地址
serverPort: "${serverInfo.servicePort}" # 服务端监听端口
autoReleaseTimeOut: 600000 # 客户端空闲超时自动断开时间（毫秒）
authMode: 1 # 授权模式：0=SELF_MANAGED(自管理), 1=FULL_AUTHORIZED(全授权受服务端控制)
`;

    const blob = new Blob([yamlContent], { type: 'text/yaml;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'client-config.yml');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    message.success('客户端配置生成成功！');
  };

  return (
    <div className="server-info-page">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Title level={4} style={{ margin: 0 }}>服务端运行信息</Title>
        <Button type="primary" icon={<DownloadOutlined />} onClick={handleDownloadConfig} disabled={!serverInfo || loading}>
          下载全授权客户端配置
        </Button>
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
