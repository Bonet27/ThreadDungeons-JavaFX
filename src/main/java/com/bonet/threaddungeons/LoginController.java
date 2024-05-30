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
    private Text errorMsg;
    private MainApp mainApp;
    private Socket socket;

    public void setMainApp(MainApp mainApp) { this.mainApp = mainApp; }

    @FXML
    private void initialize() {
        btn_login.setOnAction(event -> handleLoginButtonAction());
    }

    private void handleLoginButtonAction() {
        String login = inputUsuario.getText();
        String password = inputPassword.getText();

        try {
            socket = new Socket("localhost", 2000); // Cambia "localhost" y el puerto según sea necesario
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            // Enviar credenciales de autenticación al servidor
            output.writeUTF(login);
            output.writeUTF(password);
            output.flush();

            boolean authenticated = input.readBoolean();

            if (authenticated) {
                mainApp.openMainView(socket);
            } else {
                errorMsg.setVisible(true);
                errorMsg.setText("Autenticación fallida");
                socket.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            errorMsg.setVisible(true);
            errorMsg.setText("Error al conectar con el servidor");
        }
    }
}
