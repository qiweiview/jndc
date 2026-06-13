import React from 'react';
import { Col, Descriptions, Empty, Modal, Row, Table, Tag } from 'antd';
import { ColumnsType } from 'antd/es/table';
import { ChannelContext, ChannelRecord } from '../../../types';
import {
  formatBandwidth,
  formatBytes,
  formatCpu,
  formatDateTime,
  formatOs,
  formatTrafficBytes,
} from '../utils';

interface ChannelDetailModalProps {
  open: boolean;
  record: ChannelContext | null;
  recentRecords: ChannelRecord[];
  recentRecordsLoading: boolean;
  onCancel: () => void;
}

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

const ChannelDetailModal: React.FC<ChannelDetailModalProps> = ({
  open,
  record,
  recentRecords,
  recentRecordsLoading,
  onCancel,
}) => (
  <Modal
    title={record ? `设备详情 - ${record.clientId}` : '设备详情'}
    open={open}
    onCancel={onCancel}
    footer={null}
    width={1100}
  >
    <Row gutter={24}>
      <Col span={11}>
        <div style={{ fontWeight: 500, fontSize: 14, marginBottom: 16 }}>设备基本信息</div>
        {record && (
          <Descriptions
            bordered
            column={1}
            size="small"
            style={{ marginBottom: 16 }}
            labelStyle={{ width: 110, whiteSpace: 'nowrap' }}
          >
            <Descriptions.Item label="状态">
              <Tag color={record.online ? 'success' : 'default'} bordered={false}>
                {record.online ? '在线' : '离线'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="隧道ID">
              <span style={{ wordBreak: 'break-all' }}>{record.channelId}</span>
            </Descriptions.Item>
            <Descriptions.Item label="客户端IP">{record.clientIp}:{record.clientPort}</Descriptions.Item>
            <Descriptions.Item label="服务数">{record.serviceCount}</Descriptions.Item>
            <Descriptions.Item label="授权模式">
              <Tag color={record.authMode === 1 ? 'processing' : 'default'} bordered={false}>
                {record.authMode === 1 ? 'FULL_AUTHORIZED' : 'SELF_MANAGED'}
              </Tag>
            </Descriptions.Item>
            <Descriptions.Item label="最后在线时间">{formatDateTime(record.lastSeenAt)}</Descriptions.Item>
            <Descriptions.Item label="最后离线时间">{formatDateTime(record.lastOfflineAt)}</Descriptions.Item>
            <Descriptions.Item label="操作系统">{formatOs(record)}</Descriptions.Item>
            <Descriptions.Item label="CPU">{formatCpu(record)}</Descriptions.Item>
            <Descriptions.Item label="GPU">
              {record.gpuNames?.length ? record.gpuNames.join(', ') : '--'}
            </Descriptions.Item>
            <Descriptions.Item label="总内存">{formatBytes(record.memoryTotalBytes)}</Descriptions.Item>
            <Descriptions.Item label="磁盘总量">{formatBytes(record.diskTotalBytes)}</Descriptions.Item>
            <Descriptions.Item label="磁盘可用">{formatBytes(record.diskFreeBytes)}</Descriptions.Item>
          </Descriptions>
        )}
        {record && (
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
                {formatBandwidth(record.clientToServerBandwidth)}
              </Descriptions.Item>
              <Descriptions.Item label="server -> client 当前带宽">
                {formatBandwidth(record.serverToClientBandwidth)}
              </Descriptions.Item>
              <Descriptions.Item label="client -> server 累计流量">
                {formatTrafficBytes(record.clientToServerBytes)}
              </Descriptions.Item>
              <Descriptions.Item label="server -> client 累计流量">
                {formatTrafficBytes(record.serverToClientBytes)}
              </Descriptions.Item>
              <Descriptions.Item label="最近更新时间">
                {formatDateTime(record.trafficUpdatedAt)}
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
);

export default ChannelDetailModal;
