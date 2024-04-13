package com.bonet.threaddungeons;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    private static Stage stage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Login-view.fxml")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Thread Dungeons");
        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}