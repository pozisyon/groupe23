package com.arbre32.api.game.dto;

public class PlayerDTO {
    public String handle;
    public int score;
    public String id;

    public PlayerDTO(String id, String name, int score) {
        this.id=id;this.score=score; this.handle=name;
    }
}
