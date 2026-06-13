import React from 'react';
import { Descriptions, Modal, Tag } from 'antd';
import { ChannelContext } from '../../../types';
import { formatBytes, formatCpu, formatDateTime, formatOs } from '../utils';

interface ChannelInfoModalProps {
  open: boolean;
  record: ChannelContext | null;
  onCancel: () => void;
}

const ChannelInfoModal: React.FC<ChannelInfoModalProps> = ({ open, record, onCancel }) => (
  <Modal
    title={record ? `设备信息 - ${record.clientId}` : '设备信息'}
    open={open}
    onCancel={onCancel}
    footer={null}
    width={600}
  >
    {record && (
      <Descriptions
        bordered
        column={1}
        size="small"
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
  </Modal>
);

export default ChannelInfoModal;
