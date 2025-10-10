package com.example.lab3;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FxUI extends Application implements UI {

    private GameEngine engine;
    private final TextArea log = new TextArea();
    private final CheckBox[] holdBoxes = new CheckBox[5];
    private final Label[] diceLabels = new Label[5];
    private final TextField categoryInput = new TextField();

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        // Панель кубиков
        HBox diceBox = new HBox(10);
        for (int i = 0; i < 5; i++) {
            VBox diePane = new VBox(5);
            Label label = new Label("Кубик " + (i + 1) + ": -");
            diceLabels[i] = label;
            CheckBox hold = new CheckBox("Удержать");
            holdBoxes[i] = hold;
            diePane.getChildren().addAll(label, hold);
            diceBox.getChildren().add(diePane);
        }

        // Кнопки управления
        Button rollButton = new Button("Бросить кубики");
        Button nextButton = new Button("Следующий игрок");
        Button startButton = new Button("Начать игру");

        // Ввод категории
        HBox categoryBox = new HBox(10);
        categoryBox.getChildren().addAll(new Label("Категория:"), categoryInput);

        // Лог
        log.setPrefHeight(200);
        log.setEditable(false);

        root.getChildren().addAll(
                new Label("🎲 Ятзи — Игрок против Робота 🤖"),
                diceBox,
                rollButton,
                categoryBox,
                startButton,
                nextButton,
                new Label("Лог:"),
                log
        );

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Yahtzee");
        stage.setScene(scene);
        stage.show();

        // Инициализация движка
        Player human = new Player("Игрок 1");
        Karen bot = new Karen("Робот");
        engine = new GameEngine(human, bot, this);

        // Логика кнопок
        startButton.setOnAction(e -> {
            clearLog();
            log.appendText("Игра началась!\n");
            engine.start();
        });

        rollButton.setOnAction(e -> {
            engine.playTurn(engine.getCurrentPlayer());
        });

        nextButton.setOnAction(e -> {
            engine.nextTurn();
        });
    }

    @Override
    public void showMessage(String text) {
        log.appendText(text + "\n");
    }

    @Override
    public void showDice(int[] dice) {
        for (int i = 0; i < dice.length; i++) {
            diceLabels[i].setText("Кубик " + (i + 1) + ": " + dice[i]);
        }
    }

    @Override
    public String askCategory() {
        return categoryInput.getText().trim().toUpperCase();
    }

    @Override
    public boolean askHold(int index) {
        return holdBoxes[index].isSelected();
    }

    private void clearLog() {
        log.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
