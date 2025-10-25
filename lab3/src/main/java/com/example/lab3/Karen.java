package com.example.lab3;

import java.util.*;

public class Karen extends Player {

    private final Random random = new Random();
    private String lastCategory;
    private int lastScore;

    public Karen(String name) {
        super(name);
    }

    public String chooseCategory(UI ui) {
        FxUI fxui = (FxUI) ui;
        GameEngine engine = fxui.engine;

        // Отримуємо список доступних категорій
        List<String> available = new ArrayList<>();
        for (var e : table.getAll().entrySet()) {
            String category = e.getKey();
            if (!table.isUsed(category) &&
                    !category.equals("Sum") &&
                    !category.equals("Bonus") &&
                    !category.equals("Yatzy bonus")) {
                available.add(category);
            }
        }

        if (available.isEmpty()) return "Chance";

        // Викликаємо мінімакс для кожної категорії
        String bestCategory = null;
        double bestValue = Double.NEGATIVE_INFINITY;

        for (String cat : available) {
            double value = minimax(engine, cat, 3, true);
            if (value > bestValue) {
                bestValue = value;
                bestCategory = cat;
            }
        }

        if (bestCategory == null) {
            // fallback якщо щось піде не так
            bestCategory = available.get(random.nextInt(available.size()));
        }

        return bestCategory;
    }

    /**
     * Алгоритм мінімакс (спрощений, 1 хід уперед).
     * Глибина = 1 → оцінюємо лише поточну позицію.
     */
    private double minimax(GameEngine engine, String category, int depth, boolean maximizingPlayer) {
        if (depth == 0) {
            return evaluateState(engine);
        }

        // Поточний стан: якщо бот вибере цю категорію
        int score = engine.calculateCategoryScore(category);

        // Далі можна уявити, що гравець людина відповість оптимально (мінімізує результат)
        double opponentResponse = evaluateOpponentPotential(engine);

        // Класична формула мінімаксу
        if (maximizingPlayer) {
            return score - opponentResponse;
        } else {
            return opponentResponse - score;
        }
    }

    /**
     * Оцінює поточний стан гри для Karen.
     * Використовується як "оцінювальна функція" у мінімакс.
     */
    private double evaluateState(GameEngine engine) {
        int totalBot = engine.getBot().getTable().getTotal();
        int totalHuman = engine.getCurrentPlayer().getTable().getTotal();
        return totalBot - totalHuman;
    }

    /**
     * Оцінює потенціал відповіді опонента (евристично — на основі його суми).
     */
    private double evaluateOpponentPotential(GameEngine engine) {
        Player human = engine.getCurrentPlayer();
        int totalHuman = human.getTable().getTotal();
        int remaining = 13 - countUsedCategories(human);
        if (remaining <= 0) return 0;
        return (double) totalHuman / remaining;
    }

    private int countUsedCategories(Player player) {
        int count = 0;
        for (var e : player.getTable().getAll().entrySet()) {
            if (player.getTable().isUsed(e.getKey())) count++;
        }
        return count;
    }

    /**
     * Хід перекидання кубиків (бот намагається побудувати комбінацію).
     * Частина стратегії мінімаксу — "максимізація шансів" поточного стану.
     */
    public void makeHoldDecisions(DiceManager dice) {
        int[] vals = dice.getValues();
        Map<Integer, Integer> freq = new HashMap<>();
        for (int v : vals) freq.put(v, freq.getOrDefault(v, 0) + 1);

        int mostCommon = Collections.max(freq.entrySet(), Map.Entry.comparingByValue()).getKey();

        for (int i = 0; i < vals.length; i++) {
            if (vals[i] == mostCommon) dice.hold(i);
        }
    }

    public void setLastCategory(String cat) { lastCategory = cat; }
    public String getLastCategory() { return lastCategory; }

    public void setLastScore(int score) { lastScore = score; }
    public int getLastScore() { return lastScore; }
}
