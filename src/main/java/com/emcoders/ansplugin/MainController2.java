package com.emcoders.ansplugin;

import com.emcoders.ansplugin.controllers.Layout2Controller;
import com.emcoders.scansembox.Models.CanalModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController2 extends CanalModel {

    // FXML Elements
    @FXML
    Pane root;
    @FXML
    VBox central_box;


    // Instance state //
    private volatile boolean isLoaded = false;

    @Override
    public Pane getLayout() {
        /*
          Initial method. Which load the layout and perform the plugin init
          @return Pane
         */
        if (this.isLoaded) return null;
        this.isLoaded = true;

        setName("ANS Plug-in");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("layout2.fxml"));
        fxmlLoader.setController(this);
        try {
            this.root = fxmlLoader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
        initPlugin();


        return this.root;
    }

    public void initPlugin(){
        //SignalController signal = new SignalController("http://127.0.0.1:5000");
        Layout2Controller layout2Controller = new Layout2Controller();
        this.root.getChildren().set(1, layout2Controller.getRoot());



    }

    public void show_panels(){

    }


}
