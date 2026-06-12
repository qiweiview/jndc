import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Button,
  Space,
  Input,
  Tag,
  Modal,
  message,
  Tabs,
  Popconfirm,
  Empty
} from 'antd';
import {
  ReloadOutlined,
  SearchOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { motion } from 'framer-motion';
import { channelApi } from '../../api/channel';
import { ChannelContext, ChannelRecord } from '../../types';
import { wsClient } from '../../utils/websocket';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';

const ChannelList: React.FC = () => {
  const navigate = useNavigate();
  const [channels, setChannels] = useState<ChannelContext[]>([]);
  const [records, setRecords] = useState<ChannelRecord[]>([]);
  const [recordsTotal, setRecordsTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [recordPage, setRecordPage] = useState(1);
  const [activeTabKey, setActiveTabKey] = useState('active');

  const fetchChannels = useCallback(async () => {
    setLoading(true);
    try {
      const data = await channelApi.getServerChannelTable();
      setChannels(data);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchRecords = useCallback(async (page: number = 1) => {
    try {
      const data = await channelApi.getChannelRecord({ page, rows: 10 });
      setRecords(data?.data ?? []);
      setRecordsTotal(data?.total ?? 0);
      setRecordPage(page);
    } catch (error) {
      // Error handled by interceptor
    }
  }, []);

  useEffect(() => {
    fetchChannels();
    fetchRecords();

    // Subscribe to page refresh
    const unsub = wsClient.onPageRefresh('channel', () => {
      fetchChannels();
      fetchRecords(recordPage);
    });

    return unsub;
  }, [fetchChannels, fetchRecords, recordPage]);

  const handleSendHeartbeat = async (channelId: string) => {
    try {
      await channelApi.sendHeartBeat(channelId);
      message.success('心跳发送成功');
      fetchChannels();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleClearRecords = () => {
    Modal.confirm({
      title: '确认清空',
      content: '确定要清空所有断开记录吗？此操作无法恢复。',
      okText: '清空',
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          await channelApi.clearChannelRecord();
          message.success('记录已清空');
          fetchRecords(1);
        } catch (error) {
          // Error handled by interceptor
        }
      },
    });
  };

  const filteredChannels = channels.filter(
    (ch) =>
      ch.channelId.includes(searchText) || ch.clientIp.includes(searchText)
  );

  const channelColumns: ColumnsType<ChannelContext> = [
    {
      title: '隧道ID',
      dataIndex: 'channelId',
      key: 'channelId',
      ellipsis: true,
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
      title: '端口',
      dataIndex: 'clientPort',
      key: 'clientPort',
    },
    {
      title: '服务数',
      dataIndex: 'serviceCount',
      key: 'serviceCount',
    },
    {
      title: '最后心跳',
      dataIndex: 'lastHeartbeat',
      key: 'lastHeartbeat',
      width: 180,
      ellipsis: true,
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '状态',
      dataIndex: 'connected',
      key: 'connected',
      render: (connected: boolean) => (
        <Tag color={connected ? 'success' : 'default'} bordered={false}>
          {connected ? '已连接' : '已断开'}
        </Tag>
      ),
    },
    {
      title: '授权模式',
      dataIndex: 'authMode',
      key: 'authMode',
      render: (authMode: number) => (
        <Tag color={authMode === 1 ? 'processing' : 'default'} bordered={false}>
          {authMode === 1 ? 'FULL_AUTHORIZED' : 'SELF_MANAGED'}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      width: 180,
      fixed: 'right' as const,
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="link"
            size="small"
            style={{ padding: 0 }}
            disabled={!record.connected || record.authMode !== 1}
            onClick={() => navigate(`/management/serviceControl?clientId=${record.clientId}`)}
          >
            管控
          </Button>
          <Button
            type="link"
            size="small"
            style={{ padding: 0 }}
            onClick={() => handleSendHeartbeat(record.channelId)}
          >
            心跳
          </Button>
          <Popconfirm
            title="确认断开"
            description={`确定要断开隧道 ${record.channelId} 吗？`}
            onConfirm={async () => {
              try {
                await channelApi.closeChannelByServer(record.channelId);
                message.success('隧道已关闭');
                fetchChannels();
              } catch (error) {
                // Error handled by interceptor
              }
            }}
          >
            <Button type="text" danger size="small" style={{ padding: 0 }}>
              断开
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const recordColumns: ColumnsType<ChannelRecord> = [
    {
      title: '隧道ID',
      dataIndex: 'channelId',
      key: 'channelId',
      ellipsis: true,
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
      title: '断开时间',
      dataIndex: 'disconnectTime',
      key: 'disconnectTime',
      width: 180,
      ellipsis: true,
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '原因',
      dataIndex: 'reason',
      key: 'reason',
    },
  ];

  const tabBarExtra = activeTabKey === 'active' ? (
    <Space>
      <Input
        placeholder="搜索隧道ID或IP"
        prefix={<SearchOutlined />}
        value={searchText}
        onChange={(e) => setSearchText(e.target.value)}
        allowClear
        style={{ width: 240 }}
      />
      <Button icon={<ReloadOutlined />} onClick={fetchChannels}>
        刷新
      </Button>
    </Space>
  ) : (
    <Space>
      <Button
        danger
        icon={<DeleteOutlined />}
        onClick={handleClearRecords}
      >
        清空记录
      </Button>
      <Button icon={<ReloadOutlined />} onClick={() => fetchRecords(1)}>
        刷新
      </Button>
    </Space>
  );

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card bordered={false} style={{ borderRadius: 12 }}>
          <Tabs
            activeKey={activeTabKey}
            onChange={setActiveTabKey}
            tabBarExtraContent={tabBarExtra}
            items={[
              {
                key: 'active',
                label: '活跃隧道',
                children: (
                  <Table
                    columns={channelColumns}
                    dataSource={filteredChannels}
                    rowKey="channelId"
                    loading={loading}
                    pagination={false}
                    scroll={{ x: 'max-content' }}
                    locale={{ emptyText: <Empty description="暂无活跃隧道" /> }}
                  />
                ),
              },
              {
                key: 'history',
                label: '断开记录',
                children: (
                  <Table
                    columns={recordColumns}
                    dataSource={records}
                    rowKey="id"
                    scroll={{ x: 'max-content' }}
                    locale={{ emptyText: <Empty description="暂无断开记录" /> }}
                    pagination={{
                      current: recordPage,
                      total: recordsTotal,
                      pageSize: 10,
                      onChange: fetchRecords,
                      showSizeChanger: false,
                    }}
                  />
                ),
              },
            ]}
          />
        </Card>
      </motion.div>
    </motion.div>
  );
};

export default ChannelList;
