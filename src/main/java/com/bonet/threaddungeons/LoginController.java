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
    private static final String HOST = "localhost";
    private static final int Puerto = 2000;

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
        boolean authenticated = false;
        Socket socket = null;

        try {
            // Intentar conectar con el servidor
            socket = new Socket(HOST, Puerto);

            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            // Enviar credenciales de autenticación al servidor
            output.writeUTF(login);
            output.writeUTF(password);
            output.flush();

            authenticated = input.readBoolean();

            if (authenticated) {
                mainApp.openMainView(socket);
            } else {
                errorMsg.setVisible(true);
                errorMsg.setText("Autenticación fallida");
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            errorMsg.setVisible(true);
            errorMsg.setText("No se pudo conectar al servidor");
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
