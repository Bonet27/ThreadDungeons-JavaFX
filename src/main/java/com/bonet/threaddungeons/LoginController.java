package com.bonet.threaddungeons;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    private Label errorMsg;
    private MainApp mainApp;
    private Socket socket;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setServerIp(String serverIp) {
        mainApp.setServerIp(serverIp);
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
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
            showError("El usuario y la contraseña no pueden estar vacíos.");
            return;
        }

        // Evitar múltiples inicios de sesión simultáneos
        btn_login.setDisable(true);
        errorMsg.setVisible(false);

        Task<Void> loginTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    socket = new Socket(mainApp.getServerIp(), 2000);
                    mainApp.setSocket(socket);

                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    DataInputStream input = new DataInputStream(socket.getInputStream());

                    output.writeUTF("login");
                    output.writeUTF(login);
                    output.writeUTF(password);
                    output.flush();

                    String responseType = input.readUTF();
                    boolean authenticated = input.readBoolean();

                    Platform.runLater(() -> {
                        if ("LOGIN_RESPONSE".equals(responseType) && authenticated) {
                            mainApp.openMainView(socket);
                        } else {
                            showError("Usuario o contraseña incorrectos.");
                            closeSocket();
                        }
                        btn_login.setDisable(false);
                    });
                } catch (IOException e) {
                    Platform.runLater(() -> {
                        showError("Error al conectar con el servidor.");
                        btn_login.setDisable(false);
                    });
                }
                return null;
            }
        };
        new Thread(loginTask).start();
    }

    private void showError(String message) {
        errorMsg.setText(message);
        errorMsg.setVisible(true);
    }

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegisterButtonAction() {
        mainApp.openRegisterView();
    }
}
