package com.example.lab3;

import java.util.*;

public class DiceManager {
    private final int[] dice;
    private final boolean[] held;
    private final Random random = new Random();

    public DiceManager(int diceCount) {
        dice = new int[diceCount];
        held = new boolean[diceCount];

        reset();
    }

    public void roll() {
        for (int i = 0; i < dice.length; i++) {
            if (!held[i]) dice[i] = random.nextInt(6) + 1;
        }
    }

    public void hold(int index) {
        if (index >= 0 && index < held.length) held[index] = true;
    }

    public void reset() {
        Arrays.fill(held, false);
        Arrays.fill(dice, 0);
    }

    public int[] getValues() { return dice; }

    public void releaseAll() {
        Arrays.fill(held, false);
    }
}
