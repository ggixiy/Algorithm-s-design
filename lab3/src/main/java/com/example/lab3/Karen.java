package com.example.lab3;

import java.util.*;

public class Karen extends Player {

    private final Random random = new Random();
    private String lastCategory;
    private int lastScore;
    private GameEngine engine;

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

        int[] diceValues = engine.getDices();
        String bestCategory = null;
        int bestScore = -1;

        for (String cat : availableCategories) {
            int score = engine.calculateCategoryScore(cat, diceValues);
            if (score > bestScore) {
                bestScore = score;
                bestCategory = cat;
            }
        }

        // Якщо не знайшли (дуже рідко) — випадково
        if (bestCategory == null)
            bestCategory = availableCategories.get(new Random().nextInt(availableCategories.size()));

        return bestCategory;
    }

    public void makeHoldDecisions(DiceManager dice) {
        int[] currentDice = dice.getValues();
        boolean[] bestHold = new boolean[currentDice.length];
        double bestValue = Double.NEGATIVE_INFINITY;

        // Поточний стан гри
        List<String> availableCategories = new ArrayList<>();
        for (var e : table.getAll().entrySet()) {
            String cat = e.getKey();
            if (!table.isUsed(cat) && !cat.equals("Sum") && !cat.equals("Bonus") && !cat.equals("Yatzy bonus")) {
                availableCategories.add(cat);
            }
        }

        // Перебираємо всі варіанти утримання кубиків
        for (boolean[] holdOption : allHoldCombinations(currentDice.length)) {
            State state = new State(
                    currentDice,
                    holdOption,
                    3 - engine.getRollCount(), // залишилося перекидань
                    availableCategories,
                    table.getTotal(),
                    engine.getHumanPlayer().getTable().getTotal()
            );

            double value = expectimax(state, false); // після Max вузла йде Chance
            if (value > bestValue) {
                bestValue = value;
                bestHold = holdOption;
            }
        }

        // Встановлюємо утримання у DiceManager
        for (int i = 0; i < bestHold.length; i++) {
            if (bestHold[i]) dice.hold(i);
            else dice.release(i);
        }
    }

    /**
     * Expectimax з заданою глибиною.
     */
    private double expectimax(State state, boolean isMax) {
        if (state.rollsLeft == 0) return evaluateState(state);

        if(isMax){
            return maxValue(state);
        }else{
            return expValue(state);
        }
    }

    private double evaluateState(State state) {
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
        // Кількість кубиків, які будемо перекидати
        int freeDice = 0;
        for (boolean h : state.held) {
            if (!h) freeDice++;
        }

        // Генеруємо всі комбінації кубиків без урахування порядку
        List<int[]> combinations = new ArrayList<>();
        generateCombinations(freeDice, 1, new int[freeDice], combinations);

        double total = 0.0;

        for (int[] combo : combinations) {
            // Створюємо новий масив кубиків, враховуючи утримані
            int[] newDice = Arrays.copyOf(state.dice, state.dice.length);
            int idx = 0;
            for (int i = 0; i < newDice.length; i++) {
                if (!state.held[i]) {
                    newDice[i] = combo[idx++];
                }
            }

            // Ймовірність комбінації
            double prob = combinationProbability(combo);

            // Створюємо новий стан
            State nextState = new State(
                    newDice,
                    state.held,
                    state.rollsLeft - 1,
                    state.availableCategories,
                    state.botScore,
                    state.humanScore
            );

            // Викликаємо expectimax рекурсивно, щоб визначити наступний вузол
            double value = expectimax(nextState, true); // після Chance йде Max

            total += prob * value;
        }

        return total;
    }

    private List<boolean[]> allHoldCombinations(int n) {
        List<boolean[]> combinations = new ArrayList<>();
        int total = 1 << n; // 2^n

        for (int mask = 0; mask < total; mask++) {
            boolean[] hold = new boolean[n];
            for (int i = 0; i < n; i++) {
                hold[i] = ((mask >> i) & 1) == 1;
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
