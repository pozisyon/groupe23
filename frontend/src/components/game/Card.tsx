// src/components/game/Card.tsx
/*import type { CardDTO } from "../../api/gameApi";
interface CardProps {
  card: {
    id: string;
    value: string;
    suit: string;
    playable: boolean;
    locked: boolean;
    depth: number;
    power?: boolean;
  };
  onClick: (id: string) => void;
}

export default function Card({ card, onClick }) {
  const disabled = card.locked || !card.playable;

  return (
    <button
      disabled={disabled}
      onClick={() => onClick(card.id)}
      className={`w-full h-full rounded-md border flex flex-col items-center justify-center
        ${disabled ? "bg-gray-300 opacity-60 cursor-not-allowed" : "bg-white hover:bg-green-100 cursor-pointer"}
      `}
    >
      <div className="text-lg font-bold">{card.value}</div>
      <div className="text-sm">{card.suit}</div>
      {card.power && <div className="text-xs text-red-500">POWER</div>}
    </button>
  );
}
*/
// src/components/game/Card.tsx
import { motion } from "framer-motion";

interface CardProps {
  card: {
    id: string;
    value: string;
    suit: string;
    playable: boolean;
    locked: boolean;
    depth: number;
    power?: boolean;
  };
  onClick: (id: string) => void;
}

export default function Card({ card, onClick }: CardProps) {

  const disabled = card.locked || !card.playable;

  // test: si ton backend envoie "HEART", on convertit en emoji
  const suitEmoji = {
    HEART: "♥️",
    DIAMOND: "♦️",
    CLUB: "♣️",
    SPADE: "♠️",

    // si backend ENVOIE déjà l’emoji → on le garde
    "♥": "♥️",
    "♦": "♦️",
    "♣": "♣️",
    "♠": "♠️",
  }[card.suit] || card.suit; // fallback = montrer ce que tu reçois

  // couleur automatique
  const isRed = suitEmoji.includes("♥") || suitEmoji.includes("♦");
  const colorClass = isRed ? "text-red-600" : "text-black";

  return (
    <motion.button
      disabled={disabled}
      onClick={() => onClick(card.id)}
      whileHover={!disabled ? { scale: 1.08 } : {}}
      whileTap={!disabled ? { scale: 0.95 } : {}}
      className={`
        relative w-20 h-28 sm:w-24 sm:h-32
        rounded-lg border shadow-xl flex flex-col items-center justify-center
        font-bold bg-white select-none
        ${disabled ? "opacity-60 cursor-not-allowed" : "cursor-pointer hover:shadow-2xl"}
        ${card.playable && !disabled ? "ring-4 ring-green-400" : ""}
      `}
    >
      {/* Symbole */}
      <div className={`text-3xl ${colorClass}`}>
        {suitEmoji}
      </div>

      {/* Valeur */}
      <div className="text-xl mt-1 text-black">
        {card.value}
      </div>

      {/* POWER badge */}
      {card.power && (
        <div className="absolute bottom-1 right-1 bg-red-600 text-white text-[10px] px-1 py-[1px] rounded">
          POWER
        </div>
      )}
    </motion.button>
  );
}
