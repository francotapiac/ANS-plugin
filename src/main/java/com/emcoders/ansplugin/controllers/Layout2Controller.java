package com.emcoders.ansplugin.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;



public class Layout2Controller extends VBox {

    VBox root;
    // Paneles descripción, vídeo y señal
    @FXML
    private TitledPane panel_description;
    @FXML
    private TitledPane panel_video;
    @FXML
    private TitledPane panel_chart;

    // Señal panel
    @FXML
    private LineChart signal_chart;

    //Configuraciones de tamaños
    private Integer min_width = 1000;
    private Integer min_height = 200;

    public Layout2Controller() {

        initialize_pane_video("vídeo","");
        initialize_pane_signal("Señal Cardíaca");
        initialize_pane_description("Descripción");

        // Crando panel.
        root = new VBox();
        root.setPadding(new Insets(20, 10, 10, 10));
        root.getChildren().addAll(this.panel_description, this.panel_video, this.panel_chart);


    }

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



    public void initialize_pane_signal(String title ){

        //Inicializando gráfico
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.signal_chart =  new LineChart<Number,Number>(xAxis,yAxis);
        // Creando TitledPane
        this.panel_chart = new TitledPane();
        this.panel_chart.setText(title);
        this.panel_chart.setMinWidth(min_width);
        this.panel_chart.setMinHeight(min_height);

        //Creando gráfico
        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data(3,1));
        series.getData().add(new XYChart.Data(5,2));
        series.getData().add(new XYChart.Data(6,3));
        this.signal_chart.getData().addAll(series);

        //Agregando componente panel
        VBox content = new VBox();
        content.getChildren().add(this.signal_chart);

        this.panel_chart.setContent(content);
    }

    public VBox getRoot() {
        return root;
    }

}
