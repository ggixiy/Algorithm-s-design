package com.example.lab3;

public class GameEngine {

    private final Player human;
    private final Karen bot;
    private final DiceManager dice;
    private final UI ui;
    private Player currentPlayer;
    private boolean gameStarted = false;

    public GameEngine(Player human, Karen bot, UI ui) {
        this.human = human;
        this.bot = bot;
        this.ui = ui;
        this.dice = new DiceManager(5);
        this.currentPlayer = human;
    }

    public void start() {
        gameStarted = true;
        ui.showMessage("🎲 Welcome to Yahtzee!");
        ui.showMessage("The game has started. " + human.getName() + " goes first.");
        dice.reset();
    }

    public void playTurn(Player player) {
        if (!gameStarted) {
            ui.showMessage("You need to start the game first.");
            return;
        }

        ui.showMessage("\n--- " + player.getName() + "'s turn ---");
        dice.reset();
        dice.roll();
        ui.showDice(dice.getValues());

        if (player instanceof Karen) {
            playBotTurn();
        } else {
            playHumanTurn();
        }
    }

    private void playHumanTurn() {
        // check which dice are held
        for (int i = 0; i < 5; i++) {
            if (ui.askHold(i)) dice.hold(i);
        }

        // re-roll once for simplicity
        dice.roll();
        ui.showDice(dice.getValues());

        String category = ui.askCategory();
        if (category.isEmpty()) category = "CHANCE";

        int score = calcSimpleScore(dice.getValues());
        human.getTable().setScore(category, score);
        human.getTable().printScores(human.getName());

        ui.showMessage(human.getName() + " scored " + score + " points in " + category + ".");
    }

    private void playBotTurn() {
        ui.showMessage("🤖 Bot is thinking...");
        bot.makeHoldDecisions(dice);
        dice.roll();
        ui.showDice(dice.getValues());

        String category = bot.chooseCategory(ui);
        int score = calcSimpleScore(dice.getValues());
        bot.getTable().setScore(category, score);
        bot.getTable().printScores(bot.getName());

        ui.showMessage(bot.getName() + " chose category " + category + " and scored " + score + " points.");
    }

    public void nextTurn() {
        if (!gameStarted) {
            ui.showMessage("You need to start the game first.");
            return;
        }

        if (currentPlayer == human) {
            currentPlayer = bot;
        } else {
            currentPlayer = human;
        }

        ui.showMessage("\nNext player: " + currentPlayer.getName());
        dice.reset();

        if (currentPlayer instanceof Karen) {
            playTurn(bot);
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    private int calcSimpleScore(int[] values) {
        int sum = 0;
        for (int v : values) sum += v;
        return sum;
    }
}
