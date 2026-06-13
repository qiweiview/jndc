import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';
import { Alert, Button, Card, Empty, Select, Space, Tag, Typography } from 'antd';
import { ClearOutlined, CloseCircleOutlined, ReloadOutlined } from '@ant-design/icons';
import { motion } from 'framer-motion';
import { useSearchParams } from 'react-router-dom';
import { FitAddon } from '@xterm/addon-fit';
import { Terminal } from 'xterm';
import 'xterm/css/xterm.css';
import { channelApi } from '../../api/channel';
import { ChannelContext, TerminalSocketRequest, TerminalSocketResponse } from '../../types';
import { staggerContainerVariants, staggerItemVariants } from '../../utils/motion';
import './index.css';

const FULL_AUTHORIZED = 1;

type TerminalStatus =
  | 'idle'
  | 'connecting'
  | 'connected'
  | 'closed'
  | 'offline'
  | 'forbidden'
  | 'occupied'
  | 'error';

const statusMeta: Record<TerminalStatus, { color: string; label: string }> = {
  idle: { color: 'default', label: '待连接' },
  connecting: { color: 'processing', label: '连接中' },
  connected: { color: 'success', label: '已连接' },
  closed: { color: 'default', label: '会话结束' },
  offline: { color: 'error', label: '设备离线' },
  forbidden: { color: 'warning', label: '权限不足' },
  occupied: { color: 'warning', label: '会话占用' },
  error: { color: 'error', label: '连接异常' },
};

const createSessionId = () => {
  if (typeof crypto !== 'undefined' && typeof crypto.randomUUID === 'function') {
    return crypto.randomUUID();
  }
  return `terminal-${Date.now()}-${Math.random().toString(16).slice(2)}`;
};

const TerminalPage: React.FC = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [channels, setChannels] = useState<ChannelContext[]>([]);
  const [loading, setLoading] = useState(false);
  const [status, setStatus] = useState<TerminalStatus>('idle');
  const [statusMessage, setStatusMessage] = useState('请选择一个在线且处于 FULL_AUTHORIZED 的客户端');
  const [shellType, setShellType] = useState('');
  const selectedClientId = searchParams.get('clientId') ?? '';

  const terminalRootRef = useRef<HTMLDivElement | null>(null);
  const terminalRef = useRef<Terminal | null>(null);
  const fitAddonRef = useRef<FitAddon | null>(null);
  const resizeObserverRef = useRef<ResizeObserver | null>(null);
  const socketRef = useRef<WebSocket | null>(null);
  const sessionIdRef = useRef<string>('');
  const currentClientIdRef = useRef<string>('');

  const eligibleChannels = useMemo(
    () => channels.filter((item) => item.online && item.authMode === FULL_AUTHORIZED),
    [channels]
  );

  const selectedChannel = useMemo(
    () => channels.find((item) => item.clientId === selectedClientId) ?? null,
    [channels, selectedClientId]
  );
  const selectedChannelOnline = selectedChannel?.online ?? false;
  const selectedChannelAuthMode = selectedChannel?.authMode ?? 0;

  const channelOptions = useMemo(
    () =>
      eligibleChannels.map((item) => ({
        value: item.clientId,
        label: `${item.clientId} (${item.clientIp || '--'}:${item.clientPort || 0})`,
      })),
    [eligibleChannels]
  );

  const resetTerminalView = useCallback(() => {
    terminalRef.current?.clear();
    terminalRef.current?.write('\x1b[2J\x1b[H');
  }, []);

  const safeFitAndFocus = useCallback(() => {
    const terminal = terminalRef.current;
    const fitAddon = fitAddonRef.current;
    const root = terminalRootRef.current;
    if (!terminal || !fitAddon || !root || !root.isConnected) {
      return;
    }

    window.requestAnimationFrame(() => {
      const latestTerminal = terminalRef.current;
      const latestFitAddon = fitAddonRef.current;
      const latestRoot = terminalRootRef.current;
      if (!latestTerminal || !latestFitAddon || !latestRoot || !latestRoot.isConnected) {
        return;
      }

      try {
        latestFitAddon.fit();
        latestTerminal.focus();
      } catch (error) {
        console.warn('xterm fit skipped:', error);
      }
    });
  }, []);

  const closeSocket = useCallback((notifyServer: boolean) => {
    const socket = socketRef.current;
    if (!socket) {
      return;
    }

    if (notifyServer && socket.readyState === WebSocket.OPEN && sessionIdRef.current) {
      const payload: TerminalSocketRequest = {
        event: 'terminal.close',
        sessionId: sessionIdRef.current,
      };
      socket.send(JSON.stringify(payload));
    }

    socketRef.current = null;
    socket.close();
  }, []);

  const sendResize = useCallback(() => {
    const socket = socketRef.current;
    const terminal = terminalRef.current;
    const fitAddon = fitAddonRef.current;
    const root = terminalRootRef.current;
    if (!socket || socket.readyState !== WebSocket.OPEN || !terminal || !fitAddon || !sessionIdRef.current || !root) {
      return;
    }

    window.requestAnimationFrame(() => {
      const latestSocket = socketRef.current;
      const latestTerminal = terminalRef.current;
      const latestFitAddon = fitAddonRef.current;
      const latestRoot = terminalRootRef.current;
      if (
        !latestSocket ||
        latestSocket.readyState !== WebSocket.OPEN ||
        !latestTerminal ||
        !latestFitAddon ||
        !latestRoot ||
        !sessionIdRef.current
      ) {
        return;
      }

      try {
        latestFitAddon.fit();
      } catch (error) {
        console.warn('xterm resize fit skipped:', error);
        return;
      }

      const payload: TerminalSocketRequest = {
        event: 'terminal.resize',
        sessionId: sessionIdRef.current,
        cols: latestTerminal.cols,
        rows: latestTerminal.rows,
      };
      latestSocket.send(JSON.stringify(payload));
    });
  }, []);

  const fetchChannels = useCallback(async () => {
    setLoading(true);
    try {
      const data = await channelApi.getServerChannelTable();
      setChannels(data);
      if (!selectedClientId) {
        const preferred = data.find((item) => item.online && item.authMode === FULL_AUTHORIZED);
        if (preferred) {
          setSearchParams({ clientId: preferred.clientId });
        }
      }
    } finally {
      setLoading(false);
    }
  }, [selectedClientId, setSearchParams]);

  useEffect(() => {
    fetchChannels();
    const timer = window.setInterval(fetchChannels, 3000);
    return () => window.clearInterval(timer);
  }, [fetchChannels]);

  useEffect(() => {
    const terminal = new Terminal({
      cursorBlink: true,
      fontFamily: '"SFMono-Regular", "JetBrains Mono", "Fira Code", monospace',
      fontSize: 14,
      lineHeight: 1.3,
      theme: {
        background: '#0f1319',
        foreground: '#e6edf3',
        cursor: '#6cb6ff',
        selectionBackground: 'rgba(108, 182, 255, 0.28)',
      },
      scrollback: 2000,
    });
    const fitAddon = new FitAddon();
    terminal.loadAddon(fitAddon);
    terminal.open(terminalRootRef.current!);
    terminal.writeln('JNDC Remote Terminal');
    terminal.writeln('');
    terminalRef.current = terminal;
    fitAddonRef.current = fitAddon;
    safeFitAndFocus();

    const disposable = terminal.onData((data) => {
      const socket = socketRef.current;
      if (!socket || socket.readyState !== WebSocket.OPEN || !sessionIdRef.current) {
        return;
      }
      const payload: TerminalSocketRequest = {
        event: 'terminal.input',
        sessionId: sessionIdRef.current,
        data,
      };
      socket.send(JSON.stringify(payload));
    });

    const resizeObserver = new ResizeObserver(() => {
      sendResize();
    });
    resizeObserver.observe(terminalRootRef.current!);
    resizeObserverRef.current = resizeObserver;

    return () => {
      disposable.dispose();
      resizeObserver.disconnect();
      resizeObserverRef.current = null;
      terminal.dispose();
      terminalRef.current = null;
      fitAddonRef.current = null;
    };
  }, [safeFitAndFocus, sendResize]);

  const connectTerminal = useCallback(() => {
    if (!selectedClientId) {
      setStatus('idle');
      setStatusMessage('请选择一个在线且处于 FULL_AUTHORIZED 的客户端');
      return;
    }

    if (!selectedChannelOnline) {
      setStatus('offline');
      setStatusMessage('当前客户端已离线，无法建立终端会话');
      return;
    }

    if (selectedChannelAuthMode !== FULL_AUTHORIZED) {
      setStatus('forbidden');
      setStatusMessage('仅 FULL_AUTHORIZED 客户端允许打开远程终端');
      return;
    }

    closeSocket(false);
    terminalRef.current?.clear();
    terminalRef.current?.write('\x1b[2J\x1b[H');
    terminalRef.current?.writeln(`connecting to ${selectedClientId} ...`);

    const token = localStorage.getItem('auth-token');
    if (!token || token === '403') {
      setStatus('error');
      setStatusMessage('登录凭证缺失，请重新登录');
      return;
    }

    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
    const socket = new WebSocket(
      `${protocol}//${window.location.host}/ws?mode=terminal&auth-token=${encodeURIComponent(token)}`
    );
    socketRef.current = socket;
    sessionIdRef.current = createSessionId();
    currentClientIdRef.current = selectedClientId;
    setShellType('');
    setStatus('connecting');
    setStatusMessage(`正在连接 ${selectedClientId}`);

    socket.onopen = () => {
      const terminal = terminalRef.current;
      safeFitAndFocus();
      const payload: TerminalSocketRequest = {
        event: 'terminal.open',
        sessionId: sessionIdRef.current,
        clientId: selectedClientId,
        cols: terminal?.cols,
        rows: terminal?.rows,
      };
      socket.send(JSON.stringify(payload));
    };

    socket.onmessage = (event) => {
      const message: TerminalSocketResponse = JSON.parse(event.data);
      if (message.sessionId !== sessionIdRef.current) {
        return;
      }

      if (message.event === 'terminal.ready') {
        setStatus('connected');
        setStatusMessage(`已连接到 ${selectedClientId}`);
        setShellType(message.shellType || '');
        terminalRef.current?.writeln(`${message.shellType || 'shell'} ready`);
        terminalRef.current?.write('\r\n');
        safeFitAndFocus();
        sendResize();
        return;
      }

      if (message.event === 'terminal.output') {
        terminalRef.current?.write(message.data || '');
        return;
      }

      if (message.event === 'terminal.exit') {
        setStatus('closed');
        setStatusMessage(`终端会话已结束，exit code ${message.exitCode ?? 0}`);
        terminalRef.current?.write(`\r\n[session closed: ${message.exitCode ?? 0}]\r\n`);
        sessionIdRef.current = '';
        closeSocket(false);
        return;
      }

      if (message.event === 'terminal.error') {
        const errorMessage = message.message || '终端会话异常';
        const nextStatus = errorMessage.indexOf('已有活动终端会话') !== -1 ? 'occupied' : 'error';
        setStatus(nextStatus);
        setStatusMessage(errorMessage);
        terminalRef.current?.write(`\r\n[error] ${errorMessage}\r\n`);
        sessionIdRef.current = '';
        closeSocket(false);
      }
    };

    socket.onclose = () => {
      if (socketRef.current === socket) {
        socketRef.current = null;
      }
      if (sessionIdRef.current) {
        setStatus((prev) => (prev === 'connected' || prev === 'connecting' ? 'closed' : prev));
        setStatusMessage((prev) => (prev === `已连接到 ${selectedClientId}` ? '终端连接已关闭' : prev));
        sessionIdRef.current = '';
      }
    };

    socket.onerror = () => {
      setStatus('error');
      setStatusMessage('terminal websocket 连接失败');
    };
  }, [closeSocket, selectedChannelAuthMode, selectedChannelOnline, selectedClientId, sendResize]);

  useEffect(() => {
    connectTerminal();
    return () => {
      closeSocket(true);
      sessionIdRef.current = '';
    };
  }, [connectTerminal, closeSocket]);

  useEffect(() => {
    return () => {
      closeSocket(true);
      sessionIdRef.current = '';
    };
  }, [closeSocket]);

  const currentStatus = statusMeta[status];

  return (
    <motion.div variants={staggerContainerVariants} initial="initial" animate="animate">
      <motion.div variants={staggerItemVariants}>
        <Card
          className="terminal-page-card"
          bordered={false}
          style={{ borderRadius: 12 }}
          title={<span style={{ fontSize: 16, fontWeight: 600 }}>远程终端</span>}
          extra={
            <div className="terminal-toolbar">
              <Select
                placeholder="选择客户端"
                value={selectedClientId || undefined}
                options={channelOptions}
                onChange={(value) => setSearchParams({ clientId: value })}
                style={{ width: 360 }}
                loading={loading}
              />
              <Space>
                <Button
                  icon={<ReloadOutlined />}
                  onClick={connectTerminal}
                >
                  重连
                </Button>
                <Button icon={<ClearOutlined />} onClick={resetTerminalView}>
                  清屏
                </Button>
                <Button danger icon={<CloseCircleOutlined />} onClick={() => closeSocket(true)}>
                  关闭会话
                </Button>
              </Space>
            </div>
          }
        >
          {!selectedClientId && !eligibleChannels.length ? (
            <Empty description="当前没有在线且处于 FULL_AUTHORIZED 的客户端" />
          ) : (
            <Space direction="vertical" size={16} style={{ width: '100%' }}>
              <div className="terminal-status-row">
                <Tag color={currentStatus.color} bordered={false}>
                  {currentStatus.label}
                </Tag>
                {selectedChannel && (
                  <Tag color="blue" bordered={false}>
                    {selectedChannel.clientId}
                  </Tag>
                )}
                {shellType && (
                  <Tag color="geekblue" bordered={false}>
                    {shellType}
                  </Tag>
                )}
              </div>
              <Alert
                type={status === 'connected' ? 'success' : status === 'error' || status === 'offline' ? 'error' : 'info'}
                showIcon
                message={statusMessage}
              />
              <div className="terminal-surface">
                <div
                  ref={terminalRootRef}
                  className="terminal-shell"
                  onClick={() => terminalRef.current?.focus()}
                />
              </div>
              <Typography.Text type="secondary">
                v1 使用流式 shell 会话，不保证 `vim`、`top`、`less` 等全屏 TUI 正常工作。
              </Typography.Text>
            </Space>
          )}
        </Card>
      </motion.div>
    </motion.div>
  );
};

export default TerminalPage;
