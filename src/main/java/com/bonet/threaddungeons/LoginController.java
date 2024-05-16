package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Button btn_login, btn_register;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private TextField inputUsuario;

    @FXML
    private VBox vBox;

    @FXML
    private Label welcomeText;

    @FXML
    private Text errorMsg, gameTitle;

    @FXML
    private void initialize() {
        vBox.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                newValue.widthProperty().addListener((obs, oldVal, newVal) -> {
                    double fontSize = calculateFontSize(newValue.getWidth(), newValue.getHeight());
                    double fontSizeTitle = calculateFontSizeTitle(newValue.getWidth(), newValue.getHeight());
                    adjustFontSize(fontSize);
                    adjustFontSizeTitle(fontSizeTitle);
                });
                newValue.heightProperty().addListener((obs, oldVal, newVal) -> {
                    double fontSize = calculateFontSize(newValue.getWidth(), newValue.getHeight());
                    double fontSizeTitle = calculateFontSizeTitle(newValue.getWidth(), newValue.getHeight());
                    adjustFontSize(fontSize);
                    adjustFontSizeTitle(fontSizeTitle);
                });
            }
        });

        btn_login.setOnAction(event -> handleLoginButtonAction(event));
        btn_register.setOnAction(event -> handleRegisterButtonAction(event));
    }

    @FXML
    private void handleLoginButtonAction(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bonet/threaddungeons/Main-View.fxml"));
            Parent mainViewParent = loader.load();
            Scene mainViewScene = new Scene(mainViewParent);

            // Obtén el Stage actual
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cambia la escena
            window.setScene(mainViewScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorMsg.setText("Error al cargar la vista principal");
        }
    }

    @FXML
    private void handleRegisterButtonAction(javafx.event.ActionEvent event) {
        // Aquí puedes añadir la lógica para el registro
        System.out.println("Registro button pressed");
    }

    private double calculateFontSize(double width, double height) {
        return Math.min(width, height) / 25;
    }

    private double calculateFontSizeTitle(double width, double height) {
        return Math.min(width, height) / 10;
    }

    private void adjustFontSize(double fontSize) {
        welcomeText.setStyle("-fx-font-size: " + fontSize + "pt;");
        errorMsg.setStyle("-fx-font-size: " + fontSize / 2 + "pt;");
        inputUsuario.setStyle("-fx-font-size: " + fontSize / 2 + "pt;");
        inputPassword.setStyle("-fx-font-size: " + fontSize / 2 + "pt;");
        btn_login.setStyle("-fx-font-size: " + fontSize / 2 + "pt;");
        btn_register.setStyle("-fx-font-size: " + fontSize / 2 + "pt;");
        vBox.setSpacing(fontSize / 2);
    }

    private void adjustFontSizeTitle(double fontSize) {
        gameTitle.setStyle("-fx-font-size: " + fontSize + "pt;");
    }
}
