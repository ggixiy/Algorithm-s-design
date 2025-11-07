package com.example.lab3;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class DiceView extends Pane {
    private final ImageView imageView = new ImageView();

    public DiceView() {
        setPrefSize(40, 40);
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        getChildren().add(imageView);
    }

    public void setNumber(int n) {
        if (n < 1 || n > 6) {
            imageView.setImage(null);
            return;
        }
        Image img = new Image(getClass().getResourceAsStream("/images/dice" + n + ".png"));
        imageView.setImage(img);
    }
}
