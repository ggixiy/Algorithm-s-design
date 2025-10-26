package com.example.lab3;

public interface UI {
    void showMessage(String text);
    void showDice(int[] dice);
    boolean askHold(int index);
    public void releaseHoldBoxes();
}
