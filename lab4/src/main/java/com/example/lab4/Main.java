package com.example.lab4;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("database-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
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
