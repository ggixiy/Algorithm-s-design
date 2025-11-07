package com.example.lab3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class FxUI extends Application implements UI {

    public GameEngine engine;
    private final TextArea log = new TextArea();
    private final CheckBox[] holdBoxes = new CheckBox[5];
    private final DiceView[] diceViews = new DiceView[5];

    private final TableView<String> scoreTable = new TableView<>();
    private final ObservableList<String> categories = FXCollections.observableArrayList();

    private Button rollButton;
    private boolean canRoll = true;

    private Player human;
    private Karen bot;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));

        HBox diceBox = new HBox(10);
        for (int i = 0; i < 5; i++) {
            VBox diePane = new VBox(5);
            DiceView diceView = new DiceView();
            diceViews[i] = diceView;

            VBox.setMargin(diceView, new Insets(0, 0, 35, 0));

            CheckBox hold = new CheckBox("Hold");
            holdBoxes[i] = hold;
            holdBoxes[i].setVisible(false);
            diePane.getChildren().addAll(diceView, hold);
            diceBox.getChildren().add(diePane);
        }

        rollButton = new Button("Roll dices");
        Button startButton = new Button("Start");

        HBox buttonsBox = new HBox(10, startButton, rollButton);

        log.setPrefHeight(200);
        log.setEditable(false);

        human = new Player("Player");
        bot = new Karen("Karen");

        initScoreTable();
        scoreTable.setPrefHeight(550);

        root.getChildren().addAll(
                diceBox,
                buttonsBox,
                new Label("Results table:"),
                scoreTable,
                new Label("Logs:"),
                log
        );

        Scene scene = new Scene(root, 700, 710);
        stage.setTitle("Yatzy");
        stage.setScene(scene);
        stage.show();

        engine = new GameEngine(human, bot, this);
        bot.setEngine(engine);

        startButton.setOnAction(e -> {
            clearLog();
            resetTable();
            scoreTable.refresh();
            releaseHoldBoxes();
            showEmptyDice();
            rollButton.setDisable(false);
            canRoll = true;
            engine.start();
        });

        rollButton.setOnAction(e -> {
            if (!canRoll) return;
            engine.playTurn(engine.getCurrentPlayer());
            updateRollButtonStatus();
        });

        scoreTable.setOnMouseClicked(event -> {
            if (!engine.getCurrentPlayer().equals(human)) return;

            String selected = scoreTable.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (human.getTable().isUsed(selected)) {
                showMessage("You already scored this category!");
                return;
            }

            if (engine.getRollCount() == 0) {
                showMessage("You need to roll the dice before choosing a category!");
                return;
            }

            if (selected.equals("Sum") || selected.equals("Bonus") || selected.equals("Yatzy bonus")) {
                showMessage("This category is countable. You cannot score your points here.");
                return;
            }

            int score = engine.calculateCategoryScore(selected);
            engine.applyCategory(selected, score);
            human.getTable().setScore(selected, score);
            scoreTable.refresh();
            showMessage(human.getName() + " scored " + score + " points in " + selected);

            engine.resetRollCount();
            canRoll = true;
            rollButton.setDisable(false);
            releaseHoldBoxes();

            engine.nextTurn();
            updateBotRow();
        });
    }

    private void initScoreTable() {
        TableColumn<String, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        categoryCol.setPrefWidth(250);

        TableColumn<String, String> humanCol = new TableColumn<>("Player");
        humanCol.setCellValueFactory(data -> {
            String category = data.getValue();
            IntegerProperty scoreProp = human.getTable().getScoreProperty(category);
            String display = scoreProp.get() == -1 ? "" : String.valueOf(scoreProp.get());
            return new SimpleStringProperty(display);
        });

        humanCol.setCellFactory(col -> new TableCell<String, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                String category = getTableView().getItems().get(getIndex());
                IntegerProperty scoreProp = human.getTable().getScoreProperty(category);
                int currentScore = scoreProp.get();

                setText(currentScore == -1 ? "" : String.valueOf(currentScore));

                if (engine == null || engine.getRollCount() == 0 || engine.getCurrentPlayer() != human) {
                    setStyle("");
                    return;
                }

                int possibleScore = engine.calculateCategoryScore(category);

                if (!human.getTable().isUsed(category) && possibleScore > 0) {
                    setText(String.valueOf(possibleScore));
                    setStyle("-fx-background-color: #ffcccc;");
                } else {
                    setStyle("");
                }
            }
        });
        humanCol.setPrefWidth(150);

        TableColumn<String, String> botCol = new TableColumn<>("Karen");
        botCol.setCellValueFactory(data -> {
            String category = data.getValue();
            IntegerProperty scoreProp = bot.getTable().getScoreProperty(category);
            String display = scoreProp.get() == -1 ? "" : String.valueOf(scoreProp.get());
            return new SimpleStringProperty(display);
        });

        botCol.setCellFactory(col -> new TableCell<String, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                String category = getTableView().getItems().get(getIndex());
                IntegerProperty scoreProp = bot.getTable().getScoreProperty(category);
                int currentScore = scoreProp.get();

                setText(currentScore == -1 ? "" : String.valueOf(currentScore));

                if (engine != null && engine.getRollCount() > 0 && engine.getCurrentPlayer() == bot) {
                    int possibleScore = engine.calculateCategoryScore(category);
                    if (!bot.getTable().isUsed(category) && possibleScore > 0) {
                        setText(String.valueOf(possibleScore));
                        setStyle("-fx-background-color: #ccffcc;");
                    } else {
                        setStyle("");
                    }
                } else {
                    setStyle("");
                }
            }
        });
        botCol.setPrefWidth(150);

        scoreTable.getColumns().addAll(categoryCol, humanCol, botCol);
        scoreTable.setItems(categories);

        categories.addAll(
                "Ones", "Twos", "Threes", "Fours", "Fives", "Sixes",
                "Sum", "Bonus",
                "Three of a kind", "Four of a kind", "Full house",
                "Small straight", "Large straight", "Yatzy", "Yatzy bonus", "Chance"
        );
    }

    private void updateBotRow() {
        String category = bot.getLastCategory();
        int score = bot.getLastScore();

        if (category == null) return;

        bot.getTable().setScore(category, score);

        scoreTable.refresh();
    }

    private void updateRollButtonStatus() {
        if (engine.getRollCount() >= 3) {
            rollButton.setDisable(true);
            canRoll = false;
            showMessage("Only two rerolls are possible. Please chose category in table.");
        }
    }

    private void resetTable() {
        human.getTable().reset();
        bot.getTable().reset();
        scoreTable.refresh();
    }

    @Override
    public TableView<String> getScoreTable() {
        return scoreTable;
    }

    @Override
    public void showMessage(String text) {
        log.appendText(text + "\n");
    }

    @Override
    public void showBotHold(boolean[] held) {
        Platform.runLater(() -> {
            for (int i = 0; i < held.length; i++) {
                holdBoxes[i].setSelected(held[i]);
            }
        });
    }

    @Override
    public void showDice(int[] dice) {
        for (int i = 0; i < dice.length; i++) {
            diceViews[i].setNumber(dice[i]);
            holdBoxes[i].setVisible(true);
        }

        scoreTable.refresh();
    }

    @Override
    public boolean askHold(int index) {
        return holdBoxes[index].isSelected();
    }

    @Override
    public void releaseHoldBoxes(){
        for (CheckBox holdBox : holdBoxes) {
            holdBox.setSelected(false);
        }
    }

    private void showEmptyDice() {
        for (int i = 0; i < diceViews.length; i++) {
            diceViews[i].setNumber(0); // 0 → порожнє
            holdBoxes[i].setVisible(false);
            holdBoxes[i].setSelected(false);
        }
    }

    private void clearLog() {
        log.clear();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
