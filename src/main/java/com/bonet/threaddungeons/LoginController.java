package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginController {
    @FXML
    private TextField inputUsuario;
    @FXML
    private PasswordField inputPassword;
    @FXML
    private Button btn_login;
    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        btn_login.setOnAction(event -> handleLoginButtonAction());
    }

    private void handleLoginButtonAction() {
        String login = inputUsuario.getText();
        String password = inputPassword.getText();

        try {
            Socket socket = mainApp.getSocket();
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            // Enviar credenciales de autenticación al servidor
            output.writeUTF(login);
            output.writeUTF(password);
            output.flush();

            boolean authenticated = input.readBoolean();

            if (authenticated) {
                mainApp.openMainView();
            } else {
                System.out.println("Autenticación fallida");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
