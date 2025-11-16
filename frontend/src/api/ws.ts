import { Client, IMessage } from "@stomp/stompjs";
import SockJS from "sockjs-client";

const WS_URL = (import.meta.env.VITE_WS_URL || "http://localhost:8080") + "/ws-chat";

export function createStompClient(onConnect?: () => void) {
  const client = new Client({
    webSocketFactory: () => new SockJS(WS_URL),
    reconnectDelay: 3000,
    debug: () => {}, // console.log
    onConnect,
  });
  return client;
}

export type SubscriptionHandle = { unsubscribe: () => void };

export function subscribeChat(client: Client, roomId: string, onMessage: (m: IMessage) => void): SubscriptionHandle {
  const sub = client.subscribe(`/topic/chat/${roomId}`, onMessage);
  return { unsubscribe: () => sub.unsubscribe() };
}

export function sendChat(client: Client, roomId: string, payload: { sender: string; content: string }) {
  client.publish({
    destination: `/app/chat/${roomId}`,
    body: JSON.stringify(payload),
  });
}

export function subscribeGameUpdates(client: Client, gameId: string, onMessage: (m: IMessage) => void): SubscriptionHandle {
  const sub = client.subscribe(`/topic/game/${gameId}`, onMessage);
  return { unsubscribe: () => sub.unsubscribe() };
}
