package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

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
    private Socket socket;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
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
            errorMsg.setFill(Color.RED);
            errorMsg.setText("El usuario y la contraseña son obligatorios.");
            errorMsg.setVisible(true);
            return;
        }

        try {
            Socket socket = new Socket(mainApp.getServerIp(), 2000);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            output.writeUTF("register");
            output.writeUTF(username);
            output.writeUTF(password);
            output.writeUTF(email);
            output.flush();

            String responseType = input.readUTF();
            if ("REGISTER_RESPONSE".equals(responseType)) {
                boolean registered = input.readBoolean();

                if (registered) {
                    errorMsg.setFill(Color.GREEN);
                    errorMsg.setText("Usuario registrado con éxito.");
                    errorMsg.setVisible(true);
                } else {
                    errorMsg.setFill(Color.RED);
                    errorMsg.setText("Error al registrar el usuario. Inténtelo de nuevo.");
                    errorMsg.setVisible(true);
                }
            }
            socket.close();
        } catch (IOException e) {
            errorMsg.setFill(Color.RED);
            errorMsg.setText("Error al conectar con el servidor.");
            errorMsg.setVisible(true);
        }
    }

    private void handleGoLoginButtonAction() {
        mainApp.openLoginView();
    }
}
