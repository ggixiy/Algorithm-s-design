package com.example.lab4;

import javafx.beans.property.*;

public class Node {
    private IntegerProperty key = new SimpleIntegerProperty();
    private StringProperty data = new SimpleStringProperty();
    private Node left;
    private Node right;
    private int height;

    public Node(int id, String data) {
        this.key.set(id);
        this.data.set(data);
        left = null;
        right = null;
        height = 1;
    }

    public int getKey() { return key.get(); }
    public void setKey(int value) { key.set(value); }
    public IntegerProperty getIdProperty() { return key; }

    public String getData() { return data.get(); }
    public void setData(String value) { data.set(value); }
    public StringProperty getDataProperty() { return data; }

    public Node getLeft() { return left; }
    public void setLeft(Node n) { left = n; }

    public Node getRight() { return right; }
    public void setRight(Node n) { right = n; }

    public int getHeight() { return height;}
    public void setHeight(int h) {height = h;}
}
