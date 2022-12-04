package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.ANSController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertControllerTest {

    @Test
    void cambioEscena() {
    }

    @Test
    void getAnsController() {
        ANSController ansController = new ANSController();
        AlertController alertController = new AlertController();
        alertController.setAnsController(ansController);
        assertEquals(ansController, alertController.getAnsController());
    }

    @Test
    void setAnsController() {
    }
}