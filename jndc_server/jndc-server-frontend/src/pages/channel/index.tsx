import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Button,
  Space,
  Input,
  Select,
  Tag,
  Modal,
  message,
  Popconfirm,
  Empty,
  Descriptions,
  Row,
  Col
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
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';

const formatDateTime = (value?: number) => {
  if (!value || value <= 0) {
    return '--';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
};

const formatBytes = (value?: number, zeroText = '--') => {
  if (value === undefined || value === null || value < 0) {
    return '--';
  }
  if (value === 0) {
    return zeroText;
  }
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = value;
  let unitIndex = 0;
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex += 1;
  }
  return `${size.toFixed(size >= 10 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`;
};

const formatTrafficBytes = (value?: number) => formatBytes(value, '0 B');

const formatBandwidth = (value?: number) => `${formatTrafficBytes(value)}/s`;

const formatTrafficSummary = (bandwidth?: number, totalBytes?: number) => (
  `${formatBandwidth(bandwidth)} / ${formatTrafficBytes(totalBytes)}`
);

const formatOs = (record: ChannelContext) => {
  const parts = [record.osName, record.osVersion].filter(Boolean);
  return parts.length > 0 ? parts.join(' ') : '--';
};

const formatCpu = (record: ChannelContext) => {
  if (!record.cpuModel) {
    return '--';
  }
  return record.cpuLogicalCores > 0
    ? `${record.cpuModel} / ${record.cpuLogicalCores} 线程`
    : record.cpuModel;
};

const formatDisk = (record: ChannelContext) => {
  if (!record.diskTotalBytes) {
    return '--';
  }
  return `${formatBytes(record.diskFreeBytes)} 可用 / ${formatBytes(record.diskTotalBytes)}`;
};

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

  const recordColumns: ColumnsType<ChannelRecord> = [
    {
      title: '断开时间',
      dataIndex: 'timeStamp',
      key: 'timeStamp',
      width: 160,
      render: (value: number) => formatDateTime(value),
    },
    {
      title: '客户端IP',
      dataIndex: 'ip',
      key: 'ip',
    },
    {
      title: '端口',
      dataIndex: 'port',
      key: 'port',
      width: 80,
    },
    {
      title: '原因',
      dataIndex: 'disconnectReason',
      key: 'disconnectReason',
      render: (value: string) => value || '--',
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

      <Modal
        title={currentRecord ? `设备详情 - ${currentRecord.clientId}` : "设备详情"}
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={1100}
      >
        <Row gutter={24}>
          <Col span={11}>
            <div style={{ fontWeight: 500, fontSize: 14, marginBottom: 16 }}>设备基本信息</div>
            {currentRecord && (
              <Descriptions 
                bordered 
                column={1} 
                size="small" 
                style={{ marginBottom: 16 }}
                labelStyle={{ width: 110, whiteSpace: 'nowrap' }}
              >
                <Descriptions.Item label="状态">
                  <Tag color={currentRecord.online ? 'success' : 'default'} bordered={false}>
                    {currentRecord.online ? '在线' : '离线'}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="隧道ID">
                  <span style={{ wordBreak: 'break-all' }}>{currentRecord.channelId}</span>
                </Descriptions.Item>
                <Descriptions.Item label="客户端IP">{currentRecord.clientIp}:{currentRecord.clientPort}</Descriptions.Item>
                <Descriptions.Item label="服务数">{currentRecord.serviceCount}</Descriptions.Item>
                <Descriptions.Item label="授权模式">
                  <Tag color={currentRecord.authMode === 1 ? 'processing' : 'default'} bordered={false}>
                    {currentRecord.authMode === 1 ? 'FULL_AUTHORIZED' : 'SELF_MANAGED'}
                  </Tag>
                </Descriptions.Item>
                <Descriptions.Item label="最后在线时间">{formatDateTime(currentRecord.lastSeenAt)}</Descriptions.Item>
                <Descriptions.Item label="最后离线时间">{formatDateTime(currentRecord.lastOfflineAt)}</Descriptions.Item>
                <Descriptions.Item label="操作系统">{formatOs(currentRecord)}</Descriptions.Item>
                <Descriptions.Item label="CPU">{formatCpu(currentRecord)}</Descriptions.Item>
                <Descriptions.Item label="GPU">
                  {currentRecord.gpuNames?.length ? currentRecord.gpuNames.join(', ') : '--'}
                </Descriptions.Item>
                <Descriptions.Item label="总内存">{formatBytes(currentRecord.memoryTotalBytes)}</Descriptions.Item>
                <Descriptions.Item label="磁盘总量">{formatBytes(currentRecord.diskTotalBytes)}</Descriptions.Item>
                <Descriptions.Item label="磁盘可用">{formatBytes(currentRecord.diskFreeBytes)}</Descriptions.Item>
              </Descriptions>
            )}
            {currentRecord && (
              <>
                <div style={{ fontWeight: 500, fontSize: 14, marginBottom: 16 }}>流量统计</div>
                <Descriptions
                  bordered
                  column={1}
                  size="small"
                  style={{ marginBottom: 16 }}
                  labelStyle={{ width: 150, whiteSpace: 'nowrap' }}
                >
                  <Descriptions.Item label="client -> server 当前带宽">
                    {formatBandwidth(currentRecord.clientToServerBandwidth)}
                  </Descriptions.Item>
                  <Descriptions.Item label="server -> client 当前带宽">
                    {formatBandwidth(currentRecord.serverToClientBandwidth)}
                  </Descriptions.Item>
                  <Descriptions.Item label="client -> server 累计流量">
                    {formatTrafficBytes(currentRecord.clientToServerBytes)}
                  </Descriptions.Item>
                  <Descriptions.Item label="server -> client 累计流量">
                    {formatTrafficBytes(currentRecord.serverToClientBytes)}
                  </Descriptions.Item>
                  <Descriptions.Item label="最近更新时间">
                    {formatDateTime(currentRecord.trafficUpdatedAt)}
                  </Descriptions.Item>
                </Descriptions>
              </>
            )}
          </Col>
          <Col span={13}>
            <div style={{ fontWeight: 500, fontSize: 14, marginBottom: 16 }}>最近断开记录</div>
            <Table
              columns={recordColumns}
              dataSource={recentRecords}
              rowKey="id"
              size="small"
              loading={recentRecordsLoading}
              pagination={false}
              locale={{ emptyText: <Empty description="暂无断开记录" /> }}
              scroll={{ y: 500 }}
            />
          </Col>
        </Row>
      </Modal>
    </motion.div>
  );
};

export default ChannelList;
