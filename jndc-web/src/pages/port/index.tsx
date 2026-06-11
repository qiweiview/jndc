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
} from 'antd';
import {
  ReloadOutlined,
  PlusOutlined,
  SearchOutlined,
  PlayCircleOutlined,
  PauseCircleOutlined,
  DeleteOutlined,
  LinkOutlined,
  DisconnectOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { portApi } from '../../api/port';
import { serviceApi } from '../../api/service';
import { ServerPortBind, ServiceDescription } from '../../types';
import { wsClient } from '../../utils/websocket';
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

  const handleBind = async (values: { routeTo: string }) => {
    if (!currentPort) return;
    try {
      await portApi.doServiceBind({
        id: currentPort.id,
        routeTo: values.routeTo,
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

  const handleStopBind = async (id: number) => {
    try {
      await portApi.stopServiceBind(id);
      message.success('已停止绑定');
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleResetBind = async (id: number) => {
    try {
      await portApi.resetBindRecord(id);
      message.success('已重置绑定');
      fetchPorts();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleDelete = async (id: number) => {
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
      render: (text: string) => text || '-',
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: number) => (
        <Tag color={status === 1 ? 'green' : 'default'}>
          {status === 1 ? '已绑定' : '未绑定'}
        </Tag>
      ),
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
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 300,
      render: (_, record) => (
        <Space>
          {record.status === 0 ? (
            <Button
              type="link"
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
              <Button
                type="link"
                icon={<PauseCircleOutlined />}
                onClick={() => handleStopBind(record.id)}
              >
                停止
              </Button>
              <Button
                type="link"
                onClick={() => handleResetBind(record.id)}
              >
                重置
              </Button>
            </>
          )}
          <Button
            type="link"
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
            title="确定删除此端口监听？"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <>
      <Card
        title="端口监听"
        extra={
          <Space>
            <Input
              placeholder="搜索端口或转发目标"
              prefix={<SearchOutlined />}
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              style={{ width: 250 }}
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
        />
      </Card>

      {/* Create Modal */}
      <Modal
        title="添加端口监听"
        open={createModalVisible}
        onCancel={() => {
          setCreateModalVisible(false);
          createForm.resetFields();
        }}
        onOk={() => createForm.submit()}
      >
        <Form form={createForm} onFinish={handleCreate} layout="vertical">
          <Form.Item
            name="port"
            label="监听端口"
            rules={[{ required: true, message: '请输入端口号' }]}
          >
            <Input type="number" placeholder="请输入端口号" />
          </Form.Item>
          <Form.Item name="timeRange" label="时间范围（可选）">
            <TimePicker.RangePicker format="HH:mm" />
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
      >
        <Form form={bindForm} onFinish={handleBind} layout="vertical">
          <Form.Item
            name="routeTo"
            label="选择服务"
            rules={[{ required: true, message: '请选择要绑定的服务' }]}
          >
            <Select placeholder="请选择服务">
              {services.map((s) => (
                <Select.Option
                  key={`${s.clientId}-${s.serviceIp}-${s.servicePort}`}
                  value={`${s.serviceIp}:${s.servicePort}`}
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
      >
        <Form form={editForm} onFinish={handleEditTimeRange} layout="vertical">
          <Form.Item
            name="timeRange"
            label="时间范围"
            rules={[{ required: true, message: '请选择时间范围' }]}
          >
            <TimePicker.RangePicker format="HH:mm" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default PortList;
