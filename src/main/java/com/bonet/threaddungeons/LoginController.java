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
import java.net.Socket;

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
        btn_login.setOnAction(event -> handleLoginButtonAction(event));
        btn_register.setOnAction(event -> handleRegisterButtonAction(event));
    }

    @FXML
    private void handleLoginButtonAction(javafx.event.ActionEvent event) {
        Socket socket = null;
        try {
            socket = new Socket("localhost", 2000);

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
            errorMsg.setVisible(true);
            errorMsg.setText("Error al conectar con el servidor");
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void handleRegisterButtonAction(javafx.event.ActionEvent event) {
        // Aquí puedes añadir la lógica para el registro
        System.out.println("Registro button pressed");
    }
}
