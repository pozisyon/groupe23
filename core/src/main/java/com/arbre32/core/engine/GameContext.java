package com.arbre32.core.engine;
public class GameContext {
    private static GameContext INSTANCE;
    private GameState state;
    private GameContext(){}
    public static synchronized GameContext getInstance(){
        if(INSTANCE==null) INSTANCE = new GameContext();
        return INSTANCE;
    }
    public GameState getState(){ return state; }
    public void setState(GameState s){ this.state = s; }
}
