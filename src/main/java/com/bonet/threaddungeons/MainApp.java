package com.bonet.threaddungeons;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

public class MainApp extends Application {
    private static Stage stage;
    private Socket socket;
    private static final String HOST = "localhost";
    private static final int Puerto = 2000;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.setTitle("Thread Dungeons");

        // Crear el socket al iniciar la aplicación
        socket = new Socket(HOST, Puerto);

        openLoginView();
    }

    public static Stage getStage() {
        return stage;
    }

    public Socket getSocket() {
        return socket;
    }

    public void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login-view.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main-view.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setMainApp(this); // Pasar la referencia de MainApp al MainController
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
