package com.example.lab3;

import java.util.HashMap;
import java.util.Map;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.util.Duration;

public class GameEngine {

    private final Player human;
    private final Karen bot;
    private final DiceManager dice;
    private final UI ui;

    private Player currentPlayer;
    private boolean gameStarted = false;

    private int rollCount = 0;
    private static final int secondYatzyBonus = 100;

    public GameEngine(Player human, Karen bot, UI ui) {
        this.human = human;
        this.bot = bot;
        this.ui = ui;
        this.dice = new DiceManager(5);
        this.currentPlayer = human;
    }

    public void start() {
        gameStarted = true;
        currentPlayer = human;
        rollCount = 0;
        dice.reset();
        ui.showMessage("Yatzy!");
        ui.showMessage("The game starts with " + human.getName());
    }

    private void checkEndGame() {
        if (human.getTable().isFull() && bot.getTable().isFull()) {
            gameStarted = false;

            int humanTotal = human.getTable().getTotal();
            int botTotal = bot.getTable().getTotal();

            ui.showMessage("\n===== GAME OVER =====");
            ui.showMessage(human.getName() + " total score: " + humanTotal);
            ui.showMessage(bot.getName() + " total score: " + botTotal);

            if (humanTotal > botTotal) {
                ui.showMessage(human.getName() + " wins!");
            } else if (botTotal > humanTotal) {
                ui.showMessage(bot.getName() + " wins!");
            } else {
                ui.showMessage("It's a draw!");
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int[] getDices() {
        return dice.getValues();
    }

    public Player getHumanPlayer() {
        return human;
    }

    public int getRollCount() {
        return rollCount;
    }

    public void resetRollCount() {
        rollCount = 0;
    }

    public void playTurn(Player player) {
        if (!gameStarted) {
            ui.showMessage("Press button 'Start' to begin");
            return;
        }

        if (player != currentPlayer) {
            ui.showMessage("It`s " + currentPlayer.getName() + " turn.");
            return;
        }

        if (player instanceof Karen) {
            playBotTurn();
            return;
        }

        playHumanTurn();
    }

    public void nextTurn() {
        if (!gameStarted) {
            ui.showMessage("Press button to start.");
            return;
        }

        currentPlayer = (currentPlayer == human) ? bot : human;

        ui.showMessage("\nNext player: " + currentPlayer.getName());

        dice.reset();
        rollCount = 0;

        if (currentPlayer instanceof Karen) {
            playTurn(bot);
        }
    }

    private void playHumanTurn() {
        if (rollCount >= 3) {
            ui.showMessage("You have already rolled dices 3 times. Choose category in table.");
            return;
        }

        if (rollCount == 0) {
            dice.releaseAll();
            dice.roll();
            ui.showDice(dice.getValues());
            rollCount = 1;
            if (isYatzyRolled()) {
                ui.showMessage("🎉 YATZY! All dices are the same!");
                handleYatzyAuto();
                return;
            } else ui.showMessage("First roll. Choose dices that you want to hold and roll again.");
            return;
        }

        for (int i = 0; i < 5; i++) {
            if (ui.askHold(i)) {
                dice.hold(i);
            }
        }

        dice.roll();
        ui.showDice(dice.getValues());
        if (isYatzyRolled()) {
            ui.showMessage("🎉 YATZY! All dices are the same!");
            handleYatzyAuto();
        }else {
            rollCount++;
            if (rollCount >= 3) {
                ui.showMessage("Third roll. Choose category.");
            } else {
                ui.showMessage("Reroll is possible.");
            }
        }
    }

    private void playBotTurn() {
        ui.showMessage(bot.getName() + " is making move...");
        dice.releaseAll();
        rollCount = 0;

        playBotRoll();
    }

    private void playBotRoll() {
        if (rollCount >= 3) {
            PauseTransition delayBeforeCategory = new PauseTransition(Duration.seconds(2));
            delayBeforeCategory.setOnFinished(event -> {
                String category = bot.chooseCategory(ui);
                int score = calculateCategoryScore(category);
                applyCategory(category, score);

                Platform.runLater(() -> ui.releaseHoldBoxes());
            });
            delayBeforeCategory.play();
            return;
        }

        dice.roll();
        ui.showDice(dice.getValues());
        Platform.runLater(() -> ((FxUI) ui).getScoreTable().refresh());

        if (isYatzyRolled()) {
            ui.showMessage("🎉 YATZY! All dices are the same!");
            handleYatzyAuto();
            return;
        }

        bot.makeHoldDecisions(dice);
        ui.showBotHold(dice.held);

        rollCount++;

        Platform.runLater(() -> ((FxUI) ui).getScoreTable().refresh());

        PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
        delay.setOnFinished(event -> playBotRoll());
        delay.play();
    }

    public int calculateCategoryScore(String category, int[] diceValues) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int v : diceValues) counts.put(v, counts.getOrDefault(v, 0) + 1);

        switch (category) {
            case "Ones": return sumOf(diceValues, 1);
            case "Twos": return sumOf(diceValues, 2);
            case "Threes": return sumOf(diceValues, 3);
            case "Fours": return sumOf(diceValues, 4);
            case "Fives": return sumOf(diceValues, 5);
            case "Sixes": return sumOf(diceValues, 6);

            case "Three of a kind":
                return counts.values().stream().anyMatch(v -> v >= 3) ? sumAll(diceValues) : 0;

            case "Four of a kind":
                return counts.values().stream().anyMatch(v -> v >= 4) ? sumAll(diceValues) : 0;

            case "Full house":
                return (counts.containsValue(3) && counts.containsValue(2)) ? 25 : 0;

            case "Small straight":
                return hasStraight(diceValues, 4) ? 30 : 0;

            case "Large straight":
                return hasStraight(diceValues, 5) ? 40 : 0;

            case "Yatzy":
                return counts.values().stream().anyMatch(v -> v == 5) ? 50 : 0;

            case "Chance":
                return sumAll(diceValues);

            default:
                return 0;
        }
    }

    public int calculateCategoryScore(String category) {
        return calculateCategoryScore(category, dice.getValues());
    }

    public void applyCategory(String category, int score) {
        if (currentPlayer == null) return;

        currentPlayer.getTable().setScore(category, score);
        ui.getScoreTable().refresh();
        ui.showMessage(currentPlayer.getName() + " scored " + score + " points in category " + category + ".");

        if (category.equals("Ones") || category.equals("Twos") || category.equals("Threes") ||
                category.equals("Fours") || category.equals("Fives") || category.equals("Sixes")) {
            updateSumAndBonus(currentPlayer);
        }

        if(currentPlayer instanceof Karen){
            bot.setLastCategory(category);
            bot.setLastScore(score);
            currentPlayer = human;
        }

        dice.reset();
        rollCount = 0;
        checkEndGame();
    }

    private int sumOf(int[] dice, int value) {
        int sum = 0;
        for (int v : dice) if (v == value) sum += v;
        return sum;
    }

    private int sumAll(int[] dice) {
        int sum = 0;
        for (int v : dice) sum += v;
        return sum;
    }

    private boolean hasStraight(int[] dice, int length) {
        boolean[] present = new boolean[7]; // індекси 1..6
        for (int v : dice) {
            if (v >= 1 && v <= 6) present[v] = true;
        }

        int consecutive = 0;
        for (int i = 1; i <= 6; i++) {
            if (present[i]) {
                consecutive++;
                if (consecutive >= length) return true;
            } else {
                consecutive = 0;
            }
        }
        return false;
    }

    private void updateSumAndBonus(Player player) {
        ScoreTable table = player.getTable();
        int sum = 0;

        for (String cat : new String[]{"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes"}) {
            IntegerProperty prop = table.getAll().get(cat);
            if (prop != null && prop.get() != -1) {
                sum += prop.get();
            }
        }

        table.setScore("Sum", sum);
        int bonus = (sum >= 63) ? 35 : 0;
        table.setScore("Bonus", bonus);

        if (bonus == 35) {
            ui.showMessage(player.getName() + " received a 35-point bonus!");
        }
    }

    private void handleYatzyAuto() {
        ScoreTable table = currentPlayer.getTable();

        if (!table.isUsed("Yatzy")) {
            int score = 50;
            table.setScore("Yatzy", score);
            ui.showMessage(currentPlayer.getName() + " automatically scored 50 points in YATZY!");

            if (currentPlayer instanceof Karen botPlayer) {
                botPlayer.setLastCategory("Yatzy");
                botPlayer.setLastScore(score);
            }

        } else {
            IntegerProperty bonusProp = table.getAll().get("Yatzy bonus");
            int currentBonus = bonusProp.get() == -1 ? 0 : bonusProp.get();
            bonusProp.set(currentBonus + secondYatzyBonus);
            ui.showMessage(currentPlayer.getName() + " rolled another YATZY! Bonus +" + secondYatzyBonus + "!");
        }

        dice.reset();
        ui.releaseHoldBoxes();
        rollCount = 0;
        checkEndGame();

        nextTurn();
    }

    private boolean isYatzyRolled() {
        int[] values = dice.getValues();
        int first = values[0];
        for (int v : values) {
            if (v != first) return false;
        }
        return true;
    }
}
