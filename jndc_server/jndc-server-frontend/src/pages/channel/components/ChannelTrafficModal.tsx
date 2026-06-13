import React from 'react';
import { Descriptions, Modal } from 'antd';
import { ChannelContext } from '../../../types';
import { formatBandwidth, formatDateTime, formatTrafficBytes } from '../utils';

interface ChannelTrafficModalProps {
  open: boolean;
  record: ChannelContext | null;
  onCancel: () => void;
}

const ChannelTrafficModal: React.FC<ChannelTrafficModalProps> = ({ open, record, onCancel }) => (
  <Modal
    title={record ? `流量统计 - ${record.clientId}` : '流量统计'}
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
        labelStyle={{ width: 200, whiteSpace: 'nowrap' }}
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
    )}
  </Modal>
);

export default ChannelTrafficModal;
