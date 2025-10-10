package com.example.lab3;

import java.util.*;

public class ScoreTable {
    private final Map<String, Integer> scores = new LinkedHashMap<>();

    public ScoreTable() {
        String[] categories = {
                "ONES", "TWOS", "THREES", "FOURS", "FIVES", "SIXES",
                //-------------------------------------------------
                "THREE_OF_A_KIND", "FOUR_OF_A_KIND", "FULL_HOUSE",
                "SMALL_STRAIGHT", "LARGE_STRAIGHT", "CHANCE", "YATZY"
        };
        for (String c : categories) {
            scores.put(c, 0);
        }
    }

    public void setScore(String category, int value) {
        scores.put(category, value);
    }

    public int getTotal() {
        return scores.values().stream().mapToInt(Integer::intValue).sum();
    }

    public void printScores(String playerName) {
        System.out.println("Score TAble (" + playerName + "):");
        scores.forEach((k, v) -> System.out.println("  " + k + ": " + v));
        System.out.println("Total: " + getTotal());
    }

    public Map<String, Integer> getAll() {
        return scores;
    }

}
