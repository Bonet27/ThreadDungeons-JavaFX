package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
    @FXML
    private Button btn_register;
    @FXML
    private Text errorMsg;
    private MainApp mainApp;
    private Socket socket;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setServerIp(String serverIp) {
        mainApp.setServerIp(serverIp);
    }

    @FXML
    private void initialize() {
        btn_login.setOnAction(event -> handleLoginButtonAction());
        btn_register.setOnAction(event -> handleRegisterButtonAction());
    }

    private void handleLoginButtonAction() {
        String login = inputUsuario.getText();
        String password = inputPassword.getText();

        if (login.isEmpty() || password.isEmpty()) {
            errorMsg.setText("El usuario y la contraseña no pueden estar vacíos.");
            errorMsg.setVisible(true);
            return;
        }

        try {
            socket = new Socket(mainApp.getServerIp(), 2000);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            output.writeUTF("login");
            output.writeUTF(login);
            output.writeUTF(password);
            output.flush();

            boolean authenticated = input.readBoolean();

            if (authenticated) {
                mainApp.openMainView(socket);
            } else {
                errorMsg.setText("Usuario o contraseña incorrectos.");
                errorMsg.setVisible(true);
                socket.close();
            }
        } catch (IOException e) {
            errorMsg.setText("Error al conectar con el servidor.");
            errorMsg.setVisible(true);
        }
    }

    private void handleRegisterButtonAction() {
        mainApp.openRegisterView();
    }
}
