package com.example.lab3;

import javafx.scene.control.TableView;

public interface UI {
    void showMessage(String text);
    void showDice(int[] dice);
    boolean askHold(int index);
    void showBotHold(boolean[] held);
    TableView<String> getScoreTable();
    void releaseHoldBoxes();
}
