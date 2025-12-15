package com.arbre32.core.model;

public enum Rank {
    // Cartes à pouvoir (Valet, Dame, Roi, As)
    ACE("A", 11, true),
    KING("K", 10, true),
    QUEEN("Q", 9, true),
    JACK("J", 8, true),

    // Cartes normales 10/9/8/7 → 7/6/5/4 points
    TEN("10", 7, false),
    NINE("9", 6, false),
    EIGHT("8", 5, false),
    SEVEN("7", 4, false),

    // Le reste (pour le mode 52 cartes, moins important pour Arbre32)
    SIX("6", 3, false),
    FIVE("5", 2, false),
    FOUR("4", 1, false),
    THREE("3", 0, false),
    TWO("2", 0, false);

    private final String label;
    private final int baseScore;
    private final boolean power;

    Rank(String l, int s, boolean p) {
        label = l;
        baseScore = s;
        power = p;
    }

    public String label() { return label; }

    /** Points "bruts" selon les règles du jeu. */
    public int baseScore() { return baseScore; }

    /** Carte à pouvoir (J, Q, K, A) ? */
    public boolean isPower() { return power; }
}
