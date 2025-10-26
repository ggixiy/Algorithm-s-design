package com.example.lab3;

import java.util.ArrayList;
import java.util.List;

public class State {
    public int[] dice;
    public boolean[] held;
    public int rollsLeft;
    public List<String> availableCategories;
    public int botScore;
    public int humanScore;

    public State(int[] dice, boolean[] held, int rollsLeft,
                        List<String> availableCategories, int botScore, int humanScore) {
        this.dice = dice;
        this.held = held;
        this.rollsLeft = rollsLeft;
        this.availableCategories = new ArrayList<>(availableCategories);
        this.botScore = botScore;
        this.humanScore = humanScore;
    }
}
