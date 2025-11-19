import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";
import { useAuthStore } from "../store/authStore";

type Callback<T = any> = (payload: T) => void;

class GameSocket {
  private client: Client | null = null;
  private connected = false;
  private pendingSubscriptions: (() => void)[] = [];

  connect() {
    if (this.client && this.connected) {
      return;
    }

    const token = useAuthStore.getState().token;
    const url = (import.meta.env.VITE_WS_URL as string) || "http://localhost:8080/ws";

    const socket = new SockJS(url);
    this.client = new Client({
      webSocketFactory: () => socket as any,
      connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
      debug: () => {},
      onConnect: () => {
        this.connected = true;
        this.pendingSubscriptions.forEach((fn) => fn());
        this.pendingSubscriptions = [];
      },
      onStompError: (frame) => {
        console.error("STOMP error", frame.headers["message"]);
      },
    });

    this.client.activate();
  }

  subscribe(path: string, cb: Callback) {
    const doSub = () => {
      this.client?.subscribe(path, (message: IMessage) => {
        try {
          const body = JSON.parse(message.body);
          cb(body);
        } catch {
          cb(message.body);
        }
      });
    };

    if (this.connected && this.client) {
      doSub();
    } else {
      this.pendingSubscriptions.push(doSub);
      this.connect();
    }
  }

  send(path: string, payload: any) {
    if (!this.client || !this.connected) {
      this.connect();
    }
    const body = JSON.stringify(payload ?? {});
    this.client?.publish({ destination: path, body });
  }
}

export const gameSocket = new GameSocket();