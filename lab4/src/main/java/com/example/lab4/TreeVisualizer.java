package com.example.lab4;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class TreeVisualizer {

    private final Pane pane;
    private final double NODE_RADIUS = 20.0;
    private final double VERTICAL_GAP = 70.0;

    public TreeVisualizer(Pane pane) {
        this.pane = pane;
    }

    public void draw(Node root, AVLTree tree) {
        pane.getChildren().clear();
        if (root == null) {
            return;
        }

        int nodeCount = tree.getAllNodes(root).size();
        if (nodeCount > 100) {
            Text warning = new Text(20, 40, "Tree is too large to visualize (" + nodeCount + " nodes)");
            warning.setStyle("-fx-font-size: 16px;");
            pane.getChildren().add(warning);
            return;
        }

        // Встановлюємо рекомендований розмір панелі, щоб ScrollPane знав, наскільки потрібно прокручувати
        int height = tree.height(root);
        double paneWidth = Math.pow(2, height - 1) * (NODE_RADIUS * 2.5);
        double paneHeight = height * VERTICAL_GAP + (2 * VERTICAL_GAP);

        // Встановлюємо мінімальний розмір, щоб уникнути колапсу
        pane.setPrefSize(Math.max(paneWidth, 430), Math.max(paneHeight, 500));

        // Починаємо рекурсивне малювання
        // Починаємо з x = половина ширини, y = невеликий відступ зверху
        // Горизонтальний відступ = чверть ширини (зменшується з глибиною)
        drawRecursive(root, tree, pane.getPrefWidth() / 2, VERTICAL_GAP, pane.getPrefWidth() / 4);
    }

    private void drawRecursive(Node node, AVLTree tree, double x, double y, double hGap) {
        if (node == null) {
            return;
        }

        // --- Малюємо дочірні вузли та лінії до них ---

        // Лівий дочірній вузол
        if (node.getLeft() != null) {
            double childX = x - hGap;
            double childY = y + VERTICAL_GAP;
            // Малюємо лінію від поточного вузла до дочірнього
            pane.getChildren().add(new Line(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS));
            // Рекурсивний виклик для лівого піддерева
            drawRecursive(node.getLeft(), tree, childX, childY, hGap / 2);
        }

        // Правий дочірній вузол
        if (node.getRight() != null) {
            double childX = x + hGap;
            double childY = y + VERTICAL_GAP;
            // Малюємо лінію від поточного вузла до дочірнього
            pane.getChildren().add(new Line(x, y + NODE_RADIUS, childX, childY - NODE_RADIUS));
            // Рекурсивний виклик для правого піддерева
            drawRecursive(node.getRight(), tree, childX, childY, hGap / 2);
        }

        // --- Малюємо поточний вузол (поверх ліній) ---

        // Малюємо коло
        Circle circle = new Circle(x, y, NODE_RADIUS);
        circle.setFill(Color.LIGHTBLUE);
        circle.setStroke(Color.BLACK);

        // Малюємо текст (ключ вузла)
        Text text = new Text(String.valueOf(node.getKey()));
        // Центруємо текст у колі
        text.setX(x - text.getLayoutBounds().getWidth() / 2);
        text.setY(y + text.getLayoutBounds().getHeight() / 4);



        Text height = new Text(String.valueOf(tree.getBalance(node)));
        height.setX(x + 20 + height.getLayoutBounds().getWidth() / 2);
        height.setY(y + height.getLayoutBounds().getHeight() / 4);

        pane.getChildren().addAll(circle, text, height);
    }
}