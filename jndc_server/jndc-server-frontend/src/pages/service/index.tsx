import React, { useEffect, useState, useCallback } from 'react';
import { Table, Card, Button, Space, Input, Tag } from 'antd';
import { ReloadOutlined, SearchOutlined } from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { motion } from 'framer-motion';
import { serviceApi } from '../../api/service';
import { ServiceDescription } from '../../types';
import { wsClient } from '../../utils/websocket';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';

const ServiceList: React.FC = () => {
  const [services, setServices] = useState<ServiceDescription[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');

  const fetchServices = useCallback(async () => {
    setLoading(true);
    try {
      const data = await serviceApi.getServiceList();
      setServices(data);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchServices();

    // Subscribe to page refresh
    const unsub = wsClient.onPageRefresh('services', fetchServices);
    return unsub;
  }, [fetchServices]);

  const filteredServices = services.filter(
    (s) =>
      s.serviceName.includes(searchText) ||
      s.serviceIp.includes(searchText) ||
      s.clientIp.includes(searchText) ||
      s.clientId.includes(searchText)
  );

  const columns: ColumnsType<ServiceDescription> = [
    {
      title: '服务名称',
      dataIndex: 'serviceName',
      key: 'serviceName',
    },
    {
      title: '服务IP',
      dataIndex: 'serviceIp',
      key: 'serviceIp',
    },
    {
      title: '服务端口',
      dataIndex: 'servicePort',
      key: 'servicePort',
    },
    {
      title: '客户端ID',
      dataIndex: 'clientId',
      key: 'clientId',
      ellipsis: true,
    },
    {
      title: '客户端IP',
      dataIndex: 'clientIp',
      key: 'clientIp',
    },
    {
      title: '状态',
      key: 'status',
      render: () => <Tag color="green">在线</Tag>,
    },
  ];

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card
          title="服务注册信息"
          extra={
            <Space>
              <Input
                placeholder="搜索服务名、IP或客户端"
                prefix={<SearchOutlined />}
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                style={{ width: 300 }}
              />
              <Button icon={<ReloadOutlined />} onClick={fetchServices}>
                刷新
              </Button>
            </Space>
          }
        >
          <Table
            columns={columns}
            dataSource={filteredServices}
            rowKey={(record) => `${record.clientId}-${record.serviceIp}-${record.servicePort}`}
            loading={loading}
            pagination={false}
          />
        </Card>
      </motion.div>
    </motion.div>
  );
};

export default ServiceList;
