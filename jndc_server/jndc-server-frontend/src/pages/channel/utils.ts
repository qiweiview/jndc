import dayjs from 'dayjs';
import { ChannelContext } from '../../types';

export const formatDateTime = (value?: number) => {
  if (!value || value <= 0) {
    return '--';
  }
  return dayjs(value).format('YYYY-MM-DD HH:mm:ss');
};

export const formatBytes = (value?: number, zeroText = '--') => {
  if (value === undefined || value === null || value < 0) {
    return '--';
  }
  if (value === 0) {
    return zeroText;
  }
  const units = ['B', 'KB', 'MB', 'GB', 'TB'];
  let size = value;
  let unitIndex = 0;
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex += 1;
  }
  return `${size.toFixed(size >= 10 || unitIndex === 0 ? 0 : 1)} ${units[unitIndex]}`;
};

export const formatTrafficBytes = (value?: number) => formatBytes(value, '0 B');

export const formatBandwidth = (value?: number) => `${formatTrafficBytes(value)}/s`;

export const formatTrafficSummary = (bandwidth?: number, totalBytes?: number) => (
  `${formatBandwidth(bandwidth)} / ${formatTrafficBytes(totalBytes)}`
);

export const formatOs = (record: ChannelContext) => {
  const parts = [record.osName, record.osVersion].filter(Boolean);
  return parts.length > 0 ? parts.join(' ') : '--';
};

export const formatCpu = (record: ChannelContext) => {
  if (!record.cpuModel) {
    return '--';
  }
  return record.cpuLogicalCores > 0
    ? `${record.cpuModel} / ${record.cpuLogicalCores} 线程`
    : record.cpuModel;
};
