package com.bonet.threaddungeons;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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
    private Accordion accordion1;
    @FXML
    private Accordion accordion2;
    @FXML
    private Label enemy1HpLabel;
    @FXML
    private Label enemy2HpLabel;
    @FXML
    private Label enemy3HpLabel;
    @FXML
    private Label enemy4HpLabel;
    @FXML
    private Label enemy5HpLabel;

    @FXML
    private void initialize() {
        accordion1.setExpandedPane(nivel1pane);
        accordion2.setExpandedPane(botin1pane);

        // Agregar listeners para el evento de expansión/cierre de los TitledPane
        nivel1pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin1pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy1HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin1pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy1HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel2pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin2pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy2HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin2pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy2HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel3pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin3pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy3HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin3pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy3HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel4pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin4pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy4HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin4pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy4HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });

        nivel5pane.expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                botin5pane.setExpanded(true);
                FadeTransition fadeInTransition = new FadeTransition(Duration.millis(500), enemy5HpLabel);
                fadeInTransition.setFromValue(0.0);
                fadeInTransition.setToValue(1.0);
                fadeInTransition.play();
            } else {
                botin5pane.setExpanded(false);
                FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(500), enemy5HpLabel);
                fadeOutTransition.setFromValue(1.0);
                fadeOutTransition.setToValue(0.0);
                fadeOutTransition.play();
            }
        });


    }

}