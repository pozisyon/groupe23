import { CardDTO } from "../dto/game";

export default function Card({ card, onClick }: { card: CardDTO; onClick?: (id: string) => void }) {
  const disabled = !card.playable || card.locked;
  return (
    <button
      className={`w-full h-full border rounded-xl p-2 text-left ${disabled ? "opacity-40 cursor-not-allowed" : "hover:border-blue-500"}`}
      onClick={() => !disabled && onClick?.(card.id)}
      disabled={disabled}
      title={disabled ? "Non jouable" : "Cliquer pour jouer"}
    >
      <div className="text-sm">
        <span className={card.power ? "text-blue-600 font-semibold" : ""}>{card.value}</span>&nbsp;
        <span>{card.suit}</span>
      </div>
      <div className="text-xs text-gray-500">profondeur: {card.depth}</div>
    </button>
  );
}
