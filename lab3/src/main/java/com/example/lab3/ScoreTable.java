package com.example.lab3;

import java.util.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ScoreTable {
    private final Map<String, IntegerProperty> scores = new LinkedHashMap<>();
    private final Map<String, Boolean> usedCategories = new LinkedHashMap<>();
    private String[] categories = {
            "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes", "Sum", "Bonus",
            "Three of a kind", "Four of a kind", "Full house",
            "Small straight", "Large straight", "Yatzy", "Yatzy bonus", "Chance"
    };

    public ScoreTable() {
        for (String c : categories) {
            scores.put(c, new SimpleIntegerProperty(-1));
            usedCategories.put(c, false);
        }
    }

    public void setScore(String category, int value) {
        scores.get(category).set(value);
        usedCategories.put(category, true);
    }

    public IntegerProperty getScoreProperty(String category) {
        IntegerProperty prop = scores.get(category);
        if (prop == null) {
            System.err.println("Unknown category requested: " + category);
            return new SimpleIntegerProperty(-1);
        }
        return prop;
    }

    public int getTotal() {
        int sum = 0;
        for (String cat : getAll().keySet()) {
            if (!cat.equals("Sum"))
            {
                IntegerProperty value = scores.get(cat);
                if (value != null && value.get() != -1) sum += value.get();
            }
        }
        return sum;
    }

    public Map<String, IntegerProperty> getAll() {
        return scores;
    }

    public boolean isUsed(String category) {
        return usedCategories.getOrDefault(category, false);
    }

    public boolean isFull() {
        for (String cat : scores.keySet()) {
            if (cat.equals("Sum") || cat.equals("Bonus") || cat.equals("Yatzy bonus")) continue;
            if (!isUsed(cat)) return false;
        }
        return true;
    }

    public void reset() {
        scores.forEach((k, v) -> v.set(-1));
        usedCategories.replaceAll((k, v) -> false);
    }
}
