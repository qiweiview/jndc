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
  Tabs,
  message,
  Popconfirm,
} from 'antd';
import {
  ReloadOutlined,
  PlusOutlined,
  DeleteOutlined,
  ClearOutlined,
  GlobalOutlined,
} from '@ant-design/icons';
import { ColumnsType } from 'antd/es/table';
import { ipFilterApi } from '../../api/ipFilter';
import { IpRule, IpRecord } from '../../types';
import { wsClient } from '../../utils/websocket';
import dayjs from 'dayjs';

const IpFilter: React.FC = () => {
  const [blacklist, setBlacklist] = useState<IpRule[]>([]);
  const [whitelist, setWhitelist] = useState<IpRule[]>([]);
  const [blockRecords, setBlockRecords] = useState<IpRecord[]>([]);
  const [releaseRecords, setReleaseRecords] = useState<IpRecord[]>([]);
  const [blacklistTotal, setBlacklistTotal] = useState(0);
  const [whitelistTotal, setWhitelistTotal] = useState(0);
  const [blockRecordsTotal, setBlockRecordsTotal] = useState(0);
  const [releaseRecordsTotal, setReleaseRecordsTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [addBlackModalVisible, setAddBlackModalVisible] = useState(false);
  const [addWhiteModalVisible, setAddWhiteModalVisible] = useState(false);
  const [currentDeviceIp, setCurrentDeviceIp] = useState('');

  const [blackForm] = Form.useForm();
  const [whiteForm] = Form.useForm();

  const fetchBlacklist = useCallback(async (page: number = 1) => {
    setLoading(true);
    try {
      const data = await ipFilterApi.getBlackList({ page, pageSize: 10 });
      setBlacklist(data.list);
      setBlacklistTotal(data.total);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchWhitelist = useCallback(async (page: number = 1) => {
    setLoading(true);
    try {
      const data = await ipFilterApi.getWhiteList({ page, pageSize: 10 });
      setWhitelist(data.list);
      setWhitelistTotal(data.total);
    } catch (error) {
      // Error handled by interceptor
    } finally {
      setLoading(false);
    }
  }, []);

  const fetchBlockRecords = useCallback(async (page: number = 1) => {
    try {
      const data = await ipFilterApi.getBlockRecord({ page, pageSize: 10 });
      setBlockRecords(data.list);
      setBlockRecordsTotal(data.total);
    } catch (error) {
      // Error handled by interceptor
    }
  }, []);

  const fetchReleaseRecords = useCallback(async (page: number = 1) => {
    try {
      const data = await ipFilterApi.getReleaseRecord({ page, pageSize: 10 });
      setReleaseRecords(data.list);
      setReleaseRecordsTotal(data.total);
    } catch (error) {
      // Error handled by interceptor
    }
  }, []);

  const fetchCurrentIp = useCallback(async () => {
    try {
      const ip = await ipFilterApi.getCurrentDeviceIp();
      setCurrentDeviceIp(ip);
    } catch (error) {
      // Error handled by interceptor
    }
  }, []);

  useEffect(() => {
    fetchBlacklist();
    fetchWhitelist();
    fetchBlockRecords();
    fetchReleaseRecords();
    fetchCurrentIp();

    const unsub = wsClient.onPageRefresh('ipFilter', () => {
      fetchBlacklist();
      fetchWhitelist();
      fetchBlockRecords();
      fetchReleaseRecords();
    });

    return unsub;
  }, [fetchBlacklist, fetchWhitelist, fetchBlockRecords, fetchReleaseRecords, fetchCurrentIp]);

  const handleAddToBlacklist = async (values: { ip: string }) => {
    try {
      await ipFilterApi.addToBlackList(values.ip);
      message.success('已添加到黑名单');
      setAddBlackModalVisible(false);
      blackForm.resetFields();
      fetchBlacklist();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleAddToWhitelist = async (values: { ip: string }) => {
    try {
      await ipFilterApi.addToWhiteList(values.ip);
      message.success('已添加到白名单');
      setAddWhiteModalVisible(false);
      whiteForm.resetFields();
      fetchWhitelist();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleDeleteRule = async (id: number) => {
    try {
      await ipFilterApi.deleteIpRule(id);
      message.success('已删除');
      fetchBlacklist();
      fetchWhitelist();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const handleClearRecords = async (type: 'block' | 'release') => {
    Modal.confirm({
      title: '清空记录',
      content: '选择清空方式',
      okText: '保留最新10条',
      cancelText: '全部清空',
      onOk: async () => {
        try {
          await ipFilterApi.clearIpRecord({ keepTop10: true });
          message.success('已清空记录');
          fetchBlockRecords();
          fetchReleaseRecords();
        } catch (error) {
          // Error handled by interceptor
        }
      },
      onCancel: async () => {
        try {
          await ipFilterApi.clearIpRecord({});
          message.success('已清空所有记录');
          fetchBlockRecords();
          fetchReleaseRecords();
        } catch (error) {
          // Error handled by interceptor
        }
      },
    });
  };

  const handleAddCurrentIpToWhitelist = async () => {
    if (!currentDeviceIp) return;
    try {
      await ipFilterApi.addToWhiteList(currentDeviceIp);
      message.success('当前IP已添加到白名单');
      fetchWhitelist();
    } catch (error) {
      // Error handled by interceptor
    }
  };

  const ruleColumns: ColumnsType<IpRule> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'IP地址',
      dataIndex: 'ip',
      key: 'ip',
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
      render: (_, record) => (
        <Popconfirm
          title="确定删除此规则？"
          onConfirm={() => handleDeleteRule(record.id)}
        >
          <Button type="link" danger icon={<DeleteOutlined />}>
            删除
          </Button>
        </Popconfirm>
      ),
    },
  ];

  const recordColumns: ColumnsType<IpRecord> = [
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: 'IP地址',
      dataIndex: 'ip',
      key: 'ip',
    },
    {
      title: '访问次数',
      dataIndex: 'accessCount',
      key: 'accessCount',
    },
    {
      title: '最后访问时间',
      dataIndex: 'lastAccessTime',
      key: 'lastAccessTime',
      render: (text: string) => dayjs(text).format('YYYY-MM-DD HH:mm:ss'),
    },
  ];

  return (
    <>
      <Card
        title="IP访问管控"
        extra={
          currentDeviceIp && (
            <Space>
              <span>当前设备IP: <Tag color="blue">{currentDeviceIp}</Tag></span>
              <Button
                icon={<GlobalOutlined />}
                onClick={handleAddCurrentIpToWhitelist}
              >
                添加到白名单
              </Button>
            </Space>
          )
        }
      >
        <Tabs
          defaultActiveKey="blacklist"
          items={[
            {
              key: 'blacklist',
              label: `黑名单 (${blacklistTotal})`,
              children: (
                <>
                  <div style={{ marginBottom: 16 }}>
                    <Button
                      type="primary"
                      danger
                      icon={<PlusOutlined />}
                      onClick={() => setAddBlackModalVisible(true)}
                    >
                      添加黑名单
                    </Button>
                  </div>
                  <Table
                    columns={ruleColumns}
                    dataSource={blacklist}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                      total: blacklistTotal,
                      pageSize: 10,
                      onChange: fetchBlacklist,
                    }}
                  />
                </>
              ),
            },
            {
              key: 'whitelist',
              label: `白名单 (${whitelistTotal})`,
              children: (
                <>
                  <div style={{ marginBottom: 16 }}>
                    <Button
                      type="primary"
                      icon={<PlusOutlined />}
                      onClick={() => setAddWhiteModalVisible(true)}
                    >
                      添加白名单
                    </Button>
                  </div>
                  <Table
                    columns={ruleColumns}
                    dataSource={whitelist}
                    rowKey="id"
                    loading={loading}
                    pagination={{
                      total: whitelistTotal,
                      pageSize: 10,
                      onChange: fetchWhitelist,
                    }}
                  />
                </>
              ),
            },
            {
              key: 'blocked',
              label: `阻止记录 (${blockRecordsTotal})`,
              children: (
                <>
                  <div style={{ marginBottom: 16 }}>
                    <Button
                      danger
                      icon={<ClearOutlined />}
                      onClick={() => handleClearRecords('block')}
                    >
                      清空记录
                    </Button>
                  </div>
                  <Table
                    columns={recordColumns}
                    dataSource={blockRecords}
                    rowKey="id"
                    pagination={{
                      total: blockRecordsTotal,
                      pageSize: 10,
                      onChange: fetchBlockRecords,
                    }}
                  />
                </>
              ),
            },
            {
              key: 'released',
              label: `允许记录 (${releaseRecordsTotal})`,
              children: (
                <>
                  <div style={{ marginBottom: 16 }}>
                    <Button
                      danger
                      icon={<ClearOutlined />}
                      onClick={() => handleClearRecords('release')}
                    >
                      清空记录
                    </Button>
                  </div>
                  <Table
                    columns={recordColumns}
                    dataSource={releaseRecords}
                    rowKey="id"
                    pagination={{
                      total: releaseRecordsTotal,
                      pageSize: 10,
                      onChange: fetchReleaseRecords,
                    }}
                  />
                </>
              ),
            },
          ]}
        />
      </Card>

      {/* Add Blacklist Modal */}
      <Modal
        title="添加黑名单"
        open={addBlackModalVisible}
        onCancel={() => {
          setAddBlackModalVisible(false);
          blackForm.resetFields();
        }}
        onOk={() => blackForm.submit()}
      >
        <Form form={blackForm} onFinish={handleAddToBlacklist} layout="vertical">
          <Form.Item
            name="ip"
            label="IP地址"
            rules={[{ required: true, message: '请输入IP地址' }]}
          >
            <Input placeholder="请输入IP地址" />
          </Form.Item>
        </Form>
      </Modal>

      {/* Add Whitelist Modal */}
      <Modal
        title="添加白名单"
        open={addWhiteModalVisible}
        onCancel={() => {
          setAddWhiteModalVisible(false);
          whiteForm.resetFields();
        }}
        onOk={() => whiteForm.submit()}
      >
        <Form form={whiteForm} onFinish={handleAddToWhitelist} layout="vertical">
          <Form.Item
            name="ip"
            label="IP地址"
            rules={[{ required: true, message: '请输入IP地址' }]}
          >
            <Input placeholder="请输入IP地址" />
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
};

export default IpFilter;
