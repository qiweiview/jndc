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
  Select,
  InputNumber,
  message,
  Popconfirm,
} from 'antd';
import {
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  EditOutlined,
  SearchOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { motion } from 'framer-motion';
import { httpAppApi } from '../../api/httpApp';
import { portApi } from '../../api/port';
import { HostRouteRule, ServerPortBind } from '../../types';
import { wsClient } from '../../utils/websocket';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';
import dayjs from 'dayjs';

const { TextArea } = Input;

const routeTypeOptions = [
  { label: '转发', value: 1 },
  { label: '重定向', value: 2 },
  { label: '固定响应', value: 3 },
];

const HttpApp: React.FC = () => {
  const [rules, setRules] = useState<HostRouteRule[]>([]);
  const [ports, setPorts] = useState<ServerPortBind[]>([]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRule, setEditingRule] = useState<HostRouteRule | null>(null);

  const [form] = Form.useForm();

  const fetchRules = useCallback(async (page: number = 1) => {
    setLoading(true);
    try {
      const data = await httpAppApi.listHostRouteRule({ page, rows: 10 });
      setRules(data?.data ?? []);
      setTotal(data?.total ?? 0);
      setCurrentPage(page);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchPorts = useCallback(async () => {
    try {
      const data = await portApi.getServerPortList();
      setPorts(data.filter((p) => p.status === 1));
    } catch (error) {
      // Error handled by interceptor
    }
  }, []);

  useEffect(() => {
    fetchRules();
    fetchPorts();

    const unsub = wsClient.onPageRefresh('httpApp', () => {
      fetchRules(currentPage);
    });

    return unsub;
  }, [fetchRules, fetchPorts, currentPage]);

  const handleAdd = () => {
    setEditingRule(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: HostRouteRule) => {
    setEditingRule(record);
    form.setFieldsValue({
      hostKeyword: record.hostKeyword,
      routeType: record.routeType,
      routeTarget: record.routeTarget,
      responseContent: record.responseContent,
      responseCode: record.responseCode,
    });
    setModalVisible(true);
  };

  const handleSubmit = async (values: any) => {
    try {
      if (editingRule) {
        await httpAppApi.updateHostRouteRule({
          id: editingRule.id,
          ...values,
        });
        message.success('更新成功');
      } else {
        await httpAppApi.saveHostRouteRule(values);
        message.success('创建成功');
      }
      setModalVisible(false);
      form.resetFields();
      fetchRules(editingRule ? currentPage : 1);
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleDelete = async (id: number) => {
    try {
      await httpAppApi.deleteHostRouteRule(id);
      message.success('已删除');
      fetchRules(currentPage);
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const filteredRules = rules.filter(
    (r) =>
      r.hostKeyword.includes(searchText) ||
      r.routeTarget?.includes(searchText)
  );

  const columns: ColumnsType<HostRouteRule> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
      ellipsis: true,
    },
    {
      title: '域名关键词',
      dataIndex: 'hostKeyword',
      key: 'hostKeyword',
      ellipsis: true,
    },
    {
      title: '路由类型',
      dataIndex: 'routeType',
      key: 'routeType',
      render: (type: number) => {
        const option = routeTypeOptions.find((o) => o.value === type);
        const colorMap: Record<number, string> = { 1: 'blue', 2: 'orange', 3: 'green' };
        return <Tag color={colorMap[type]}>{option?.label}</Tag>;
      },
    },
    {
      title: '路由目标',
      dataIndex: 'routeTarget',
      key: 'routeTarget',
      ellipsis: true,
      render: (text: string) => text || '-',
    },
    {
      title: '响应码',
      dataIndex: 'responseCode',
      key: 'responseCode',
      render: (code: number) => code || '-',
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 180,
      ellipsis: true,
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: '操作',
      key: 'action',
      width: 150,
      fixed: 'right' as const,
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          <Popconfirm
            title="确定删除此路由规则？"
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

  const routeType = Form.useWatch('routeType', form);

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card
          title="HTTP应用 - 域名路由"
          extra={
            <Space>
              <Input
                placeholder="搜索域名或目标"
                prefix={<SearchOutlined />}
                value={searchText}
                onChange={(e) => setSearchText(e.target.value)}
                style={{ width: 250 }}
              />
              <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={handleAdd}
              >
                添加路由
              </Button>
              <Button icon={<ReloadOutlined />} onClick={() => fetchRules(currentPage)}>
                刷新
              </Button>
            </Space>
          }
        >
          <Table
            columns={columns}
            dataSource={filteredRules}
            rowKey="id"
            loading={loading}
            scroll={{ x: 800 }}
            pagination={{
              current: currentPage,
              total,
              pageSize: 10,
              onChange: fetchRules,
            }}
          />
        </Card>
      </motion.div>

      <Modal
        title={editingRule ? '编辑路由规则' : '添加路由规则'}
        open={modalVisible}
        onCancel={() => {
          setModalVisible(false);
          form.resetFields();
        }}
        onOk={() => form.submit()}
        width={600}
      >
        <Form form={form} onFinish={handleSubmit} layout="vertical">
          <Form.Item
            name="hostKeyword"
            label="域名关键词"
            rules={[{ required: true, message: '请输入域名关键词' }]}
          >
            <Input placeholder="例如: example.com" />
          </Form.Item>

          <Form.Item
            name="routeType"
            label="路由类型"
            rules={[{ required: true, message: '请选择路由类型' }]}
          >
            <Select options={routeTypeOptions} />
          </Form.Item>

          {routeType === 1 && (
            <Form.Item
              name="routeTarget"
              label="转发端口"
              rules={[{ required: true, message: '请选择转发端口' }]}
            >
              <Select placeholder="请选择端口">
                {ports.map((p) => (
                  <Select.Option key={p.id} value={p.port.toString()}>
                    端口 {p.port}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          )}

          {routeType === 2 && (
            <Form.Item
              name="routeTarget"
              label="重定向地址"
              rules={[{ required: true, message: '请输入重定向地址' }]}
            >
              <Input placeholder="https://example.com" />
            </Form.Item>
          )}

          {routeType === 3 && (
            <>
              <Form.Item
                name="responseCode"
                label="响应码"
                rules={[{ required: true, message: '请输入响应码' }]}
              >
                <InputNumber min={100} max={599} placeholder="200" style={{ width: '100%' }} />
              </Form.Item>
              <Form.Item
                name="responseContent"
                label="响应内容"
                rules={[{ required: true, message: '请输入响应内容' }]}
              >
                <TextArea rows={6} placeholder='{"message": "ok"}' />
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </motion.div>
  );
};

export default HttpApp;
