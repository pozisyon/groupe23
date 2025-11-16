import React, { createContext, useContext, useEffect, useState } from "react";
import { Client, IMessage } from "@stomp/stompjs";
import { ChatMessageDTO } from "../dto/chat";
import { createStompClient, sendChat, subscribeChat } from "../api/ws";

type ChatCtx = {
  messages: ChatMessageDTO[];
  connect: (roomId: string) => void;
  send: (roomId: string, m: ChatMessageDTO) => void;
};

const Ctx = createContext<ChatCtx | null>(null);
export const useChat = () => {
  const ctx = useContext(Ctx);
  if (!ctx) throw new Error("useChat must be used within ChatProvider");
  return ctx;
};

export const ChatProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [client, setClient] = useState<Client | null>(null);
  const [room, setRoom] = useState<string>();
  const [messages, setMessages] = useState<ChatMessageDTO[]>([]);

  useEffect(() => () => { if (client?.active) client.deactivate(); }, [client]);

  const connect = (roomId: string) => {
    const c = createStompClient(() => {
      const h = subscribeChat(c, roomId, (msg: IMessage) => {
        try { setMessages(prev => [...prev, JSON.parse(msg.body)]); } catch {}
      });
      // on garde h si besoin de unsubscribe, ici simple
    });
    c.activate();
    setClient(c);
    setRoom(roomId);
  };

  const send = (roomId: string, m: ChatMessageDTO) => {
    if (!client) return;
    sendChat(client, roomId, m);
  };

  return (
    <Ctx.Provider value={{ messages, connect, send }}>
      {children}
    </Ctx.Provider>
  );
};
