package com.example.lab4;

import java.util.Random;

public class Generator {
    private Random r = new Random();

    public void Generate(int n, String currentDB){
        String record;
        try (java.io.PrintWriter writer = new java.io.PrintWriter(currentDB)) {
            for (int i = 0; i < n; i++) {
                record = randomString(r.nextInt(10, 100));
                writer.println( i + 1 + ";" + record);
            }
            System.out.println("Generated " + n + " records into database.txt");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public String randomString(int length){
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = r.nextInt(letters.length());
            sb.append(letters.charAt(index));
        }
        return sb.toString();
    }
}
