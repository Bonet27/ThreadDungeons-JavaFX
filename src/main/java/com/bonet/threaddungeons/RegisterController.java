package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class RegisterController {
    @FXML
    private Button btn_createAcc; // Botón para crear cuenta
    @FXML
    private Button btn_back; // Botón para volver
    @FXML
    private Label errorMsg; // Etiqueta para mensajes de error
    @FXML
    private TextField inputEmail; // Campo de texto para el email
    @FXML
    private PasswordField inputPassword; // Campo de texto para la contraseña
    @FXML
    private TextField inputUsuario; // Campo de texto para el nombre de usuario
    private MainApp mainApp; // Referencia a la aplicación principal
    private Socket socket; // Socket de conexión al servidor

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
            errorMsg.setText("El usuario y la contraseña son obligatorios.");
            errorMsg.setVisible(true);
            return;
        }

        try {
            socket = new Socket(mainApp.getServerIp(), 2000); // Conectar al servidor
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            DataInputStream input = new DataInputStream(socket.getInputStream());

            // Enviar datos de registro al servidor
            output.writeUTF("register");
            output.writeUTF(username);
            output.writeUTF(password);
            output.writeUTF(email.isEmpty() ? "" : email); // Enviar un string vacío si no hay email

            output.flush();

            // Leer respuesta del servidor
            String responseType = input.readUTF();
            if ("REGISTER_RESPONSE".equals(responseType)) {
                boolean registered = input.readBoolean();

                if (registered) {
                    errorMsg.setText("Usuario registrado con éxito.");
                    errorMsg.setTextFill(Color.GREEN);
                } else {
                    errorMsg.setText("Error al registrar el usuario. Inténtelo de nuevo.");
                    errorMsg.setTextFill(Color.RED);
                }
                errorMsg.setVisible(true);
            }
            socket.close(); // Cerrar el socket
        } catch (IOException e) {
            errorMsg.setText("Error al conectar con el servidor.");
            errorMsg.setTextFill(Color.RED);
            errorMsg.setVisible(true);
        }
    }

    private void handleGoLoginButtonAction() {
        mainApp.openLoginView(); // Abrir la vista de login
    }
}
