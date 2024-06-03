package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class RegisterController {
    @FXML
    private Button btn_createAcc;
    @FXML
    private Button btn_back;
    @FXML
    private Text errorMsg;
    @FXML
    private TextField inputEmail;
    @FXML
    private PasswordField inputPassword;
    @FXML
    private TextField inputUsuario;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        btn_createAcc.setOnAction(event -> handleRegisterButtonAction());
        btn_back.setOnAction(event -> handleGoLoginButtonAction());
    }

    private void handleRegisterButtonAction() {
        String username = inputUsuario.getText();
        String password = inputPassword.getText();
        String email = inputEmail.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMsg.setText("El usuario y la contraseña son obligatorios.");
            errorMsg.setVisible(true);
            return;
        }

        try {
            if (DatabaseManager.registerUser(username, password, email)) {
                errorMsg.setText("Usuario registrado con éxito.");
                errorMsg.setVisible(true);
            } else {
                errorMsg.setText("Error al registrar el usuario. Inténtelo de nuevo.");
                errorMsg.setVisible(true);
            }
        } catch (Exception e) {
            errorMsg.setText("Ocurrió un error inesperado. Inténtelo de nuevo.");
            errorMsg.setVisible(true);
        }
    }

    private void handleGoLoginButtonAction() {
        mainApp.openLoginView();
    }
}
