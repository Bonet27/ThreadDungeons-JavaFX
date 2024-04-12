package com.bonet.threaddungeons;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import java.io.IOException;

public class MainApp extends Application {

    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("Login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        MainApp.stage = stage;
        stage.setTitle("Thread Dungeons");
        stage.setScene(scene);
        stage.show();
        stage.setMaximized(true);
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}