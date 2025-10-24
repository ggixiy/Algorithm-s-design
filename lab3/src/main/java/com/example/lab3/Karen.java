package com.example.lab3;

import java.util.*;

public class Karen extends Player {

    private final Random random = new Random();
    private String lastCategory;
    private int lastScore;

    public Karen(String name) {
        super(name);
    }

    @Override
    public String chooseCategory(UI ui) {
        // Бот выбирает случайную категорию, где 0 очков
        List<String> available = new ArrayList<>();
        for (var e : table.getAll().entrySet()) {
            String category = e.getKey();
            int value = e.getValue().get(); // отримуємо значення з IntegerProperty
            if (!table.isUsed(category) && !category.equals("Sum") && !category.equals("Bonus") && !category.equals("Yatzy bonus")) {
                available.add(category);
            }
        }
        if (available.isEmpty()) return "Chance";
        return available.get(random.nextInt(available.size()));
    }

    public void setLastCategory(String cat) { lastCategory = cat; }
    public String getLastCategory() { return lastCategory; }

    public void setLastScore(int score) { lastScore = score; }
    public int getLastScore() { return lastScore; }

    public void makeHoldDecisions(DiceManager dice) {
        // Примитивная логика: бот удерживает все кубики со значением >= 5
        int[] vals = dice.getValues();
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] >= 5) dice.hold(i);
        }
    }
}
