package com.example.lab3;

import java.util.*;

public class Karen extends Player {

    private final Random random = new Random();

    public Karen(String name) {
        super(name);
    }

    @Override
    public String chooseCategory(UI ui) {
        // Бот выбирает случайную категорию, где 0 очков
        List<String> available = new ArrayList<>();
        for (var e : table.getAll().entrySet()) {
            if (e.getValue() == 0) available.add(e.getKey());
        }
        if (available.isEmpty()) return "CHANCE";
        return available.get(random.nextInt(available.size()));
    }

    public void makeHoldDecisions(DiceManager dice) {
        // Примитивная логика: бот удерживает все кубики со значением >= 5
        int[] vals = dice.getValues();
        for (int i = 0; i < vals.length; i++) {
            if (vals[i] >= 5) dice.hold(i);
        }
    }
}
