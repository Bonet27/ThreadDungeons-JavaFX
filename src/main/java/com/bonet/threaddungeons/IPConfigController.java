package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IPConfigController {

    @FXML
    private TextField ipTextField; // Campo de texto para la IP

    private MainApp mainApp; // Referencia a la aplicación principal

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleOkButtonAction() {
        String serverIp = ipTextField.getText();
        if (serverIp != null && !serverIp.trim().isEmpty()) {
            mainApp.setServerIp(serverIp); // Establecer la IP del servidor en la aplicación principal
            Stage stage = (Stage) ipTextField.getScene().getWindow();
            stage.close(); // Cerrar la ventana de configuración de IP
        }
    }
}
