import React, { useCallback, useEffect, useMemo, useState } from 'react';
import { Button, Card, Empty, Input, InputNumber, Select, Space, Table, Tag, message, Modal, Form } from 'antd';
import { PlusOutlined, ReloadOutlined, SaveOutlined, DeleteOutlined, EditOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import { motion } from 'framer-motion';
import { useSearchParams } from 'react-router-dom';
import { channelApi } from '../../api/channel';
import { serviceControlApi } from '../../api/serviceControl';
import { ChannelContext, ControlledServiceState, ServiceDescription } from '../../types';
import { wsClient } from '../../utils/websocket';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';
import './index.css';

const FULL_AUTHORIZED = 1;



const ServiceControl: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [channels, setChannels] = useState<ChannelContext[]>([]);
  const [detail, setDetail] = useState<ControlledServiceState | null>(null);
  const [targetServices, setTargetServices] = useState<ServiceDescription[]>([]);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingIndex, setEditingIndex] = useState<number | null>(null);
  const [form] = Form.useForm();

  const selectedClientId = searchParams.get('clientId') ?? '';

  const fetchChannels = useCallback(async () => {
    const data = await channelApi.getServerChannelTable();
    setChannels(data);
    if (!selectedClientId) {
      const preferred = data.find((item) => item.online && item.authMode === FULL_AUTHORIZED);
      if (preferred) {
        setSearchParams({ clientId: preferred.clientId });
      }
    }
  }, [selectedClientId, setSearchParams]);

  const fetchDetail = useCallback(async (clientId: string) => {
    if (!clientId) {
      setDetail(null);
      setTargetServices([]);
      return;
    }
    setLoading(true);
    try {
      const data = await serviceControlApi.getClientControlledServiceList(clientId);
      setDetail(data);
      setTargetServices(data.targetServices ?? []);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchChannels();
    const unsub = wsClient.onPageRefresh('serviceControl', () => {
      fetchChannels();
      if (selectedClientId) {
        fetchDetail(selectedClientId);
      }
    });
    return unsub;
  }, [fetchChannels, fetchDetail, selectedClientId]);

  useEffect(() => {
    if (selectedClientId) {
      fetchDetail(selectedClientId);
    }
  }, [fetchDetail, selectedClientId]);

  const editable = detail?.online && detail.authMode === FULL_AUTHORIZED;

  const channelOptions = useMemo(
    () =>
      channels.map((item) => ({
        value: item.clientId,
        label: `${item.clientId} [${item.online ? '在线' : '离线'}] (${item.clientIp || '--'}:${item.clientPort || 0})`,
      })),
    [channels]
  );

  const handleModalOk = async () => {
    try {
      const values = await form.validateFields();
      setTargetServices((current) => {
        if (editingIndex !== null) {
          return current.map((item, idx) => (idx === editingIndex ? { ...item, ...values } : item));
        } else {
          return [...current, values as ServiceDescription];
        }
      });
      setModalVisible(false);
    } catch (e) {
      // validate failed
    }
  };

  const handleSave = async () => {
    if (!selectedClientId) {
      message.error('请先选择客户端');
      return;
    }

    const invalid = targetServices.some(
      (item) => !item.serviceName.trim() || !item.serviceIp.trim() || !item.servicePort
    );
    if (invalid) {
      message.error('服务名称、IP 和端口不能为空');
      return;
    }

    setSaving(true);
    try {
      await serviceControlApi.replaceClientControlledServices(
        selectedClientId,
        targetServices.map((item) => ({
          serviceName: item.serviceName,
          serviceIp: item.serviceIp,
          servicePort: item.servicePort,
          description: item.description,
        }))
      );
      message.success('目标服务清单已保存');
      fetchDetail(selectedClientId);
    } finally {
      setSaving(false);
    }
  };

  const targetColumns: ColumnsType<ServiceDescription> = [
    {
      title: '服务名称',
      dataIndex: 'serviceName',
      width: '25%',
    },
    {
      title: '服务IP',
      dataIndex: 'serviceIp',
      width: '25%',
    },
    {
      title: '服务端口',
      dataIndex: 'servicePort',
      width: 140,
    },
    {
      title: '描述',
      dataIndex: 'description',
    },
    {
      title: '操作',
      width: 140,
      fixed: 'right' as const,
      align: 'center',
      render: (_, record, index) => (
        <Space>
          <Button
            type="text"
            size="small"
            style={{ padding: 0 }}
            icon={<EditOutlined />}
            disabled={!editable}
            onClick={() => {
              setEditingIndex(index);
              form.setFieldsValue(record);
              setModalVisible(true);
            }}
          >
            编辑
          </Button>
          <Button
            danger
            type="text"
            size="small"
            style={{ padding: 0 }}
            icon={<DeleteOutlined />}
            disabled={!editable}
            onClick={() => setTargetServices((current) => current.filter((_, currentIndex) => currentIndex !== index))}
          >
            删除
          </Button>
        </Space>
      ),
    },
  ];

  const actualColumns: ColumnsType<ServiceDescription> = [
    { title: '服务名称', dataIndex: 'serviceName', width: '25%' },
    { title: '服务IP', dataIndex: 'serviceIp', width: '25%' },
    { title: '服务端口', dataIndex: 'servicePort', width: 140 },
    { title: '描述', dataIndex: 'description' },
  ];

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card
          title={<span style={{ fontSize: 16, fontWeight: 600 }}>全授权服务管控</span>}
          bordered={false}
          style={{ borderRadius: 12 }}
          extra={
            <Space>
              <Select
                placeholder="选择客户端"
                style={{ width: 360 }}
                value={selectedClientId || undefined}
                options={channelOptions}
                onChange={(value) => setSearchParams({ clientId: value })}
              />
              <Button icon={<ReloadOutlined />} onClick={() => {
                fetchChannels();
                if (selectedClientId) {
                  fetchDetail(selectedClientId);
                }
              }}>
                刷新
              </Button>
            </Space>
          }
        >
          {!selectedClientId || !detail ? (
            <Empty description="请在右上角选择一个需要管控的客户端" />
          ) : (
            <Space direction="vertical" size={24} style={{ width: '100%' }}>
              <div style={{ padding: '0 8px' }}>
                <Space>
                  <span style={{ color: '#646A73' }}>客户端状态：</span>
                  <Tag color={detail.online ? 'success' : 'error'} bordered={false}>
                    {detail.online ? '在线' : '离线'}
                  </Tag>
                  <Tag color={detail.authMode === FULL_AUTHORIZED ? 'processing' : 'default'} bordered={false}>
                    {detail.authMode === FULL_AUTHORIZED ? 'FULL_AUTHORIZED' : 'SELF_MANAGED'}
                  </Tag>
                  {!editable && <Tag color="warning" bordered={false}>仅在线且处于全授权模式的客户端可编辑目标服务</Tag>}
                </Space>
              </div>

              <Card
                title={<span style={{ fontWeight: 600 }}>目标服务清单</span>}
                style={{ borderRadius: 8, borderColor: '#EFF0F1', boxShadow: 'none' }}
                extra={
                  <Space>
                    <Button
                      icon={<PlusOutlined />}
                      disabled={!editable}
                      onClick={() => {
                        setEditingIndex(null);
                        form.resetFields();
                        setModalVisible(true);
                      }}
                    >
                      新增服务
                    </Button>
                    <Button type="primary" icon={<SaveOutlined />} loading={saving} disabled={!editable} onClick={handleSave}>
                      保存清单
                    </Button>
                  </Space>
                }
              >
                <Table<ServiceDescription>
                  className="service-control-table"
                  size="middle"
                  rowKey={(_, index) => `${selectedClientId}-target-${index}`}
                  columns={targetColumns}
                  dataSource={targetServices}
                  loading={loading}
                  pagination={false}
                  scroll={{ x: 800 }}
                  locale={{ emptyText: <Empty description="暂无目标服务" /> }}
                />
              </Card>

              <Card
                title={<span style={{ fontWeight: 600 }}>当前实际在线清单</span>}
                style={{ borderRadius: 8, borderColor: '#EFF0F1', boxShadow: 'none' }}
              >
                <Table<ServiceDescription>
                  className="service-control-table"
                  size="middle"
                  rowKey={(record) => `${record.serviceIp}-${record.servicePort}`}
                  columns={actualColumns}
                  dataSource={detail.actualServices ?? []}
                  loading={loading}
                  pagination={false}
                  locale={{ emptyText: <Empty description="客户端当前无实际上报的在线服务" /> }}
                />
              </Card>
            </Space>
          )}
        </Card>
      </motion.div>

      {/* Add/Edit Modal */}
      <Modal
        title={editingIndex !== null ? '编辑目标服务' : '新增目标服务'}
        open={modalVisible}
        onCancel={() => setModalVisible(false)}
        onOk={handleModalOk}
        destroyOnClose
        style={{ borderRadius: 12 }}
      >
        <Form form={form} layout="vertical" style={{ marginTop: 20 }}>
          <Form.Item
            name="serviceName"
            label="服务名称"
            rules={[{ required: true, message: '请输入服务名称' }]}
          >
            <Input placeholder="如 web-ui" />
          </Form.Item>
          <Form.Item
            name="serviceIp"
            label="服务IP"
            rules={[{ required: true, message: '请输入服务IP' }]}
          >
            <Input placeholder="如 127.0.0.1" />
          </Form.Item>
          <Form.Item
            name="servicePort"
            label="服务端口"
            rules={[{ required: true, message: '请输入服务端口' }]}
          >
            <InputNumber min={1} max={65535} style={{ width: '100%' }} placeholder="8080" />
          </Form.Item>
          <Form.Item
            name="description"
            label="描述"
          >
            <Input placeholder="服务描述" />
          </Form.Item>
        </Form>
      </Modal>
    </motion.div>
  );
};

export default ServiceControl;
