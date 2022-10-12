package com.emcoders.ansplugin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        /*try{
            String path =  "C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\pythonScripts\\test.txt";
            String path_model = "C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\pythonScripts\\svc.joblib";
            ProcessBuilder builder = new ProcessBuilder("python", "C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\pythonScripts\\signal_hrv.py", path ,"1", "1000",path_model);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readers = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String lines = null;
            while((lines = reader.readLine())!=null){
                System.out.println("respuesta:"+lines);
            }
            while((lines=readers.readLine())!=null){
                System.out.println("Elines:"+lines);
            }


        }
        catch (Exception e){
            e.printStackTrace();
        }*/
        MainController2 mainController = new MainController2();
        Scene scene = new Scene(mainController.getLayout(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}