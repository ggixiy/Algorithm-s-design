package com.example.lab3;

import java.util.*;

public class ConsoleUI implements UI {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void showMessage(String text) {
        System.out.println(text);
    }

    @Override
    public void showDice(int[] dice) {
        System.out.println("Cubiki: " + Arrays.toString(dice));
    }

    @Override
    public String askCategory() {
        System.out.print("Enter category: ");
        return scanner.nextLine().trim().toUpperCase();
    }

    @Override
    public boolean askHold(int index) {
        System.out.print("hold cubik ili net " + (index + 1) + "? (y/n): ");
        return scanner.nextLine().trim().equalsIgnoreCase("y");
    }
}
