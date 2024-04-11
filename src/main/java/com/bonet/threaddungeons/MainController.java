package com.bonet.threaddungeons;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class MainController {
    @FXML
    private Button btn_attack;

    @FXML
    private Button btn_skip;

    @FXML
    private TitledPane nivel1pane;

    @FXML
    private TitledPane nivel2pane;

    @FXML
    private TitledPane nivel3pane;

    @FXML
    private TitledPane nivel4pane;

    @FXML
    private TitledPane nivel5pane;

    @FXML
    private TitledPane tp_money;
    @FXML
    private TitledPane botin1pane;
    @FXML
    private TitledPane botin2pane;
    @FXML
    private TitledPane botin3pane;
    @FXML
    private TitledPane botin4pane;
    @FXML
    private TitledPane botin5pane;

    @FXML
    private void initialize() {

        nivel1pane.setExpanded(true);
        botin1pane.setExpanded(true);

        // Agregar listeners para el evento de expansión/cierre de los TitledPane
        nivel1pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Si nivel1pane se expande, expandir también botin1pane
                botin1pane.setExpanded(true);
            } else {
                // Si nivel1pane se cierra, cerrar también botin1pane
                botin1pane.setExpanded(false);
            }
        });

        nivel2pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Si nivel1pane se expande, expandir también botin1pane
                botin2pane.setExpanded(true);
            } else {
                // Si nivel1pane se cierra, cerrar también botin1pane
                botin2pane.setExpanded(false);
            }
        });

        nivel3pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Si nivel1pane se expande, expandir también botin1pane
                botin3pane.setExpanded(true);
            } else {
                // Si nivel1pane se cierra, cerrar también botin1pane
                botin3pane.setExpanded(false);
            }
        });

        nivel4pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Si nivel1pane se expande, expandir también botin1pane
                botin4pane.setExpanded(true);
            } else {
                // Si nivel1pane se cierra, cerrar también botin1pane
                botin4pane.setExpanded(false);
            }
        });

        nivel5pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Si nivel1pane se expande, expandir también botin1pane
                botin5pane.setExpanded(true);
            } else {
                // Si nivel1pane se cierra, cerrar también botin1pane
                botin5pane.setExpanded(false);
            }
        });


    }

}