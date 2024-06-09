package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class IPConfigController {

    @FXML
    private TextField ipTextField;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleOkButtonAction() {
        String serverIp = ipTextField.getText();
        if (serverIp != null && !serverIp.trim().isEmpty()) {
            mainApp.setServerIp(serverIp);
            Stage stage = (Stage) ipTextField.getScene().getWindow();
            stage.close();
        }
    }
}
