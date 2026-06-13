import React, { useEffect, useState, useCallback } from 'react';
import {
  Table,
  Card,
  Button,
  Space,
  Input,
  Tag,
  Modal,
  Form,
  TimePicker,
  Select,
  message,
  Popconfirm,
  Empty
} from 'antd';
import {
  ReloadOutlined,
  PlusOutlined,
  SearchOutlined,
  PauseCircleOutlined,
  DeleteOutlined,
  LinkOutlined,
  ClockCircleOutlined,
  UndoOutlined
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { motion } from 'framer-motion';
import { portApi } from '../../api/port';
import { serviceApi } from '../../api/service';
import { ServerPortBind, ServiceDescription } from '../../types';
import { wsClient } from '../../utils/websocket';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';
import dayjs from 'dayjs';

const PortList: React.FC = () => {
  const [ports, setPorts] = useState<ServerPortBind[]>([]);
  const [services, setServices] = useState<ServiceDescription[]>([]);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [createModalVisible, setCreateModalVisible] = useState(false);
  const [bindModalVisible, setBindModalVisible] = useState(false);
  const [editModalVisible, setEditModalVisible] = useState(false);
  const [currentPort, setCurrentPort] = useState<ServerPortBind | null>(null);

  const [createForm] = Form.useForm();
  const [bindForm] = Form.useForm();
  const [editForm] = Form.useForm();

  const buildRouteTo = useCallback((service: ServiceDescription) => {
    return `${service.clientId}->${service.serviceIp}:${service.servicePort}`;
  }, []);

  const fetchPorts = useCallback(async () => {
    setLoading(true);
    try {
      const data = await portApi.getServerPortList();
      setPorts(data);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchServices = useCallback(async () => {
    try {
      const data = await serviceApi.getServiceList();
      setServices(data);
    } catch (error) {
      // Error handled by interceptor
    }
  }, []);

  useEffect(() => {
    fetchPorts();
    fetchServices();

    const unsub = wsClient.onPageRefresh('serverPortList', fetchPorts);
    return unsub;
  }, [fetchPorts, fetchServices]);

  const handleCreate = async (values: { port: number; timeRange?: dayjs.Dayjs[] }) => {
    try {
      await portApi.createPortMonitoring({
        port: values.port,
        timeRangeStart: values.timeRange?.[0]?.format('HH:mm'),
        timeRangeEnd: values.timeRange?.[1]?.format('HH:mm'),
      });
      message.success('端口监听创建成功');
      setCreateModalVisible(false);
      createForm.resetFields();
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleBind = async (values: { serviceKey: string }) => {
    if (!currentPort) return;
    const selectedService = services.find(
      (service) => (service.id ?? buildRouteTo(service)) === values.serviceKey
    );
    if (!selectedService) {
      message.error('未找到要绑定的服务');
      return;
    }
    try {
      await portApi.doServiceBind({
        id: currentPort.id,
        serviceId: selectedService.id,
        routeTo: buildRouteTo(selectedService),
      });
      message.success('服务绑定成功');
      setBindModalVisible(false);
      bindForm.resetFields();
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleEditTimeRange = async (values: { timeRange: dayjs.Dayjs[] }) => {
    if (!currentPort) return;
    try {
      await portApi.doDateRangeEdit({
        id: currentPort.id,
        timeRangeStart: values.timeRange[0].format('HH:mm'),
        timeRangeEnd: values.timeRange[1].format('HH:mm'),
      });
      message.success('时间范围更新成功');
      setEditModalVisible(false);
      editForm.resetFields();
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleStopBind = async (id: string) => {
    try {
      await portApi.stopServiceBind(id);
      message.success('已停止绑定');
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleResetBind = async (id: string) => {
    try {
      await portApi.resetBindRecord(id);
      message.success('已重置绑定');
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await portApi.deleteServiceBindRecord(id);
      message.success('已删除');
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const filteredPorts = ports.filter(
    (p) =>
      p.port.toString().includes(searchText) ||
      p.routeTo?.includes(searchText)
  );

  const columns: ColumnsType<ServerPortBind> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      ellipsis: true,
    },
    {
      title: '监听端口',
      dataIndex: 'port',
      key: 'port',
    },
    {
      title: '转发目标',
      dataIndex: 'routeTo',
      key: 'routeTo',
      ellipsis: true,
      render: (text: string) => text || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => {
        const color = status === 1 ? 'success' : status === 2 ? 'processing' : 'default';
        const text = status === 1 ? '已绑定' : status === 2 ? '绑定中' : '未绑定';
        return <Tag color={color} bordered={false}>{text}</Tag>;
      },
    },
    {
      title: '时间范围',
      key: 'timeRange',
      render: (_, record) => {
        if (record.timeRangeStart && record.timeRangeEnd) {
          return `${record.timeRangeStart} - ${record.timeRangeEnd}`;
        }
        return '全天';
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      ellipsis: true,
      render: (text?: string) => (text ? dayjs(text).format('YYYY-MM-DD HH:mm:ss') : '-'),
    },
    {
      title: '操作',
      key: 'action',
      width: 260,
      fixed: 'right' as const,
      render: (_, record) => (
        <Space size="middle">
          {record.status !== 1 ? (
            <Button
              type="link"
              size="small"
              style={{ padding: 0 }}
              icon={<LinkOutlined />}
              onClick={() => {
                setCurrentPort(record);
                setBindModalVisible(true);
              }}
            >
              绑定
            </Button>
          ) : (
            <>
              <Popconfirm
                title="停止绑定"
                description="确定要停止此端口的绑定吗？"
                onConfirm={() => handleStopBind(record.id)}
              >
                <Button type="text" danger size="small" style={{ padding: 0 }} icon={<PauseCircleOutlined />}>
                  停止
                </Button>
              </Popconfirm>
              <Popconfirm
                title="重置绑定"
                description="确定要重置此端口绑定吗？"
                onConfirm={() => handleResetBind(record.id)}
              >
                <Button type="text" danger size="small" style={{ padding: 0 }} icon={<UndoOutlined />}>
                  重置
                </Button>
              </Popconfirm>
            </>
          )}
          <Button
            type="link"
            size="small"
            style={{ padding: 0 }}
            icon={<ClockCircleOutlined />}
            onClick={() => {
              setCurrentPort(record);
              editForm.setFieldsValue({
                timeRange: record.timeRangeStart
                  ? [dayjs(record.timeRangeStart, 'HH:mm'), dayjs(record.timeRangeEnd, 'HH:mm')]
                  : undefined,
              });
              setEditModalVisible(true);
            }}
          >
            时间
          </Button>
          <Popconfirm
            title="确定删除"
            description="确定删除此端口监听吗？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="text" danger size="small" style={{ padding: 0 }} icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card
          title={<span style={{ fontSize: 16, fontWeight: 600 }}>端口监听</span>}
          bordered={false}
          style={{ borderRadius: 12 }}
          extra={
            <Space>
              <Input
                placeholder="搜索端口或转发目标"
                prefix={<SearchOutlined />}
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                allowClear
                style={{ width: 240 }}
              />
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setCreateModalVisible(true)}
              >
                添加端口
              </Button>
              <Button icon={<ReloadOutlined />} onClick={fetchPorts}>
                刷新
              </Button>
            </Space>
          }
        >
          <Table
            columns={columns}
            dataSource={filteredPorts}
            rowKey="id"
            loading={loading}
            pagination={false}
            scroll={{ x: 'max-content' }}
            locale={{ emptyText: <Empty description="暂无监听端口" /> }}
          />
        </Card>
      </motion.div>

      {/* Create Modal */}
      <Modal
        title="添加端口监听"
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          createForm.resetFields();
        }}
        onOk={() => createForm.submit()}
        destroyOnClose
        style={{ borderRadius: 12 }}
      >
        <Form form={createForm} onFinish={handleCreate} layout="vertical" style={{ marginTop: 20 }}>
          <Form.Item
            name="port"
            label="监听端口"
            rules={[{ required: true, message: '请输入端口号' }]}
          >
            <Input type="number" placeholder="请输入端口号" />
          </Form.Item>
          <Form.Item name="timeRange" label="时间范围（可选）">
            <TimePicker.RangePicker format="HH:mm" style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>

      {/* Bind Modal */}
      <Modal
        title="绑定服务"
        open={bindModalVisible}
        onCancel={() => {
          setBindModalVisible(false);
          bindForm.resetFields();
        }}
        onOk={() => bindForm.submit()}
        destroyOnClose
        style={{ borderRadius: 12 }}
      >
        <Form form={bindForm} onFinish={handleBind} layout="vertical" style={{ marginTop: 20 }}>
          <Form.Item
            name="serviceKey"
            label="选择服务"
            rules={[{ required: true, message: '请选择要绑定的服务' }]}
          >
            <Select placeholder="请选择服务">
              {services.map((s) => (
                <Select.Option
                  key={`${s.clientId}-${s.serviceIp}-${s.servicePort}`}
                  value={s.id ?? buildRouteTo(s)}
                >
                  {s.serviceName} ({s.serviceIp}:{s.servicePort})
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
        </Form>
      </Modal>

      {/* Edit Time Range Modal */}
      <Modal
        title="编辑时间范围"
        open={editModalVisible}
        onCancel={() => {
          setEditModalVisible(false);
          editForm.resetFields();
        }}
        onOk={() => editForm.submit()}
        destroyOnClose
        style={{ borderRadius: 12 }}
      >
        <Form form={editForm} onFinish={handleEditTimeRange} layout="vertical" style={{ marginTop: 20 }}>
          <Form.Item
            name="timeRange"
            label="时间范围"
            rules={[{ required: true, message: '请选择时间范围' }]}
          >
            <TimePicker.RangePicker format="HH:mm" style={{ width: '100%' }} />
          </Form.Item>
        </Form>
      </Modal>
    </motion.div>
  );
};

export default PortList;
