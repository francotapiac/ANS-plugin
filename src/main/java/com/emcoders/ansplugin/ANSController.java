package com.emcoders.ansplugin;

import com.emcoders.ansplugin.controllers.LayoutController;
import com.emcoders.ansplugin.controllers.SignalController;
import com.emcoders.scansembox.Models.CanalModel;
import com.emcoders.scansembox.Utils.TimelineElement;
import com.emcoders.scansembox.Utils.TimelineTag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;


public class ANSController extends CanalModel {

    // FXML Elements
    @FXML
    Pane root;
    @FXML
    private LineChart<String, Double> chart;

    //Label panel información
    @FXML
    private Label alert;
    @FXML
    private Label cardiac_coherence;
    @FXML
    private Label emotion;

    // Atributos
    SignalController signalController;
    private final ArrayList<TimelineTag> tags = new ArrayList<>();
    private ExecutorService backgroundWorkers;


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

        //Cargando layout
        setName("ANS Plug-in");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("page1.fxml"));
        fxmlLoader.setController(this);
        try {
            this.root = fxmlLoader.load();
        } catch(IOException e) {
            e.printStackTrace();
        }
        initPlugin();


        return this.root;
    }


    // Inicializa componentes
    public void initPlugin(){
        //*********************** Gráfico ***********************
        // Instanciando controlador de señal
        signalController = new SignalController("http://127.0.0.1:5000/heart");

        //Seteando layout principal (componente señal de VBox) con la señal leída
        LayoutController layoutController = new LayoutController();
        layoutController.initialize_panes(signalController);

        //Inicializando gráfico
        this.chart.getData().addAll(layoutController.getSeries());

        //Modificando atributos de gráfico
        this.chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");

        //*********************** Análisis de señal ***********************
        this.alert.setText(signalController.getSignal().getCount_alerts().toString() + " Alertas");
        this.emotion.setText(signalController.getSignal().getPredominant_emotion().toString() + " Alertas");
        System.out.println(this.alert.getText());










    }

    public void show_panels(){


    }
    public ArrayList<TimelineTag> getTags() {
        return this.tags;
    }

    public void play() {
    }

    public void stop() {
    }

    public void pause() {
    }

    public void seek(double millis) {
    }

    // Comprueba si puede abrir archivo con señal
    public boolean canOpen(String path) {
        if(path != "" && path != null){
            File file = new File(path);
            if(file.exists())
                return true;
        }
        return false;
    }

   // Abre el archivo
    public TimelineElement openSource(String path) {

        //*********************** Gráfico ***********************
        // Instanciando controlador de señal
        signalController = new SignalController("http://127.0.0.1:5000/heart");

        //Seteando layout principal (componente señal de VBox) con la señal leída
        LayoutController layoutController = new LayoutController();
        layoutController.initialize_panes(signalController);

        //Inicializando gráfico
        this.chart.getData().addAll(layoutController.getSeries());

        //Modificando atributos de gráfico
        this.chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");

        //*********************** Análisis de señal ***********************
        this.alert.setText(signalController.getSignal().getCount_alerts().toString() + " Alertas");
        this.emotion.setText(signalController.getSignal().getPredominant_emotion().toString() + " Alertas");
        System.out.println(this.alert.getText());

        //Párametros de la línea de tiempo
        Double sourceLength = signalController.get_sourceLength();

        TimelineElement timelineElement = new TimelineElement(path,sourceLength,"Psicofisiológico", Color.valueOf("81b622"));
        return timelineElement;
    }

    public TimelineElement loadProjectSource(String path) {
        return null;
    }

    public ExecutorService getBackgroundWorkers() {
        return backgroundWorkers;
    }

    public void setBackgroundWorkers(ExecutorService backgroundWorkers) {
        this.backgroundWorkers = backgroundWorkers;
    }

    public void loadProjectTag(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
    }

    public void requestUpdate(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
    }

    public void deleteTag(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
    }

    public void shutdown() {
    }

}
