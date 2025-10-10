package com.example.lab3;

public interface UI {
    void showMessage(String text);
    void showDice(int[] dice);
    String askCategory();
    boolean askHold(int index);
}
