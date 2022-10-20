package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.ANSController;
import javafx.fxml.FXML;

public class AlertController {
    ANSController ansController;

    @FXML
    public void cambioEscena(){

    }
    public ANSController getAnsController() {
        return ansController;
    }

    public void setAnsController(ANSController ansController) {
        this.ansController = ansController;
    }
}
