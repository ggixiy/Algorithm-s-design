package com.example.lab3;

import java.util.Random;

public class Randomizer {
    private Random r = new Random();

    public int[] ThrowDices(int n, Random r){
        int[] dices = new int[n];
        for(int i = 0; i < n; i++){
            dices[i] = r.nextInt(1, 7);
        }
        return dices;
    }
}
