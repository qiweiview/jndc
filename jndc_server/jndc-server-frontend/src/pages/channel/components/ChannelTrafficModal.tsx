import React, { useEffect, useRef, useState } from 'react';
import { Descriptions, Modal, Segmented, Spin } from 'antd';
import * as echarts from 'echarts';
import dayjs from 'dayjs';
import { channelApi } from '../../../api/channel';
import { ChannelContext, ChannelTrafficTrendResult, TrafficTrendRange } from '../../../types';
import { formatBandwidth, formatDateTime, formatTrafficBytes } from '../utils';

interface ChannelTrafficModalProps {
  open: boolean;
  record: ChannelContext | null;
  onCancel: () => void;
}

const RANGE_OPTIONS: { label: string; value: TrafficTrendRange }[] = [
  { label: '24 Hour', value: '24hour' },
  { label: '7 Day', value: '7day' },
  { label: '1 Month', value: '1month' },
  { label: '1 Year', value: '1year' },
];

const EMPTY_TREND: ChannelTrafficTrendResult = {
  range: '24hour',
  bucketUnit: 'hour',
  points: [],
};

const formatXAxisLabel = (timestamp: number, bucketUnit: ChannelTrafficTrendResult['bucketUnit']) => {
  if (bucketUnit === 'month') {
    return dayjs(timestamp).format('YYYY-MM');
  }
  if (bucketUnit === 'day') {
    return dayjs(timestamp).format('MM-DD');
  }
  return dayjs(timestamp).format('MM-DD HH:00');
};

const ChannelTrafficModal: React.FC<ChannelTrafficModalProps> = ({ open, record, onCancel }) => {
  const chartRef = useRef<HTMLDivElement | null>(null);
  const chartInstanceRef = useRef<echarts.ECharts | null>(null);
  const [range, setRange] = useState<TrafficTrendRange>('24hour');
  const [trend, setTrend] = useState<ChannelTrafficTrendResult>(EMPTY_TREND);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!open) {
      setRange('24hour');
      setTrend(EMPTY_TREND);
    }
  }, [open]);

  useEffect(() => {
    if (!open || !record?.clientId) {
      return;
    }

    let alive = true;
    setLoading(true);
    channelApi.getChannelTrafficTrend(record.clientId, range)
      .then((data) => {
        if (alive) {
          setTrend(data ?? { ...EMPTY_TREND, range });
        }
      })
      .catch(() => {
        if (alive) {
          setTrend({ ...EMPTY_TREND, range });
        }
      })
      .finally(() => {
        if (alive) {
          setLoading(false);
        }
      });

    return () => {
      alive = false;
    };
  }, [open, range, record?.clientId, record?.trafficUpdatedAt]);

  useEffect(() => {
    if (!open || !chartRef.current) {
      return;
    }

    const chart = chartInstanceRef.current ?? echarts.init(chartRef.current);
    chartInstanceRef.current = chart;

    const xAxisData = trend.points.map((point) => formatXAxisLabel(point.timestamp, trend.bucketUnit));
    const clientToServerData = trend.points.map((point) => point.clientToServerBytes);
    const serverToClientData = trend.points.map((point) => point.serverToClientBytes);

    chart.setOption({
      animation: true,
      color: ['#0f766e', '#ea580c'],
      grid: {
        left: 48,
        right: 24,
        top: 48,
        bottom: 48,
      },
      legend: {
        top: 8,
        data: ['client -> server', 'server -> client'],
      },
      tooltip: {
        trigger: 'axis',
        valueFormatter: (value: number | string) => formatTrafficBytes(Number(value)),
        formatter: (params: unknown) => {
          const items = Array.isArray(params) ? params as Array<{ dataIndex: number; seriesName: string; value: number; color: string }> : [];
          const point = trend.points[items[0]?.dataIndex ?? 0];
          const lines = [
            formatDateTime(point?.timestamp),
            ...items.map((item) => `${item.seriesName}: ${formatTrafficBytes(item.value)}`),
            `total: ${formatTrafficBytes(point?.totalBytes ?? 0)}`,
          ];
          return lines
            .map((line, index) => (index === 0 ? line : `<span style="color:${items[index - 1]?.color ?? '#111827'}">${line}</span>`))
            .join('<br/>');
        },
      },
      xAxis: {
        type: 'category',
        data: xAxisData,
        boundaryGap: false,
        axisLabel: {
          color: '#4b5563',
          hideOverlap: true,
        },
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          color: '#4b5563',
          formatter: (value: number) => formatTrafficBytes(value),
        },
        splitLine: {
          lineStyle: {
            color: '#e5e7eb',
          },
        },
      },
      series: [
        {
          name: 'client -> server',
          type: 'line',
          smooth: true,
          showSymbol: false,
          areaStyle: {
            opacity: 0.12,
          },
          data: clientToServerData,
        },
        {
          name: 'server -> client',
          type: 'line',
          smooth: true,
          showSymbol: false,
          areaStyle: {
            opacity: 0.08,
          },
          data: serverToClientData,
        },
      ],
    });

    const handleResize = () => {
      chart.resize();
    };
    window.addEventListener('resize', handleResize);
    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [open, trend]);

  useEffect(() => () => {
    chartInstanceRef.current?.dispose();
    chartInstanceRef.current = null;
  }, []);

  return (
    <Modal
      title={record ? `流量统计 - ${record.clientId}` : '流量统计'}
      open={open}
      onCancel={onCancel}
      footer={null}
      width={880}
      destroyOnHidden={false}
    >
      {record && (
        <>
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

          <div style={{ marginTop: 20 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
              <strong style={{ fontSize: 14 }}>趋势图</strong>
              <Segmented<TrafficTrendRange>
                options={RANGE_OPTIONS}
                value={range}
                onChange={(value) => setRange(value)}
              />
            </div>
            <Spin spinning={loading}>
              <div
                ref={chartRef}
                style={{
                  height: 340,
                  width: '100%',
                  border: '1px solid #f1f5f9',
                  borderRadius: 12,
                  background: 'linear-gradient(180deg, #fcfcfd 0%, #f8fafc 100%)',
                }}
              />
            </Spin>
          </div>
        </>
      )}
    </Modal>
  );
};

export default ChannelTrafficModal;
