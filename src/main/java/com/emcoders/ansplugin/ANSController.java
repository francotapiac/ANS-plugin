package com.emcoders.ansplugin;

import com.emcoders.ansplugin.controllers.AlertController;
import com.emcoders.ansplugin.controllers.LayoutController;
import com.emcoders.ansplugin.controllers.SignalController;
import com.emcoders.ansplugin.controllers.VideoController;
import com.emcoders.ansplugin.models.SegmentSignal;
import com.emcoders.ansplugin.models.Signal;
import com.emcoders.scansembox.Models.CanalModel;
import com.emcoders.scansembox.Utils.TimelineElement;
import com.emcoders.scansembox.Utils.TimelineTag;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.jfoenix.controls.JFXButton;
import javafx.util.Duration;


public class ANSController extends CanalModel {
    @FXML
    private Label prueba;

    // FXML Elements
    @FXML
    Pane root;
    @FXML
    private AnchorPane pane_principal;
    @FXML
    private VBox resources_vbox;
    @FXML
    private LineChart<String, Double> chart;
    @FXML
    private MediaView media;

    //Label panel información
    @FXML
    private Label alert;
    @FXML
    private Label cardiac_coherence;
    @FXML
    private Label emotion;
    @FXML
    private Label initial_time;
    @FXML
    private Label final_time;

    //Label emoción
    @FXML
    private ImageView image_emotion;
    @FXML
    private Label emotion_abstract;

    // Panel de Tabla Detalles
    @FXML
    private AnchorPane detail_panel;
    @FXML
    private TableView<SegmentSignal> table_detail;
    @FXML
    private TableColumn<SegmentSignal, String > segment_detail;
    @FXML
    private TableColumn<SegmentSignal, Float> initial_time_detail;
    @FXML
    private TableColumn<SegmentSignal, Float> final_time_detail;
    @FXML
    private TableColumn<SegmentSignal, String> alert_detail;
    @FXML
    private TableColumn<SegmentSignal, String> cardiac_detail;
    @FXML
    private TableColumn<SegmentSignal, String> emotion_detail;
    //Botones
    @FXML
    private JFXButton alert_btn;
    @FXML
    private JFXButton general_btn;
    @FXML
    private JFXButton btn_chart_fci;

    @FXML
    private JFXButton btn_chart_rr;

    @FXML
    private JFXButton btn_study_segment;

    @FXML
    private JFXButton btn_study_signal;



    // Atributos
    private final ArrayList<TimelineTag> tags = new ArrayList<>();
    private ExecutorService backgroundWorkers;

    private String path_video = "";

    // Controladores
    SignalController signalController;
    LayoutController layoutController;
    VideoController videoController;

    Double millis;
    TimelineElement timelineElement;


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


        //Moviendo todos los paneles en su posición real
        this.detail_panel.setLayoutX(this.pane_principal.getLayoutX());
        this.detail_panel.setLayoutY(this.pane_principal.getLayoutY());

        this.pane_principal.toFront();

        this.millis = 0.0;

        //seek(13.6);

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
        if(this.signalController != null){
            this.millis = millis;
        }

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
        new Thread(() -> {
            signalController = new SignalController("http://127.0.0.1:5000/heart");

            //DO SOMETHING WITH CONTROLLS ON FX THREAD ACCORDING RESULT OF OVER
            Platform.runLater(() -> {
                this.timelineElement = openSourceThread(path);
            });
        }).start();


        return this.timelineElement;


    }

    public TimelineElement openSourceThread(String path){
        //*********************** Gráfico ***********************
        // Instanciando controlador de señal


        //Seteando layout principal (componente señal de VBox) con la señal leída
        layoutController = new LayoutController();
        layoutController.initialize_panes(signalController);

        //Inicializando gráfico
        this.chart.getData().addAll(layoutController.getSeries());

        //Modificando atributos de gráfico
        this.chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        this.chart.setTitle("Frecuencia Cardíaca Instantánea en el Tiempo");
        this.chart.getXAxis().setLabel("Tiempo (s)");
        this.chart.getYAxis().setLabel("Frecuencia Cardíaca Instantánea");


        //*********************** Emoción de señal ***********************
        this.emotion_abstract.setText(signalController.getSignal().getPredominant_emotion().toString());
        try{
            this.image_emotion.setImage(new Image(new FileInputStream("C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\src\\main\\resources\\com\\emcoders\\ansplugin\\images\\sorpresa.png")));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //*********************** Análisis de señal ***********************
        this.alert.setText("N° Alertas: " + signalController.getSignal().getCount_alerts().toString());
        this.emotion.setText("Emoción: " + signalController.getSignal().getPredominant_emotion());
        this.initial_time.setText("Tiempo inicial: " + format_number(signalController.getSignal().getStart_time_signal()) + " s");
        this.final_time.setText("Tiempo final: " + format_number(signalController.getSignal().getEnd_time_signal()) + " s");
        System.out.println(this.alert.getText());

        //*********************** Vídeo ***********************
        //this.path_video = "C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\audio.wav";
        if(path_video !=""){
            videoController = new VideoController();
            MediaPlayer media = videoController.initialize_video(path_video);
            this.media.setMediaPlayer(media);
        }
        //*********************** Tabla de detalles ***********************
        create_table_detail();


        //Párametros de la línea de tiempo
        Double sourceLength = signalController.get_sourceLength();

         this.timelineElement = new TimelineElement(path,sourceLength,"Psicofisiológico", Color.valueOf("81b622"));
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


    @FXML
    void handleButtonAlert(ActionEvent event) {
        this.general_btn.setStyle("-fx-background-color: White");
        this.alert_btn.setStyle("-fx-background-color: #d8d7f6");
        //this.alert_btn.setRipplerFill(Color.valueOf("#d8d7f6"));
        this.detail_panel.setLayoutX(this.pane_principal.getLayoutX());
        this.detail_panel.setLayoutY(this.pane_principal.getLayoutY());
        this.detail_panel.toFront();

    }

    @FXML
    void handleButtonGeneral(ActionEvent event) {
        this.general_btn.setStyle("-fx-background-color: #d8d7f6");
        this.alert_btn.setStyle("-fx-background-color: White");
        this.pane_principal.setLayoutX(this.pane_principal.getLayoutX());
        this.pane_principal.setLayoutY(this.pane_principal.getLayoutY());
        this.pane_principal.toFront();
    }

    @FXML
    void handleButtonResource(ActionEvent event) {
        this.openSource("");

    }


    @FXML
    void handleButtonSegment(ActionEvent event) {
        this.btn_study_segment.setStyle("-fx-background-color: #F0F0F6");
        this.btn_study_segment.setButtonType(JFXButton.ButtonType.FLAT);
        this.btn_study_signal.setStyle("-fx-background-color:  #d8d7f6");
        this.btn_study_signal.setButtonType(JFXButton.ButtonType.RAISED);
        set_data_abstact(millis);

    }

    @FXML
    void handleButtonSignal(ActionEvent event) {
        this.btn_study_signal.setStyle("-fx-background-color: #F0F0F6");
        this.btn_study_signal.setButtonType(JFXButton.ButtonType.FLAT);
        this.btn_study_segment.setStyle("-fx-background-color:  #d8d7f6");
        this.btn_study_segment.setButtonType(JFXButton.ButtonType.RAISED);
        set_data_abstact(millis);

    }
    @FXML
    void handleButtonFCI(ActionEvent event) {
        this.btn_chart_fci.setStyle("-fx-background-color: #F0F0F6");
        this.btn_chart_fci.setButtonType(JFXButton.ButtonType.FLAT);
        this.btn_chart_rr.setStyle("-fx-background-color:  #d8d7f6");
        this.btn_chart_rr.setButtonType(JFXButton.ButtonType.RAISED);
        //set_data_abstact(millis);
    }

    @FXML
    void handleButtonRR(ActionEvent event) {
        this.btn_chart_rr.setStyle("-fx-background-color: #F0F0F6");
        this.btn_chart_rr.setButtonType(JFXButton.ButtonType.FLAT);
        this.btn_chart_fci.setStyle("-fx-background-color:  #d8d7f6");
        this.btn_chart_fci.setButtonType(JFXButton.ButtonType.RAISED);
        //set_data_abstact(millis);
    }
    // Crear tabla con detalles de cada alerta
    @FXML
    void create_table_detail(){
        this.segment_detail.setCellValueFactory(new PropertyValueFactory<>("id"));
        this.initial_time_detail.setCellValueFactory(new PropertyValueFactory<>("initial_time"));
        this.final_time_detail.setCellValueFactory(new PropertyValueFactory<>("final_time"));
        this.emotion_detail.setCellValueFactory(new PropertyValueFactory<>("emotion"));
        this.cardiac_detail.setCellValueFactory(new PropertyValueFactory<>("cardiac_coherence"));
        this.alert_detail.setCellValueFactory(new PropertyValueFactory<>("alert"));
        ObservableList<SegmentSignal> list = FXCollections.observableArrayList();
        for (Integer i = 0; i < signalController.getSignal().getTime_line().size(); i ++){
            Float initial_time = signalController.getSignal().getTime_line().get(i).getKey().getStart_time();
            Float final_time = signalController.getSignal().getTime_line().get(i).getKey().getEnd_time();
            String emotion = signalController.getSignal().getTime_line().get(i).getKey().getEmotion().getName();
            String cardiac_coherence = signalController.getSignal().getTime_line().get(i).getKey().getDescription();
            String alert = signalController.getSignal().getTime_line().get(i).getKey().getText_alert();
            SegmentSignal segmentSignal = new SegmentSignal(i.toString(),initial_time,final_time,emotion,cardiac_coherence,alert);
            list.add(segmentSignal);
            System.out.println(segmentSignal.getEmotion());
        }
        this.table_detail.setItems(list);
    }

    public void set_data_abstact(double millis){
        this.signalController.get_particular_data(millis);
        Platform.runLater( () -> {

            this.alert.setText("Coherencia Cardíaca: " + this.signalController.getCardiac_coherence_description());
            this.emotion.setText("Emoción: " + this.signalController.getEmotion());
            this.cardiac_coherence.setText("Valor cardíaco: " + this.signalController.getRatio_coherence().toString());
            this.initial_time_detail.setText("Tiempo inicial: " + this.signalController.getPartial_start_time().toString());
            this.final_time.setText("Tiempo final: " + this.signalController.getPartial_end_time().toString());
        });
    }






    // Entrega un número con formato .0000
    public Float format_number(Double number){
        DecimalFormat numeroFormateado = new DecimalFormat("#.0000");
        String textoFormateado = numeroFormateado.format(number);
        String[] list_number = textoFormateado.split(",");
        String new_number = list_number[0] + "." + list_number[1];
        return Float.parseFloat(new_number);
    }

}
