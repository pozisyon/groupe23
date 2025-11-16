// main.tsx
import "./polyfills/global";
import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { App } from './ui/App';
import "./index.css";
import { GameProvider } from "./context/GameContext";
import { ChatProvider } from "./context/ChatContext";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <BrowserRouter>
      <GameProvider>
        <ChatProvider>
          <App />
        </ChatProvider>
      </GameProvider>
    </BrowserRouter>
  </React.StrictMode>
);
