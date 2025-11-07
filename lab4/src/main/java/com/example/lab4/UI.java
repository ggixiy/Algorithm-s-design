package com.example.lab4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UI extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(UI.class.getResource("database-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Database Interface");
        stage.setScene(scene);
        stage.show();

        DataBaseController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(event -> controller.saveToFile());
    }

    public static void main(String[] args) {
        launch();
    }
}
