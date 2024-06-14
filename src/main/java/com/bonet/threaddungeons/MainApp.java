package com.bonet.threaddungeons;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Objects;

public class MainApp extends Application {
    private static Stage stage;

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    private Socket socket; // Socket compartido

    public String getServerIp() {
        return serverIp;
    }

    private String serverIp = "127.0.0.1"; // Default IP

    @Override
    public void start(Stage primaryStage) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/gold.png")));
        primaryStage.getIcons().add(icon);
        stage = primaryStage;
        primaryStage.setTitle("Thread Dungeons - Game");
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        openIPConfigView();
    }

    public static Stage getStage() {
        return stage;
    }

    public void openIPConfigView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("IPConfig-view.fxml"));
            Parent root = loader.load();
            IPConfigController controller = loader.getController();
            controller.setMainApp(this);

            Stage ipConfigStage = new Stage();
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/gold.png")));
            ipConfigStage.getIcons().add(icon);
            ipConfigStage.setTitle("Thread Dungeons - Server IP Configuration");
            ipConfigStage.setScene(new Scene(root));
            ipConfigStage.showAndWait();

            openLoginView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("Login-view.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setMainApp(this);
            loginController.setServerIp(serverIp);
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
            this.socket = socket;
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
            Socket newSocket = new Socket(serverIp, 2000); // Crear un nuevo socket solo para el GameOverController
            gameOverController.setSocket(newSocket); // Pasar el nuevo socket al controlador
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }
}
