export type Suit = "♠" | "♥" | "♦" | "♣";

export interface CardDTO {
  id: string;          // identifiant unique (pour clic/DOM key)
  value: string;       // "A","K","Q","J","10","9","8","7" (ou 52)
  suit: Suit;
  power: boolean;      // carte à pouvoir ?
  playable: boolean;   // jouable à ce tour ?
  depth: number;       // profondeur dans l'arbre
  locked?: boolean;    // ex. racine verrouillée
}

export interface GameStateDTO {
  gameId: string;
  round: number;
  turnPlayer: string;
  rootLocked: boolean;
  maxDepthAllowed: number; // ex: n, n-1, etc. selon la règle du tour
  board: CardDTO[][];      // 4x8 pour mode 32 (ou layout selon mode 52)
  score: {
    player1: number;
    player2: number;
  };
  lastMove?: { by: string; cardId: string };
}

export interface PlayRequestDTO {
  cardId: string;      // id de la carte cliquée
}
