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
} from 'antd';
import {
  ReloadOutlined,
  SearchOutlined,
  HeartOutlined,
  DisconnectOutlined,
  DeleteOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { channelApi } from '../../api/channel';
import { ChannelContext, ChannelRecord } from '../../types';
import { wsClient } from '../../utils/websocket';
import dayjs from 'dayjs';

const ChannelList: React.FC = () => {
  const [channels, setChannels] = useState<ChannelContext[]>([]);
  const [records, setRecords] = useState<ChannelRecord[]>([]);
  const [recordsTotal, setRecordsTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [recordPage, setRecordPage] = useState(1);

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
      const data = await channelApi.getChannelRecord({ page, pageSize: 10 });
      setRecords(data.list);
      setRecordsTotal(data.total);
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

  const handleCloseChannel = (channelId: string) => {
    Modal.confirm({
      title: '确认关闭',
      content: `确定要关闭隧道 ${channelId} 吗？`,
      onOk: async () => {
        try {
          await channelApi.closeChannelByServer(channelId);
          message.success('隧道已关闭');
          fetchChannels();
        } catch (error) {
          // Error handled by interceptor
        }
      },
    });
  };

  const handleClearRecords = () => {
    Modal.confirm({
      title: '确认清空',
      content: '确定要清空所有断开记录吗？',
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
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '状态',
      dataIndex: 'connected',
      key: 'connected',
      render: (connected: boolean) => (
        <Tag color={connected ? 'green' : 'red'}>
          {connected ? '已连接' : '已断开'}
        </Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<HeartOutlined />}
            onClick={() => handleSendHeartbeat(record.channelId)}
          >
            心跳
          </Button>
          <Button
            type="link"
            danger
            icon={<DisconnectOutlined />}
            onClick={() => handleCloseChannel(record.channelId)}
          >
            断开
          </Button>
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
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '原因',
      dataIndex: 'reason',
      key: 'reason',
    },
  ];

  return (
    <div>
      <Tabs
        defaultActiveKey="active"
        items={[
          {
            key: 'active',
            label: '活跃隧道',
            children: (
              <Card
                extra={
                  <Space>
                    <Input
                      placeholder="搜索隧道ID或IP"
                      prefix={<SearchOutlined />}
                      value={searchText}
                      onChange={(e) => setSearchText(e.target.value)}
                      style={{ width: 250 }}
                    />
                    <Button
                      icon={<ReloadOutlined />}
                      onClick={fetchChannels}
                    >
                      刷新
                    </Button>
                  </Space>
                }
              >
                <Table
                  columns={channelColumns}
                  dataSource={filteredChannels}
                  rowKey="channelId"
                  loading={loading}
                  pagination={false}
                />
              </Card>
            ),
          },
          {
            key: 'history',
            label: '断开记录',
            children: (
              <Card
                extra={
                  <Button
                    danger
                    icon={<DeleteOutlined />}
                    onClick={handleClearRecords}
                  >
                    清空记录
                  </Button>
                }
              >
                <Table
                  columns={recordColumns}
                  dataSource={records}
                  rowKey="id"
                  pagination={{
                    current: recordPage,
                    total: recordsTotal,
                    pageSize: 10,
                    onChange: fetchRecords,
                  }}
                />
              </Card>
            ),
          },
        ]}
      />
    </div>
  );
};

export default ChannelList;
