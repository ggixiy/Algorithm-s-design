package com.example.lab3;

import java.util.*;

public class Karen extends Player {

    private final Random random = new Random();
    private String lastCategory;
    private int lastScore;
    private GameEngine engine;
    private static final Map<String, Double> targetScores = new HashMap<>();
    static {
        targetScores.put("Ones", 3.0);
        targetScores.put("Twos", 6.0);
        targetScores.put("Threes", 9.0);
        targetScores.put("Fours", 12.0);
        targetScores.put("Fives", 15.0);
        targetScores.put("Sixes", 18.0);

        targetScores.put("Three of a kind", 22.0);
        targetScores.put("Four of a kind", 18.0);
        targetScores.put("Full house", 20.0);
        targetScores.put("Small straight", 25.0);
        targetScores.put("Large straight", 35.0);
        targetScores.put("Yatzy", 25.0);
        targetScores.put("Chance", 30.0);
    }

    public Karen(String name) {
        super(name);
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    public String chooseCategory(UI ui) {
        List<String> availableCategories = new ArrayList<>();
        for (var e : table.getAll().entrySet()) {
            String cat = e.getKey();
            if (!table.isUsed(cat) && !cat.equals("Sum") && !cat.equals("Bonus") && !cat.equals("Yatzy bonus")) {
                availableCategories.add(cat);
            }
        }

        /*int[] diceValues = engine.getDices();
        String bestCategory = null;
        int bestScore = -1;

        for (String cat : availableCategories) {
            int score = engine.calculateCategoryScore(cat, diceValues);
            if (score > bestScore) {
                bestScore = score;
                bestCategory = cat;
            }
        }*/

        int[] diceValues = engine.getDices();
        String bestCategory = null;
        double bestHeuristicValue = Double.NEGATIVE_INFINITY;

        for (String cat : availableCategories) {
            int score = engine.calculateCategoryScore(cat, diceValues);

            double target = targetScores.getOrDefault(cat, 0.0);
            double heuristicValue = score - target;

            if (heuristicValue >= bestHeuristicValue) {
                bestHeuristicValue = heuristicValue;
                bestCategory = cat;
            }
        }

        if (bestCategory == null)
            bestCategory = availableCategories.get(new Random().nextInt(availableCategories.size()));

        return bestCategory;
    }

    public void makeHoldDecisions(DiceManager dice) {
        int[] currentDice = dice.getValues();
        boolean[] bestHold = new boolean[currentDice.length];
        double bestValue = Double.NEGATIVE_INFINITY;

        List<String> availableCategories = new ArrayList<>();
        for (var e : table.getAll().entrySet()) {
            String cat = e.getKey();
            if (!table.isUsed(cat) && !cat.equals("Sum") && !cat.equals("Bonus") && !cat.equals("Yatzy bonus")) {
                availableCategories.add(cat);
            }
        }

        for (boolean[] holdOption : allHoldCombinations(currentDice.length)) {
            State state = new State(
                    currentDice,
                    holdOption,
                    3 - engine.getRollCount(),
                    availableCategories,
                    table.getTotal(),
                    engine.getHumanPlayer().getTable().getTotal()
            );

            double value = expectimax(state, false);
            if (value > bestValue) {
                bestValue = value;
                bestHold = holdOption;
            }
        }

        for (int i = 0; i < bestHold.length; i++) {
            if (bestHold[i]) dice.hold(i);
            else dice.release(i);
        }
    }

    private double expectimax(State state, boolean isMax) {
        if (state.rollsLeft == 0) return evaluateState(state);

        if(isMax){
            return maxValue(state);
        }else{
            return expValue(state);
        }
    }

    private double evaluateState(State state) {
        //if(state.availableCategories.size() >= 7) {
        /*double bestHeuristicValue = Double.NEGATIVE_INFINITY;

            for (String cat : state.availableCategories) {
                int score = engine.calculateCategoryScore(cat, state.dice);

                double target = targetScores.getOrDefault(cat, 0.0);
                double heuristicValue = score - target;

                if (heuristicValue >= bestHeuristicValue) {
                    bestHeuristicValue = heuristicValue;
                }
            }
            return bestHeuristicValue;*/


        double max = 0;
        for(String cat: state.availableCategories){
            int value = engine.calculateCategoryScore(cat, state.dice);
            max = Math.max(value, max);
        }
        return max;
    }

    private double maxValue(State state){
        double bestValue = Double.NEGATIVE_INFINITY;
        for (boolean[] holdOption : allHoldCombinations(state.dice.length)) {
            State nextState = new State(
                    state.dice,
                    holdOption,
                    state.rollsLeft - 1,
                    state.availableCategories,
                    state.botScore,
                    state.humanScore
            );
            double value = expectimax(nextState, false);
            bestValue = Math.max(bestValue, value);
        }
        return bestValue;
    }

    private double expValue(State state) {
        int freeDice = 0;
        for (boolean h : state.held) {
            if (!h) freeDice++;
        }

        List<int[]> combinations = new ArrayList<>();
        generateCombinations(freeDice, 1, new int[freeDice], combinations);

        double total = 0.0;

        for (int[] combo : combinations) {
            int[] newDice = Arrays.copyOf(state.dice, state.dice.length);
            int idx = 0;
            for (int i = 0; i < newDice.length; i++) {
                if (!state.held[i]) {
                    newDice[i] = combo[idx++];
                }
            }

            double prob = combinationProbability(combo);

            State nextState = new State(
                    newDice,
                    state.held,
                    state.rollsLeft - 1,
                    state.availableCategories,
                    state.botScore,
                    state.humanScore
            );

            double value = expectimax(nextState, true);

            total += prob * value;
        }

        return total;
    }

    private List<boolean[]> allHoldCombinations(int n) {
        List<boolean[]> combinations = new ArrayList<>();
        int total = (int) Math.pow(2, n);

        for (int i = 0; i < total; i++) {
            boolean[] hold = new boolean[n];
            for (int j = 0; j < n; j++) {
                hold[j] = ((i >> j) & 1) == 1; // зсув і вправо на j бітів
            }
            combinations.add(hold);
        }

        return combinations;
    }

    private void generateCombinations(int diceLeft, int start, int[] current, List<int[]> result) {
        if (diceLeft == 0) {
            result.add(Arrays.copyOf(current, current.length));
            return;
        }
        for (int i = start; i <= 6; i++) {
            current[current.length - diceLeft] = i;
            generateCombinations(diceLeft - 1, i, current, result);
        }
    }

    private double combinationProbability(int[] combo) {
        int k = combo.length;
        int totalPermutations = (int) Math.pow(6, k);

        Map<Integer, Integer> freq = new HashMap<>();
        for (int v : combo) freq.put(v, freq.getOrDefault(v, 0) + 1);

        int permutationsOfCombo = factorial(k);
        for (int count : freq.values()) {
            permutationsOfCombo /= factorial(count);
        }

        return (double) permutationsOfCombo / totalPermutations;
    }

    private int factorial(int n) {
        int f = 1;
        for (int i = 2; i <= n; i++) f *= i;
        return f;
    }

    public void setLastCategory(String cat) { lastCategory = cat; }
    public String getLastCategory() { return lastCategory; }

    public void setLastScore(int score) { lastScore = score; }
    public int getLastScore() { return lastScore; }
}
