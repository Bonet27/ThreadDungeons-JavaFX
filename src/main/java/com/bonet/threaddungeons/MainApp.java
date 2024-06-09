package com.bonet.threaddungeons;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class MainApp extends Application {
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        primaryStage.setTitle("Thread Dungeons");
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        openLoginView();
    }

    public static Stage getStage() {
        return stage;
    }

    public void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("Login-view.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openRegisterView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Register-view.fxml"));
            Parent root = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setMainApp(this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openMainView(Socket socket) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main-view.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setMainApp(this);
            mainController.setSocket(socket);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void openGameOverView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameOver-view.fxml"));
            Parent root = loader.load();
            GameOverController gameOverController = loader.getController();
            gameOverController.setMainApp(this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
