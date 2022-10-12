package com.emcoders.ansplugin.components;

import com.emcoders.ansplugin.HelloApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Modality;

import java.io.IOException;


public class TagDialog extends Dialog{
    @FXML
    private ButtonType acceptButtonType;
    @FXML
    private ButtonType cancelButtonType;
    @FXML
    private TextField codeTextField;
    @FXML
    private TextArea descTextArea;

    public TagDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloApplication.class.getResource("tagDialogPane.fxml"));
            loader.setController(this);

            DialogPane dialogPane = loader.load();
            dialogPane.lookupButton(acceptButtonType).addEventFilter(ActionEvent.ANY, this::acceptChanges);
            dialogPane.lookupButton(cancelButtonType).addEventFilter(ActionEvent.ANY, this::cancelChanges);

            initModality(Modality.APPLICATION_MODAL);
            setResizable(true);
            setDialogPane(dialogPane);
            setOnShowing(dialogEvent -> Platform.runLater(() -> codeTextField.requestFocus()));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void acceptChanges(ActionEvent event) {
        System.out.println("acceptChanges");
        System.out.println(this.descTextArea.getText());
    }
    @FXML
    private void cancelChanges(ActionEvent event) {
        System.out.println("cancelChanges");
    }

    public boolean isCompleted() {
        String codeVal = this.codeTextField.getText();
        String descVal = this.descTextArea.getText();
        return !codeVal.isEmpty() && !descVal.isEmpty();
    }

    public void setCodeText(String text) { this.codeTextField.setText(text); }

    public void setDescText(String text) { this.descTextArea.setText(text); }

    public String getCodeText() { return this.codeTextField.getText(); }

    public String getDescText() { return this.descTextArea.getText(); }
}
