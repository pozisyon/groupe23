package com.arbre32.api.dto;
import java.util.List;
public record GameStateDTO(String status, List<List<String>> board, String currentPlayer, int p1Score, int p2Score) {}
