package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

public class LoginController {

    @FXML
    private Button btn_login, btn_register;

    @FXML
    private PasswordField inputPassword;

    @FXML
    private TextField inputUsuario;

    @FXML
    private Text errorMsg, gameTitle;

    @FXML
    private void initialize() {
        btn_login.setOnAction(this::handleLoginButtonAction);
        btn_register.setOnAction(this::handleRegisterButtonAction);
    }

    @FXML
    private void handleLoginButtonAction(javafx.event.ActionEvent event) {
        try (Socket socket = new Socket("localhost", 2000)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bonet/threaddungeons/Main-View.fxml"));
            Parent mainViewParent = loader.load();
            Scene mainViewScene = new Scene(mainViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(mainViewScene);
            window.show();
        } catch (ConnectException e) {
            errorMsg.setVisible(true);
            errorMsg.setText(e.getMessage());
        }catch (IOException e) {
            e.printStackTrace();
            errorMsg.setVisible(true);
            errorMsg.setText(e.getMessage());
        }
    }

    @FXML
    private void handleRegisterButtonAction(javafx.event.ActionEvent event) {
        System.out.println("Registro button pressed");
    }
}
