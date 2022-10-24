package com.emcoders.ansplugin.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.util.List;


public class LayoutController extends VBox {


    // Paneles descripción, vídeo y señal
    @FXML
    private TitledPane panel_description;
    @FXML
    private TitledPane panel_video;
    @FXML
    private XYChart.Series<String, Double> series;

    //Configuraciones de tamaños
    private Integer min_width = 300;
    private Integer min_height = 200;

    public LayoutController() {

    }

    public void initialize_panes(SignalController signalController){
        initialize_pane_video("vídeo","");
        initialize_pane_signal("Señal Cardíaca", signalController);
        initialize_pane_description("Descripción");
    }
    /*
    Inicializando panel de descripción
    @param title:   Título del panel
    @return:
     */
    public void initialize_pane_description(String title){
        this.panel_description = new TitledPane();
        this.panel_description.setText(title);
        this.panel_description.setMinWidth(min_width);
        this.panel_description.setMinHeight(min_height);

        VBox content = new VBox();
        content.getChildren().add(new Label("Java Swing Tutorial"));
        content.getChildren().add(new Label("JavaFx Tutorial"));
        content.getChildren().add(new Label("Java IO Tutorial"));

        this.panel_description.setContent(content);
    }

    /*
    Inicializando panel de vídeo
    @param title:   Título del panel
    @return:
     */
    public void initialize_pane_video(String title, Object component){
        // Creando TitledPane.
        this.panel_video = new TitledPane();
        this.panel_video.setText(title);
        this.panel_video.setMinWidth(min_width);
        this.panel_video.setMinHeight(min_height);

        VBox content = new VBox();
        content.getChildren().add(new Label("Java Swing Tutorial"));
        content.getChildren().add(new Label("JavaFx Tutorial"));
        content.getChildren().add(new Label("Java IO Tutorial"));

        this.panel_video.setContent(content);
    }


    /*
    Inicializando panel de señal
    @param title:   Título del panel
    @return:
     */
    public void initialize_pane_signal(String title, SignalController signalController ){

        if(signalController != null){
            //Creando gráfico
            this.series = new XYChart.Series();

            //Modificando parámetros del gráfico
            this.series.setName("Frecuencia Cardíaca Instantánea (bpm)");

            //Obteniendo puntos de la señal y tiempos
            List<Double> points_signal = signalController.get_pointsSignal();
            List<Double> times = signalController.get_times();
            for(int i = 0; i < points_signal.size(); i++){
                Number x = times.get(i);
                Number y = points_signal.get(i);
                String new_format_number = format_number(x);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //update application thread
                        series.getData().add(new XYChart.Data(new_format_number,y));
                    }
                });

            }
            //this.signal_chart.getData().addAll(series);

        }
        else{
            //Creando gráfico
            XYChart.Series series = new XYChart.Series();
            series.getData().add(new XYChart.Data(3,1));
            series.getData().add(new XYChart.Data(5,2));
            series.getData().add(new XYChart.Data(6,3));

        }

    }

    public String format_number(Number number){
        DecimalFormat numeroFormateado = new DecimalFormat("#.0000");
        String textoFormateado = numeroFormateado.format(number);
        String[] list_number = textoFormateado.split(",");
        String new_number = list_number[0] + "." + list_number[1];
        return new_number;
    }
    public TitledPane getPanel_description() {
        return panel_description;
    }

    public TitledPane getPanel_video() {
        return panel_video;
    }

    public XYChart.Series getSeries() {
        return series;
    }
}
