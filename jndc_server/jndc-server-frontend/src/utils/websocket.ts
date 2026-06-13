import { WsMessage } from '../types';

type WsCallback = (data: string) => void;

class WebSocketClient {
  private ws: WebSocket | null = null;
  private reconnectTimer: number | null = null;
  private listeners: Map<string, Set<WsCallback>> = new Map();
  private globalListeners: Set<WsCallback> = new Set();
  private lastUrl: string | null = null;
  private manualClose = false;

  connect(url: string) {
    if (this.ws && (this.ws.readyState === WebSocket.OPEN || this.ws.readyState === WebSocket.CONNECTING)) {
      return;
    }

    this.lastUrl = url;
    this.manualClose = false;
    this.ws = new WebSocket(url);

    this.ws.onopen = () => {
      console.log('WebSocket connected');
      if (this.reconnectTimer) {
        clearTimeout(this.reconnectTimer);
        this.reconnectTimer = null;
      }
    };

    this.ws.onmessage = (event) => {
      try {
        const message: WsMessage = JSON.parse(event.data);
        if (message.type === 1) {
          // 页面刷新事件
          const pageListeners = this.listeners.get(message.data);
          pageListeners?.forEach((cb) => cb(message.data));
        } else {
          // 全局通知
          this.globalListeners.forEach((cb) => cb(message.data));
        }
      } catch (e) {
        console.error('WebSocket message parse error:', e);
      }
    };

    this.ws.onclose = () => {
      console.log('WebSocket disconnected');
      this.ws = null;
      if (!this.manualClose) {
        this.reconnect();
      }
    };

    this.ws.onerror = (error) => {
      console.error('WebSocket error:', error);
    };
  }

  private reconnect() {
    if (this.reconnectTimer) {
      return;
    }
    this.reconnectTimer = window.setTimeout(() => {
      this.reconnectTimer = null;
      if (this.lastUrl) {
        this.connect(this.lastUrl);
      }
    }, 3000);
  }

  // 订阅页面刷新事件
  onPageRefresh(page: string, callback: WsCallback) {
    if (!this.listeners.has(page)) {
      this.listeners.set(page, new Set());
    }
    this.listeners.get(page)!.add(callback);

    return () => {
      this.listeners.get(page)?.delete(callback);
    };
  }

  // 订阅全局通知
  onNotification(callback: WsCallback) {
    this.globalListeners.add(callback);

    return () => {
      this.globalListeners.delete(callback);
    };
  }

  disconnect() {
    this.manualClose = true;
    if (this.reconnectTimer) {
      clearTimeout(this.reconnectTimer);
      this.reconnectTimer = null;
    }
    this.ws?.close();
    this.ws = null;
  }
}

export const wsClient = new WebSocketClient();
