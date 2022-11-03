package com.emcoders.ansplugin;

import com.emcoders.ansplugin.components.TagDialog;
import com.emcoders.ansplugin.controllers.*;
import com.emcoders.ansplugin.models.SegmentSignal;
import com.emcoders.ansplugin.models.Signal;
import com.emcoders.scansembox.Events.AddSourceEvent;
import com.emcoders.scansembox.Events.AddTagEvent;
import com.emcoders.scansembox.Events.UpdateTagEvent;
import com.emcoders.scansembox.Models.CanalModel;
import com.emcoders.scansembox.Utils.TimelineElement;
import com.emcoders.scansembox.Utils.TimelineTag;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import io.github.palexdev.materialfx.enums.ScrimPriority;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.jfoenix.controls.JFXButton;
import javafx.util.Duration;
import javafx.util.Pair;


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
    private LineChart<Double, Double> chart;
    @FXML
    private NumberAxis xAxis;

    @FXML
    private NumberAxis yAxis;
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
    private JFXButton btn_study_segment;

    @FXML
    private JFXButton btn_study_signal;
    @FXML
    private JFXButton btn_create_event;
    @FXML
    TagDialog tagDialog;

    Dialog<String> dialog_charge;

    @FXML
    private RadioButton radio_general;

    @FXML
    private RadioButton radio_segmento;

    @FXML
    private RadioButton radio_tiempo;

    private Double lower_x_axis;
    private Double upper_x_axis;
    private int type_view_chart;
    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;


    // Atributos
    private final ArrayList<TimelineTag> tags = new ArrayList<>();
    private ExecutorService backgroundWorkers;

    private String path_video = "";
    private String path_signal = "";

    // Controladores
    SignalController signalController;
    LayoutController layoutController;
    VideoController videoController;

    DetailsController detailsController;

    Float millis;
    TimelineElement timelineElement;
    String alert_task;
    String emotion_task;
    String cardiac_coherence_task;
    String initial_time_task;
    String final_time_task;
    String source_name;
    InputStream path_image_emotion;
    Boolean activate_btn_file_chart = true;

    private ArrayList<BooleanProperty> tagsSelected = new ArrayList<>();


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
        this.tagDialog = new TagDialog();
        this.detailsController = new DetailsController();
        this.radio_general.setSelected(true);

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
        this.millis = format_number(millis);
        signalController.get_particular_data(format_number(millis));
        alert_task = signalController.getCardiac_coherence_description();
        emotion_task = signalController.getEmotion();
        cardiac_coherence_task = signalController.getRatio_coherence().toString();
        initial_time_task = signalController.getPartial_start_time().toString();
        final_time_task = signalController.getPartial_end_time().toString();
        path_image_emotion =  ANSController.class.getResourceAsStream(signalController.getPath_image_emotion());
        select_type_view_chart(type_view_chart);
        Platform.runLater(() -> {
            //update application thread
            xAxis.setLowerBound(lower_x_axis);
            xAxis.setUpperBound(upper_x_axis);
            xAxis.setAutoRanging(false);
            set_data_abstact(millis);
        });



        //System.out.println(millis);
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


    public TimelineElement openSource(String path) {

        //Ruta completa
        String route_api = "http://127.0.0.1:5000/heart/" + path + "/" + "1000";

        // Instanciando controlador de señal
        signalController = new SignalController(route_api);
        this.path_signal = path;
        this.source_name = Paths.get(path).getFileName().toString();
        Platform.runLater(() -> {
            //update application thread
            openSourceThread(path);
            addEventTag();
        });

        //Párametros de la línea de tiempo
        Double sourceLength = signalController.get_sourceLength();
        timelineElement = new TimelineElement(path,sourceLength,"Psicofisiológico", Color.valueOf("81b622"));
        this.root.fireEvent(
                new AddSourceEvent(this.getProjectDir() + "\\" + this.source_name, this.getName())
        );

        return this.timelineElement;


    }

    public void openSourceThread(String path){


        //Seteando layout principal (componente señal de VBox) con la señal leída
        layoutController = new LayoutController();
        layoutController.initialize_panes(signalController);

        //*********************** Emoción de señal ***********************
        this.emotion_abstract.setText(signalController.getSignal().getPredominant_emotion().toString());
        try {
            InputStream url = ANSController.class.getResourceAsStream(signalController.getSignal().getEmotion_image_path());
            this.image_emotion.setImage(new Image(url));
            this.image_emotion.setFitHeight(181);
            this.image_emotion.setFitWidth(150);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //*********************** Análisis de señal ***********************
        this.alert.setText("N° Alertas: " + signalController.getSignal().getCount_alerts().toString());
        this.emotion.setText("Emoción: " + signalController.getSignal().getPredominant_emotion());
        this.initial_time.setText("Tiempo inicial: " + format_number(signalController.getSignal().getStart_time_signal())/1000 + " s");
        this.final_time.setText("Tiempo final: " + format_number(signalController.getSignal().getEnd_time_signal()) /1000+ " s");

        //*********************** Vídeo ***********************
        //this.path_video = "C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\audio.wav";
        if(path_video !=""){
            videoController = new VideoController();
            MediaPlayer media = videoController.initialize_video(path_video);
            this.media.setMediaPlayer(media);
        }

        //*********************** Gráfico ***********************
        //Modificando atributos de gráfico
        this.chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        this.chart.setTitle("Frecuencia Cardíaca Instantánea en el Tiempo");
        this.chart.getXAxis().setLabel("Tiempo (s)");
        this.chart.getYAxis().setLabel("Frecuencia Cardíaca (Hz)");
        this.chart.setCreateSymbols(false);

        //Inicializando gráfico
        //this.chart.getData().addAll(layoutController.getSeries_fci(), layoutController.getSeries_points());
        this.chart.getData().addAll(layoutController.getSeries_fci());
        yAxis.setLowerBound(78);
        yAxis.setUpperBound(82);
        xAxis.setLowerBound(signalController.getSignal().getStart_time_signal());
        xAxis.setUpperBound(signalController.getSignal().getEnd_time_signal());

        this.type_view_chart = 1;
        this.chart.getYAxis().setAutoRanging(false);
        this.chart.setAnimated(false);

        //*********************** Tabla de detalles ***********************
        create_table_detail();
        detailsController.create_report_excel(this.table_detail, this.signalController.getSignal().getFci(), this.signalController.getSignal().getTimes_fci(), this.getProjectDir(), this.source_name);

    }

    public TimelineElement loadProjectSource(String path) {
        detailsController.read_xls(path + ".xls",true);
        String route_api = "http://127.0.0.1:5000/heart/" + path + "/" + "1000";
        // Instanciando controlador de señal
        signalController = new SignalController(route_api);
        this.path_signal = path;
        this.source_name = Paths.get(path).getFileName().toString();
        Platform.runLater(() -> {
            //update application thread
            openSourceThread(path);
            //addEventTag();
        });

        //Párametros de la línea de tiempo
        Double sourceLength = signalController.get_sourceLength();
        timelineElement = new TimelineElement(path,sourceLength,"Psicofisiológico", Color.valueOf("81b622"));

        return this.timelineElement;
    }

    public void loadProjectTag(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
        Color color;
        if(Objects.equals(code.split("")[0], "Evento")){
            color = Color.RED;
        }else{
            color = Color.TEAL;
        }
        color = Color.RED;
        TimelineTag timelineTag = new TimelineTag(this.timelineElement.getWidth() * initTimeInMS / (signalController.getSignal().getEnd_time_signal()*1000),
                this.timelineElement.getWidth() * lengthInMS / (signalController.getSignal().getEnd_time_signal()*1000),
                code,
                desc,
                initTimeInMS,
                lengthInMS,
                color,
                this.getName(),
                source);
        this.timelineElement.getChildren().add(timelineTag);
        this.getTags().add(timelineTag);
        this.tagsSelected.add(timelineTag.selectedProperty());
        this.tagsSelected.get(this.tagsSelected.size() - 1).addListener(
                (observable, oldValue, newValue) -> {
                   // processingBtn.setDisable(newValue);
                    //exportDetailsBtn.setDisable(true);
                    //checkTagBtn.setDisable(!newValue);
                    //addTagBtn.setDisable(newValue);
                }
        );
    }

    public void requestUpdate(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
     /*
          Method to update an existing tag on the timeline
          @param string source path
         * @param string tag code
         * @param string description
         * @param double start millisecond
         * @param double millisecond length
         * @return void
         */
        TimelineTag tagToUpdate = null;
        for (TimelineTag t : this.getTags()) {
            if(
                    Objects.equals(source, t.getSource()) &&
                            initTimeInMS == t.getInitTimeInMS() &&
                            lengthInMS == t.getLengthInMS()
            ){
                tagToUpdate = t;
                break;
            }
        }

        if (tagToUpdate == null) return;
        this.tagDialog.setCodeText(tagToUpdate.getCode());
        this.tagDialog.setDescText(tagToUpdate.getDescription());
        this.tagDialog.showAndWait();

        if (!this.tagDialog.isCompleted()) return;

        TimelineTag finalTagToUpdate = tagToUpdate;
        Platform.runLater(() -> {
            new UpdateTagEvent(
                    this.getName(),
                    source,
                    code,
                    desc,
                    initTimeInMS,
                    lengthInMS,
                    this.tagDialog.getCodeText(),
                    this.tagDialog.getDescText(),
                    finalTagToUpdate.getInitTimeInMS(),
                    finalTagToUpdate.getLengthInMS()
            );
            finalTagToUpdate.setDescription(this.tagDialog.getDescText());
            finalTagToUpdate.setCode(this.tagDialog.getCodeText());
        });

    }

    public void deleteTag(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
        TimelineTag tagToDelete = null;
        for(TimelineTag t : this.getTags()){
            if(
                    Objects.equals(source, t.getSource()) &&
                            initTimeInMS == t.getInitTimeInMS() &&
                            lengthInMS == t.getLengthInMS()
            ){
                tagToDelete = t;
                break;
            }
        }
        if(tagToDelete == null) return;
        this.getTags().remove(tagToDelete);
        this.timelineElement.getChildren().remove(tagToDelete);
        // Remove timelineTag selected property
        this.tagsSelected.remove(this.tagsSelected.indexOf(tagToDelete.selectedProperty()));
    }

    @Override
    public void showTagDetail(String source, String code, double initTimeInMS, double lengthInMS) {
        /*
          Method to check a timeline's tag detail
          @param string source path
         * @param string tag code
         * @param double start millisecond
         * @param double millisecond length
         * @return void
         */
        TimelineTag tagToUpdate = null;
        for (TimelineTag t : this.getTags()) {
            if(
                    Objects.equals(source, t.getSource()) &&
                            initTimeInMS == t.getInitTimeInMS() &&
                            lengthInMS == t.getLengthInMS()
            ){
                tagToUpdate = t;
                break;
            }
        }

        if (tagToUpdate == null) return;
        this.tagDialog.setCodeText(tagToUpdate.getCode());
        this.tagDialog.setDescText(tagToUpdate.getDescription());
        this.tagDialog.showAndWait();

        if (!this.tagDialog.isCompleted()) return;


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
        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        openSource("");
                        final CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(() -> {
                            Double p = (Double)timelineElement.getSourceLength();
                            emotion.setText(p.toString());
                        });

                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }

        };
        service.start();
    }


    @FXML
    void handleButtonSegment(ActionEvent event) {
        //seek(12.53918);
        this.btn_study_segment.setStyle("-fx-background-color: #F0F0F6");
        this.btn_study_segment.setButtonType(JFXButton.ButtonType.FLAT);
        this.btn_study_signal.setStyle("-fx-background-color:  #d8d7f6");
        this.btn_study_signal.setButtonType(JFXButton.ButtonType.RAISED);



        Service<Void> service = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        signalController.get_particular_data(millis);
                        alert_task = signalController.getCardiac_coherence_description();
                        emotion_task = signalController.getEmotion();
                        cardiac_coherence_task = signalController.getRatio_coherence().toString();
                        initial_time_task = signalController.getPartial_start_time().toString();
                        final_time_task = signalController.getPartial_end_time().toString();
                        final CountDownLatch latch = new CountDownLatch(1);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                set_data_abstact(millis);
                            }
                        });

                        latch.await();
                        //Keep with the background work
                        return null;
                    }
                };
            }

        };
        service.start();



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
    void set_type_signal_chart(ActionEvent event) {
        if(this.radio_general.isSelected()){
            type_view_chart = 1;
        }
        else if(this.radio_segmento.isSelected()){
            type_view_chart = 2;
        }
        else if(this.radio_tiempo.isSelected()){
            type_view_chart = 3;
        }
    }

    void select_type_view_chart(int type_view){
        if(type_view == 1){
            this.lower_x_axis = signalController.getSignal().getStart_time_signal();
            this.upper_x_axis = this.signalController.getSignal().getEnd_time_signal();
        }
        else if(type_view == 2){
            signalController.get_particular_data(this.millis);
            this.lower_x_axis = Double.parseDouble(signalController.getPartial_start_time().toString())/1000;
            this.upper_x_axis = Double.parseDouble(signalController.getPartial_end_time().toString())/1000;
        }
        else if(type_view == 3){
            this.lower_x_axis = signalController.getSignal().getStart_time_signal();
            this.upper_x_axis = Double.parseDouble(this.millis.toString())/1000;
        }

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

        TableColumn vlf = new TableColumn("Muy Baja Frecuencia (VLF)");
        TableColumn lf = new TableColumn("Baja Frecuencia (LF)");
        TableColumn hf = new TableColumn("Alta Frecuencia (HF)");
        TableColumn lf_hf = new TableColumn("LF/HF");
        TableColumn fft_total = new TableColumn("FFT Total");
        TableColumn hr_mean = new TableColumn("hr_mean");
        TableColumn hr_min = new TableColumn("hr_min");
        TableColumn hr_max = new TableColumn("hr_max");
        TableColumn sdnn = new TableColumn("sdnn");
        TableColumn rmssd = new TableColumn("rmssd");
        TableColumn sdsd = new TableColumn("sdsd");
        //TableColumn pnn50 = new TableColumn("pnn50");

        vlf.setCellValueFactory(new PropertyValueFactory<>("vlf"));
        lf.setCellValueFactory(new PropertyValueFactory<>("lf"));
        hf.setCellValueFactory(new PropertyValueFactory<>("hf"));
        lf_hf.setCellValueFactory(new PropertyValueFactory<>("lf_hf"));
        fft_total.setCellValueFactory(new PropertyValueFactory<>("fft_total"));
        hr_mean.setCellValueFactory(new PropertyValueFactory<>("hr_mean"));
        hr_min.setCellValueFactory(new PropertyValueFactory<>("hr_min"));
        hr_max.setCellValueFactory(new PropertyValueFactory<>("hr_max"));
        sdnn.setCellValueFactory(new PropertyValueFactory<>("sdnn"));
        rmssd.setCellValueFactory(new PropertyValueFactory<>("rmssd"));
        sdsd.setCellValueFactory(new PropertyValueFactory<>("sdsd"));
        //ddpnn50.setCellValueFactory(new PropertyValueFactory<>("pnn50"));


        this.table_detail.getColumns().addAll(vlf,lf, hf, lf_hf, fft_total, hr_mean,hr_min, hr_max, sdnn, rmssd, sdsd);
        ObservableList<SegmentSignal> list = FXCollections.observableArrayList();
        // Creando nuevo segmento de la señal

        
        Integer contador = 1;
        for (Integer i = 0; i < signalController.getSignal().getTime_line().size(); i ++){
            SegmentSignal segmentSignal = new SegmentSignal();
            Float initial_time = signalController.getSignal().getTime_line().get(i).getKey().getStart_time();
            Float final_time = signalController.getSignal().getTime_line().get(i).getKey().getEnd_time();
            String emotion = signalController.getSignal().getTime_line().get(i).getKey().getEmotion().getName();
            String cardiac_coherence = signalController.getSignal().getTime_line().get(i).getKey().getDescription();
            String alert = signalController.getSignal().getTime_line().get(i).getKey().getText_alert();
            segmentSignal.setId(contador.toString());
            segmentSignal.setInitial_time(initial_time);
            segmentSignal.setFinal_time(final_time);
            segmentSignal.setEmotion(emotion);
            segmentSignal.setCardiac_coherence(cardiac_coherence);
            segmentSignal.setAlert(alert);
            int pos_freq = 0;
            for(Pair<String, Float> element : this.signalController.getSignal().getTime_line().get(i).getValue().getFrequency_feature()){
                if(element.getKey().equals("vlf"))
                    segmentSignal.setVlf(element.getValue());
                else if (element.getKey().equals("lf")) {segmentSignal.setLf(element.getValue());}
                else if (element.getKey().equals("hf")) {segmentSignal.setHf(element.getValue());}
                else if (element.getKey().equals("lf-hf")) {segmentSignal.setLf_hf(element.getValue());}
                else if (element.getKey().equals("fft_total")) {segmentSignal.setFft_total(element.getValue());}
                pos_freq ++;
            }
            int pos_time = 0;
            for(Pair<String, Float> element : this.signalController.getSignal().getTime_line().get(i).getValue().getTimes_feature()){
                if(element.getKey().equals("hr_mean"))
                    segmentSignal.setHr_mean(element.getValue());
                else if (element.getKey().equals("hr_min")) {segmentSignal.setHr_min(element.getValue());}
                else if (element.getKey().equals("hr_max")) {segmentSignal.setHr_max(element.getValue());}
                else if (element.getKey().equals("sdnn")) {segmentSignal.setSdnn(element.getValue());}
                else if (element.getKey().equals("rmssd")) {segmentSignal.setRmssd(element.getValue());}
                else if (element.getKey().equals("sdsd")) {segmentSignal.setSdsd(element.getValue());}
                //else if (element.getKey().equals("pnn50")) {segmentSignal.setPnn50(element.getValue());}
                pos_time++;
            }

            list.add(segmentSignal);
            contador++;
            //System.out.println(segmentSignal.getEmotion());
        }
        this.table_detail.setItems(list);

    }

    public void set_data_abstact(double millis){

        alert.setText("Coherencia Cardíaca: " + alert_task );
        emotion.setText("Emoción: " + emotion_task);
        cardiac_coherence.setText("Valor cardíaco: " + cardiac_coherence_task);
        initial_time.setText("Tiempo inicial: " + initial_time_task);
        final_time.setText("Tiempo final: " +  final_time_task);
        image_emotion.setImage(new Image(path_image_emotion));
        this.image_emotion.setFitHeight(181);
        this.image_emotion.setFitWidth(150);

    }

    private void addEventTag(){
        for(Integer i = 1; i < signalController.getSignal().getTime_line().size(); i++) {
            if (signalController.getSignal().getTime_line().get(i).getKey().getRatio_coherence() < 1) {
                double partial_initial_time = signalController.getSignal().getTime_line().get(i).getKey().getStart_time();
                double partial_final_time = signalController.getSignal().getTime_line().get(i).getKey().getEnd_time();
                double partial_ratio_coherence = signalController.getSignal().getTime_line().get(i).getKey().getRatio_coherence();
                Color color;
                if (partial_ratio_coherence <= 0.5)
                    color = Color.RED;
                else color = Color.ORANGE;

                TimelineTag timelineTag = new TimelineTag(
                        this.timelineElement.getRectangle().getWidth() * partial_initial_time * 1000 / (this.signalController.getSignal().getEnd_time_signal() * 1000),
                        this.timelineElement.getRectangle().getWidth() * partial_final_time * 1000 / (this.signalController.getSignal().getEnd_time_signal() * 1000) - this.timelineElement.getRectangle().getWidth() * partial_initial_time * 1000 / (this.signalController.getSignal().getEnd_time_signal() * 1000),
                        "Evento " + i.toString(),
                        "Coherencia Cardíaca de " + partial_ratio_coherence,
                        partial_initial_time * 1000,
                        partial_final_time * 1000 - partial_initial_time * 1000,
                        color,
                        this.getName(),
                        Paths.get(this.getProjectDir() + "/" + this.source_name).toAbsolutePath().toString());
                this.timelineElement.getChildren().add(timelineTag);
                this.getTags().add(timelineTag);
                root.fireEvent(new AddTagEvent(getName(), this.path_signal, timelineTag.getCode(),
                        timelineTag.getDescription(), timelineTag.getInitTimeInMS(), timelineTag.getLengthInMS()));
            }
        }
    }

    @FXML
    void handleButtonReport(ActionEvent event) {
        detailsController.create_report_excel(this.table_detail, this.signalController.getSignal().getFci(), this.signalController.getSignal().getTimes_fci(), this.getProjectDir(), this.source_name);

        //Creating a dialog
        Dialog<String> dialog = new Dialog<String>();
        //Setting the title
        dialog.setTitle("Información");
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        //Setting the content of the dialog
        dialog.setContentText("Se ha creado el reporte en el directorio: " + this.getProjectDir() + " \n con el nombre: " + this.source_name + ".xls");
        //Adding buttons to the dialog pane
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.showAndWait();

    }


    @FXML
    void handleButtonEvent(ActionEvent e){
        MFXTextField code = new MFXTextField();
        code.setFloatingText("Ingrese una codificación");
        MFXTextField desc = new MFXTextField();
        desc.setFloatingText("Ingrese una descripción del evento");

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(code, desc);

        dialogContent = MFXGenericDialogBuilder.build()
                .setContent(container)
                .setShowMinimize(false)
                .setShowAlwaysOnTop(false)
                .setShowClose(false)
                .setHeaderText("Añadir evento")
                .get();

        dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(root.getScene().getWindow())
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(true)
                .setTitle("Añadir evento")
                .setOwnerNode((BorderPane) root.getScene().getRoot())
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();

        dialogContent.addActions(
                Map.entry(new MFXButton("Añadir"), event -> {

                        if(code.getText().length() == 0 || desc.getText().length() == 0) return;
                        TimelineTag timelineTag = new TimelineTag(this.timelineElement.getSelectionX(),
                                this.timelineElement.getSelectionWidth(),
                                code.getText(),
                                desc.getText(),
                                signalController.getSignal().getEnd_time_signal() * 1000 * this.timelineElement.getSelectionX() / this.timelineElement.getWidth(),
                                signalController.getSignal().getEnd_time_signal() * 1000 * this.timelineElement.getSelectionWidth() / this.timelineElement.getWidth(),
                                Color.RED,
                                this.getName(),
                                Paths.get(this.getProjectDir() + "/" + this.source_name).toAbsolutePath().toString());
                        this.timelineElement.getChildren().add(timelineTag);
                        this.getTags().add(timelineTag);
                        root.fireEvent(new AddTagEvent(getName(), this.path_signal, timelineTag.getCode(),
                                timelineTag.getDescription(), timelineTag.getInitTimeInMS(), timelineTag.getLengthInMS()));
                        dialog.close();

                }), Map.entry( new MFXButton("Cancelar"), event -> {
                    dialog.close();
                })
        );
        dialog.show();

    }

    public Dialog create_charge_dialog(){
        this.dialog_charge = new Dialog<>();
        //Setting the title
        this.dialog_charge.setTitle("Procesando señal");
        //Setting the content of the dialog
        this.dialog_charge.setContentText("EMCODER-Heart se encuentra analizando la señal ingresada...");

        final ProgressBar pb = new ProgressBar();
        pb.setProgress(-1.0f);
        final ProgressIndicator pin = new ProgressIndicator();
        pin.setProgress(-1.0f);

        final HBox hb = new HBox();
        hb.setSpacing(5);
        hb.setAlignment(Pos.CENTER);
        hb.getChildren().addAll(pb,pin);
        //Adding buttons to the dialog pane
        dialog_charge.getDialogPane().getChildren().addAll(hb);
        dialog_charge.showAndWait();
        return this.dialog_charge;
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
