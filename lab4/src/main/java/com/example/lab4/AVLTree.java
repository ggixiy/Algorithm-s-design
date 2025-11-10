package com.example.lab4;

import java.util.ArrayList;
import java.util.List;

public class AVLTree {

    private int comparisonCount = 0;

    public int getComparisonCount() {
        return comparisonCount;
    }

    public void resetComparisonCount() {
        comparisonCount = 0;
    }

    public static int height(Node n){
        if(n == null) return 0;
        return n.getHeight();
    }

    public Node insert(Node root, int k, String value){
        if(root == null) return new Node(k, value);

        if (k < root.getKey()){
            root.setLeft(insert(root.getLeft(), k, value));
        } else if (k > root.getKey()){
            root.setRight(insert(root.getRight(), k, value));
        } else
            return root;

        root.setHeight(Math.max(height(root.getLeft()), height(root.getRight())) + 1);

        return balance(root);
    }

    public Node delete(Node root, int k){
        if(root == null) return root;

        if(k < root.getKey()){
            root.setLeft(delete(root.getLeft(), k));
        } else if(k > root.getKey()){
            root.setRight(delete(root.getRight(), k));
        } else {
            if(root.getLeft() == null || root.getRight() == null){
                Node temp = root.getLeft() == null ? root.getRight() : root.getLeft();

                if(temp == null){
                    root = null;
                } else {
                    root = temp;
                }
            } else {
                Node temp = minValue(root.getRight());
                root.setKey(temp.getKey());
                root.setData(temp.getData());
                root.setRight(delete(root.getRight(), temp.getKey()));
            }
        }

        if(root == null) return root;

        root.setHeight(Math.max(height(root.getLeft()), height(root.getRight())) + 1);

        return balance(root);
    }

    public Node minValue(Node node){
        Node current = node;

        while(current.getLeft() != null){
            current = current.getLeft();
        }

        return current;
    }

    public Node search(Node root, int k){
        comparisonCount++;
        if(root == null || root.getKey() == k){
            return root;
        }

        comparisonCount++;
        if(k < root.getKey()){
            return search(root.getLeft(), k);
        } else {
            return search(root.getRight(), k);
        }
    }

    public Node update(Node root, int k, String value){
        Node toUpdate = search(root, k);
        if (toUpdate != null){
            toUpdate.setData(value);
        }
        return root;
    }

    public Node rightRotation(Node y){
        Node x = y.getLeft();
        Node T2 = x.getRight();

        x.setRight(y);
        y.setLeft(T2);

        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);
        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);

        return x;
    }

    public Node leftRotation(Node x){
        Node y = x.getRight();
        Node T2 = y.getLeft();

        y.setLeft(x);
        x.setRight(T2);

        x.setHeight(Math.max(height(x.getLeft()), height(x.getRight())) + 1);
        y.setHeight(Math.max(height(y.getLeft()), height(y.getRight())) + 1);

        return y;
    }

    public Node leftRightRotation(Node y) {
        y.setLeft(leftRotation(y.getLeft()));
        return rightRotation(y);
    }

    public Node rightLeftRotation(Node x) {
        x.setRight(rightRotation(x.getRight()));
        return leftRotation(x);
    }

    public int getBalance(Node node){
        return  height(node.getLeft()) - height(node.getRight());
    }

    public Node balance(Node root){
        int balance = getBalance(root);


        // RightRight
        if (balance < -1 && getBalance(root.getRight()) <= 0)
            return leftRotation(root);

        // LeftLeft
        if (balance > 1 && getBalance(root.getLeft()) >= 0)
            return rightRotation(root);

        // LeftRight
        if (balance > 1 && getBalance(root.getLeft()) < 0) {
            return leftRightRotation(root);
        }

        // RightLeft
        if (balance < -1 && getBalance(root.getRight()) > 0) {
            return rightLeftRotation(root);
        }

        return root;
    }

    public List<Node> getAllNodes(Node root) {
        java.util.List<Node> list = new ArrayList<>();
        inOrderTraversal(root, list);
        return list;
    }

    private void inOrderTraversal(Node node, List<Node> list) {
        if (node != null) {
            inOrderTraversal(node.getLeft(), list);
            list.add(node);
            inOrderTraversal(node.getRight(), list);
        }
    }
}
