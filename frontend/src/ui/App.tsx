import React, { useEffect, useState } from 'react'
type GameStateDTO = {
  status: string
  board: string[][]
  currentPlayer: string
  p1Score: number
  p2Score: number
}
export const App: React.FC = () => {
  const [state, setState] = useState<GameStateDTO | null>(null)
  const start = async () => {
    await fetch('/api/game/start', { method: 'POST' })
    await refresh()
  }
  const refresh = async () => {
    const s = await fetch('/api/game/state').then(r => r.json())
    setState(s)
  }
  useEffect(() => { refresh() }, [])
  return (
    <div className="wrap">
      <h1>Arbre32 – Prototype temps réel</h1>
      <div className="scores">
        <button onClick={start}>Démarrer une partie</button>
        <div>État: <b>{state?.status ?? 'NO_GAME'}</b></div>
        <div>Tour: <b>{state?.currentPlayer ?? '-'}</b></div>
        <div>Scores: <b>Alice {state?.p1Score ?? 0}</b> / <b>Bob {state?.p2Score ?? 0}</b></div>
      </div>
      <hr/>
      <h3>Plateau 4×8</h3>
      <div className="board">
        {state?.board?.flatMap((row, r) => row.map((cell, c) => (
          <div key={r+'-'+c} className="card">{cell}</div>
        )))}
      </div>
      <p style={{marginTop:16, color:'#555'}}>Remarque : les cartes affichées sont un exemple généré côté serveur. Le moteur d'arbre sera branché étape par étape.</p>
    </div>
  )
}
