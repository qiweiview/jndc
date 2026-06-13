import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Button,
  Space,
  Input,
  Select,
  Tag,
  message,
  Popconfirm,
  Empty,
} from 'antd';
import {
  ReloadOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { motion } from 'framer-motion';
import { channelApi } from '../../api/channel';
import { ChannelContext, ChannelRecord } from '../../types';
import { wsClient } from '../../utils/websocket';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';
import { useNavigate } from 'react-router-dom';
import ChannelDetailModal from './components/ChannelDetailModal';
import { formatDateTime, formatTrafficSummary } from './utils';

const ChannelList: React.FC = () => {
  const navigate = useNavigate();
  const [channels, setChannels] = useState<ChannelContext[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState<'all' | 'online' | 'offline'>('all');
  const [detailVisible, setDetailVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState<ChannelContext | null>(null);
  const [recentRecords, setRecentRecords] = useState<ChannelRecord[]>([]);
  const [recentRecordsLoading, setRecentRecordsLoading] = useState(false);

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

  const fetchRecentRecords = useCallback(async (clientId: string) => {
    if (!clientId) {
      setRecentRecords([]);
      return;
    }
    setRecentRecordsLoading(true);
    try {
      const data = await channelApi.getRecentChannelRecordByClientId(clientId);
      setRecentRecords(data ?? []);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setRecentRecordsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchChannels();

    const timer = window.setInterval(() => {
      fetchChannels();
    }, 3000);

    const unsub = wsClient.onPageRefresh('channel', () => {
      fetchChannels();
      if (detailVisible && currentRecord?.clientId) {
        fetchRecentRecords(currentRecord.clientId);
      }
    });

    return () => {
      window.clearInterval(timer);
      unsub();
    };
  }, [currentRecord?.clientId, detailVisible, fetchChannels, fetchRecentRecords]);

  useEffect(() => {
    if (!detailVisible || !currentRecord?.clientId) {
      return;
    }
    const latestRecord = channels.find((channel) => channel.clientId === currentRecord.clientId);
    if (latestRecord && latestRecord !== currentRecord) {
      setCurrentRecord(latestRecord);
    }
  }, [channels, currentRecord, detailVisible]);

  const handleSendHeartbeat = async (channelId: string) => {
    try {
      await channelApi.sendHeartBeat(channelId);
      message.success('心跳发送成功');
      fetchChannels();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const normalizedSearch = searchText.trim().toLowerCase();
  const filteredChannels = channels.filter((channel) => {
    if (statusFilter === 'online' && !channel.online) {
      return false;
    }
    if (statusFilter === 'offline' && channel.online) {
      return false;
    }
    if (!normalizedSearch) {
      return true;
    }
    const candidates = [
      channel.channelId,
      channel.clientId,
      channel.clientIp,
      channel.osName,
      channel.osVersion,
      channel.cpuModel,
    ];
    return candidates.some((value) => value?.toLowerCase().includes(normalizedSearch));
  });

  const channelColumns: ColumnsType<ChannelContext> = [
    {
      title: '状态',
      dataIndex: 'online',
      key: 'online',
      width: 100,
      render: (online: boolean) => (
        <Tag color={online ? 'success' : 'default'} bordered={false}>
          {online ? '在线' : '离线'}
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
      title: '服务数',
      dataIndex: 'serviceCount',
      key: 'serviceCount',
    },
    {
      title: '最后在线时间',
      dataIndex: 'lastSeenAt',
      key: 'lastSeenAt',
      width: 180,
      ellipsis: true,
      render: (value: number) => formatDateTime(value),
    },
    {
      title: '上行(client->server)',
      key: 'clientToServerTraffic',
      width: 180,
      render: (_, record) => formatTrafficSummary(record.clientToServerBandwidth, record.clientToServerBytes),
    },
    {
      title: '下行(server->client)',
      key: 'serverToClientTraffic',
      width: 180,
      render: (_, record) => formatTrafficSummary(record.serverToClientBandwidth, record.serverToClientBytes),
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
            onClick={async () => {
              setCurrentRecord(record);
              setDetailVisible(true);
              await fetchRecentRecords(record.clientId);
            }}
          >
            详情
          </Button>
          <Button
            type="link"
            size="small"
            style={{ padding: 0 }}
            disabled={!record.online || record.authMode !== 1}
            onClick={() => navigate(`/management/serviceControl?clientId=${record.clientId}`)}
          >
            管控
          </Button>
          <Button
            type="link"
            size="small"
            style={{ padding: 0 }}
            disabled={!record.online}
            onClick={() => handleSendHeartbeat(record.channelId)}
          >
            心跳
          </Button>
          <Popconfirm
            title="确认断开"
            description={`确定要断开隧道 ${record.channelId} 吗？`}
            disabled={!record.online}
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
            <Button type="text" danger size="small" style={{ padding: 0 }} disabled={!record.online}>
              断开
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  const toolbar = (
    <Space>
      <Select
        value={statusFilter}
        onChange={(value) => setStatusFilter(value)}
        style={{ width: 120 }}
        options={[
          { label: '全部状态', value: 'all' },
          { label: '仅在线', value: 'online' },
          { label: '仅离线', value: 'offline' },
        ]}
      />
      <Input
        placeholder="搜索客户端ID、IP、OS、CPU"
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
  );

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card
          bordered={false}
          style={{ borderRadius: 12 }}
          title="设备列表"
          extra={toolbar}
        >
          <Table
            columns={channelColumns}
            dataSource={filteredChannels}
            rowKey="clientId"
            loading={loading}
            pagination={false}
            scroll={{ x: 'max-content' }}
            locale={{ emptyText: <Empty description="暂无设备" /> }}
          />
        </Card>
      </motion.div>
      <ChannelDetailModal
        open={detailVisible}
        record={currentRecord}
        recentRecords={recentRecords}
        recentRecordsLoading={recentRecordsLoading}
        onCancel={() => setDetailVisible(false)}
      />
    </motion.div>
  );
};

export default ChannelList;
