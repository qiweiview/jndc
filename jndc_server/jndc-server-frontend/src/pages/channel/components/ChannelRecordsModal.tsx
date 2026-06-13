import React from 'react';
import { Empty, Modal, Table } from 'antd';
import { ColumnsType } from 'antd/es/table';
import { ChannelContext, ChannelRecord } from '../../../types';
import { formatDateTime } from '../utils';

interface ChannelRecordsModalProps {
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

const ChannelRecordsModal: React.FC<ChannelRecordsModalProps> = ({
  open,
  record,
  recentRecords,
  recentRecordsLoading,
  onCancel,
}) => (
  <Modal
    title={record ? `最近断开记录 - ${record.clientId}` : '最近断开记录'}
    open={open}
    onCancel={onCancel}
    footer={null}
    width={800}
  >
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
  </Modal>
);

export default ChannelRecordsModal;
