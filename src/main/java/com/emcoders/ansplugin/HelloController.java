package com.emcoders.ansplugin;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {

    public boolean processSignal(boolean process){
        return process;
    }
    public boolean loadingPlugin(boolean loading){
        return loading;
    }
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}