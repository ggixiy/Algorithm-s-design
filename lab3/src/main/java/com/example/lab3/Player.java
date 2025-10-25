package com.example.lab3;

public class Player {
    protected final String name;
    protected final ScoreTable table = new ScoreTable();

    public Player(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public ScoreTable getTable() { return table; }
}
