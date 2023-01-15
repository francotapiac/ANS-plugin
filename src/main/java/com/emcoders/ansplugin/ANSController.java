package com.emcoders.ansplugin;

import com.emcoders.ansplugin.components.TagDialog;
import com.emcoders.ansplugin.controllers.*;
import com.emcoders.ansplugin.models.Emotion;
import com.emcoders.ansplugin.models.Project;
import com.emcoders.ansplugin.models.SegmentSignal;
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
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;

import java.io.*;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import com.jfoenix.controls.JFXButton;
import javafx.util.Pair;

import javax.swing.*;


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
    private Label cant_segmento_panel;
    @FXML
    private Label segmento_panel;
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
    private JFXButton btn_process_signal;
    @FXML
    private JFXButton btn_segment_report;

    //Botones render señal
    @FXML
    private JFXButton btn_adelantar;
    @FXML
    private JFXButton btn_atras;
    @FXML
    private JFXButton btn_play;
    @FXML
    private Slider slide_chart;
    Double render_millis_chart;
    boolean is_play;
    Timer timer; //timer del render

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

    @FXML
    private ComboBox<String> combobox_emotions;
    @FXML
    private JFXButton btn_set_emotion;
    private MFXGenericDialog dialogContent;
    private MFXStageDialog dialog;


    // Atributos
    private final ArrayList<TimelineTag> tags = new ArrayList<>();
    private ExecutorService backgroundWorkers;

    private String path_video = "";
    private String path_signal = "";

    // Controladores
    SignalController signalController;
    ChartController layoutController;
    VideoController videoController;

    ReportController reportController;

    Float millis;
    TimelineElement timelineElement;
    TimelineElement timelineElementEmotion;
    Integer segment_time;
    String alert_task;
    String emotion_task;
    String cardiac_coherence_task;
    String initial_time_task;
    String final_time_task;
    String source_name;
    InputStream path_image_emotion;
    Boolean activate_btn_file_chart = true;
    private ArrayList<BooleanProperty> tagsSelected = new ArrayList<>();
    SegmentSignal segmentSignal;

    // Instance state //
    private volatile boolean isLoaded = false;

    //Proyecto
    Project project;
    String sample = "1000";
    String window = "20";
    String shif = "1";


    /************************************************
     1.    Métodos heredados de proyecto EMBox-Core
     ***********************************************/

    /*
    1.1. Métodos inicializar componentes en vista
     */
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

        //Moviendo todos los paneles en su posición real
        this.detail_panel.setLayoutX(this.pane_principal.getLayoutX());
        this.detail_panel.setLayoutY(this.pane_principal.getLayoutY());

        this.pane_principal.toFront();
        this.tagDialog = new TagDialog();
        this.reportController = new ReportController();
        this.radio_general.setSelected(true);
        ObservableList<String> items = FXCollections.observableArrayList();
        items.addAll("Sorpresa", "Felicidad", "Miedo", "Enojo", "Tristeza");
        this.combobox_emotions.getItems().addAll(items);
        this.combobox_emotions.setOnAction(e -> disable_combobox_emotion());

        combobox_emotions.valueProperty().addListener((ov, p1, p2) -> {
            System.out.println("Nueva Selección: " + p2);
            System.out.println("Vieja Selección: " + p1);
        });

        //Agregando imagenes a botones de render de gráfico
        this.btn_play.setGraphic(create_image_btn_render("images/play.png"));
        this.btn_atras.setGraphic(create_image_btn_render("images/back.png"));
        this.btn_adelantar.setGraphic(create_image_btn_render("images/next.png"));
        this.render_millis_chart = 0.0;
        //Indicando que no se ha presionado el botón play
        this.is_play = false;

        //Se desctivan botones
        this.btn_set_emotion.setDisable(true);
        this.btn_create_event.setDisable(true);
        this.btn_process_signal.setDisable(true);
        this.combobox_emotions.setDisable(true);
        //this.btn_segment_report.setDisable(true);
        this.slide_chart.setDisable(true);

        //Agregando listener para cada fila de la tabla de detalles
        this.table_detail.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                //Check whether item is selected and set value of selected item to Label
                if(table_detail.getSelectionModel().getSelectedItem() != null)
                {
                    segmentSignal = table_detail.getSelectionModel().getSelectedItem();
                    //btn_segment_report.setDisable(false);
                    System.out.println(segmentSignal.getHr_max());
                }
            }
        });

        //Timer del render para ver chart como vídeo
        this.timer = new Timer();
    }

    /*
    1.2. Métodos para reproducir cursor en banda
     */

    // Inicializa cursor en banda
    public void play() {
        this.pushPlaySignal(null);
        this.is_play = true;
    }

    // Detiene cursor en banda
    public void stop() {
    }

    // Pausa cursor en banda
    public void pause() {
        this.pushPlaySignal(null);
        this.is_play = false;
    }

    // Lee cada elemento en banda tras mover el cursor
    public void seek(double millis) {
        System.out.println(millis);
        slide_chart.setValue(millis/1000);
        //Se activan botones
        this.btn_set_emotion.setDisable(false);
        this.btn_create_event.setDisable(false);
        this.combobox_emotions.setDisable(false);

        this.millis = format_number(millis);
        signalController.get_particular_data(format_number(millis));
        alert_task = signalController.getCardiac_coherence_description();
        segment_time = signalController.getN_segment();
        emotion_task = signalController.getEmotion();
        cardiac_coherence_task = signalController.getRatio_coherence().toString();
        initial_time_task = signalController.getPartial_start_time().toString();
        final_time_task = signalController.getPartial_end_time().toString();
        path_image_emotion =  ANSController.class.getResourceAsStream(signalController.getPath_image_emotion());
        //select_type_view_chart(type_view_chart);
        System.out.println(type_view_chart);
        Platform.runLater(() -> {
            //update application thread
          /*  xAxis.setLowerBound(lower_x_axis);
            System.out.println(upper_x_axis);
            xAxis.setUpperBound(upper_x_axis);
            xAxis.setAutoRanging(false);*/
            // set_data_abstact(millis);
        });
        //System.out.println(millis);
    }

    /*
    1.3. Métodos lectura de archivos
     */

    // Comprueba si puede abrir archivo con señal
    public boolean canOpen(String path) {
        if(path != "" && path != null){
            File file = new File(path);
            if(file.exists())
                return true;
        }
        return false;
    }

    // Abre un archivo con la señal cardíaca según la ruta
    public TimelineElement openSource(String path) {

        //Activando botón
        this.btn_process_signal.setDisable(false);

        this.path_signal = path;
        this.source_name = Paths.get(path).getFileName().toString();

        //Párametros de la línea de tiempo
        Double sourceLength = get_length_signal_file(path);
        timelineElement = new TimelineElement(path,sourceLength,"Psicofisiológico", Color.valueOf("81b622"));
        this.root.fireEvent(
                new AddSourceEvent(this.getProjectDir() + "\\" + this.source_name, this.getName())
        );

        return this.timelineElement;
    }

    public ImageView create_image_btn_render(String path_image){
        Image image = new Image(getClass().getResourceAsStream(path_image));
        ImageView image_view = new ImageView(image);
        image_view.setFitHeight(20);
        image_view.setFitWidth(20);
        return image_view;
    }

    public void disable_combobox_emotion(){
        if(this.combobox_emotions.getValue().equals(this.emotion_task)){
            this.btn_set_emotion.setDisable(true);
        }
        else{
            this.btn_set_emotion.setDisable(false);
        }
        try {
            Emotion emotion_aux = new Emotion("");
            InputStream url = ANSController.class.getResourceAsStream(emotion_aux.create_path(this.combobox_emotions.getValue()));
            this.image_emotion.setImage(new Image(url));
            this.image_emotion.setFitHeight(181);
            this.image_emotion.setFitWidth(150);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<TimelineTag> getTags() {
        return this.tags;
    }








    public void get_signal_rest(String path){
        //Ruta completa
        String route_api = "http://127.0.0.1:5000/heart/" + path + "/" + sample + "/" + window + "/" + shif;

        // Instanciando controlador de señal
        signalController = new SignalController();
        signalController.create_signal(route_api);

        //Guardando analisis
        reportController.create_time_line_excel(signalController.getSignal().getTime_line(), getProjectDir(), 0);
        reportController.create_time_line_excel(signalController.getSignal().getPartitions_time_line(), getProjectDir(), 1);
        reportController.create_signal_excel(this.signalController.getSignal().getFci(), this.signalController.getSignal().getTimes_fci(), getProjectDir());

        //Obteniendo segmentos a analizar de la señal
        Double start_time = signalController.getSignal().getEnd_time_signal() * 1000 * timelineElement.getSelectionX() / timelineElement.getWidth();
        Double end_time = start_time + signalController.getSignal().getEnd_time_signal() * 1000 * timelineElement.getSelectionWidth() / timelineElement.getWidth();

        this.path_signal = path;
        this.source_name = Paths.get(path).getFileName().toString();

        Platform.runLater(() -> {
            //update application thread
            openSourceThread(path);
            addEventTag();
            //Desactivando botón procesar señal y activando opción de crear alerta
            this.btn_process_signal.setDisable(true);
            this.btn_create_event.setDisable(false);

            // dialog.close();
        });
    }

    public void dialog_project_create(){
        Dialog<Project> dialogProject = new Dialog<>();
        dialogProject.setTitle("Información del insumo");
        dialogProject.setHeaderText("Por favor, ingrese sigueintes datos de la señal cardíaca…");
        DialogPane dialogPane = dialogProject.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label sampleLabel = new Label("Frecuencia de Muestreo (Hz)");
        TextField sampleRate = new TextField("1000");
        Tooltip  tSample = new Tooltip("Número de muestras por unidad de tiempo para generar señal discreta");
        tSample.setFont(Font.font("Verdana", FontPosture.REGULAR, 10));
        tSample.setTextAlignment(TextAlignment.RIGHT);
        sampleLabel.setTooltip(tSample);

        Label windowLabel = new Label("Ventana de Tiempo (Milisegundos)");
        TextField windowSampling = new TextField("20000");
        Tooltip  tWindow = new Tooltip("Rango inicial para procesar señal cardíaca");
        tWindow.setFont(Font.font("Verdana", FontPosture.REGULAR, 10));
        tWindow.setTextAlignment(TextAlignment.RIGHT);
        windowLabel.setTooltip(tWindow);

        Label shifLabel = new Label("Tiempo de desplazamiento (Milisegundos)");
        TextField shifSampling = new TextField("1000");
        Tooltip  tShif = new Tooltip("Bloques de desplazamiento de la ventana sobre la señal");
        tShif.setFont(Font.font("Verdana", FontPosture.REGULAR, 10));
        tShif.setTextAlignment(TextAlignment.RIGHT);
        shifLabel.setTooltip(tShif);

        dialogPane.setContent(new VBox(8, sampleLabel, sampleRate, windowLabel, windowSampling, shifLabel, shifSampling));
        Platform.runLater(sampleRate::requestFocus);
        Platform.runLater(windowSampling::requestFocus);
        Platform.runLater(shifSampling::requestFocus);

        dialogProject.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                this.sample = sampleRate.getText();
                this.window = windowSampling.getText();
                this.shif = shifSampling.getText();
                project = new Project(this.getName(), this.getProjectDir(), Double.parseDouble(sample),
                        Double.parseDouble(window), Double.parseDouble(shif) );


            } else if (button == ButtonType.CANCEL) {
                dialogProject.close();
            }

            return null;
        });
        Optional<Project> optionalResult = dialogProject.showAndWait();
        optionalResult.ifPresent((Project results) -> {

        });
    }

    public Double get_length_signal_file(String path){
        Double sourceLength = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
            int count = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                count++;
            }
            sourceLength = count/1000.0;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sourceLength;

    }

    public void dialog_charge(){
        MFXTextField code = new MFXTextField();
        code.setFloatingText("Cargando señal...");
        MFXTextField desc = new MFXTextField();
        desc.setFloatingText("Ingrese una descripción de la alerta");

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        //container.getChildren().addAll(code, desc);

        dialogContent = MFXGenericDialogBuilder.build()
                .setContent(container)
                .setShowMinimize(false)
                .setShowAlwaysOnTop(false)
                .setShowClose(false)
                .setHeaderText("Cargando Señal...")
                .get();

        dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(root.getScene().getWindow())
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(true)
                .setTitle("Cargando Señal...")
                .setOwnerNode((BorderPane) root.getScene().getRoot())
                .setScrimPriority(ScrimPriority.WINDOW)
                .setScrimOwner(true)
                .get();

        ProgressIndicator pi;
        dialogContent.addActions(
                Map.entry(pi = new ProgressIndicator(), event -> {
                    pi.setProgress(-1.0f);
                })
        );
        dialog.show();
    }

    public void openSourceThread(String path){


        //Seteando layout principal (componente señal de VBox) con la señal leída
        layoutController = new ChartController();
        layoutController.initialize_panes(signalController);

        //*********************** Emoción de señal ***********************
        this.emotion_abstract.setText("Emoción general: " + signalController.getSignal().getPredominant_emotion().toString());
        try {
            InputStream url = ANSController.class.getResourceAsStream(signalController.getSignal().getEmotion_image_path());
            this.image_emotion.setImage(new Image(url));
            this.image_emotion.setFitHeight(181);
            this.image_emotion.setFitWidth(150);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //*********************** Análisis de señal ***********************
        //this.alert.setText("N° Alertas: " + signalController.getSignal().getCount_alerts().toString());
        this.alert.setText("N° Alertas: " +   signalController.calculateNumberAlertas());

        this.cant_segmento_panel.setText("Cantidad de Particiones: " + signalController.getSignal().getTime_line().size());
        Float initial_seg = format_number(signalController.getSignal().getStart_time_signal());
        Float final_seg = format_number(signalController.getSignal().getEnd_time_signal());
        segmento_panel.setText("Segmento: [" + initial_seg + "s , " + final_seg + "s]");
        cardiac_coherence.setText("Nombre de archivo: " + Paths.get(path).getFileName().toString());

        //*********************** Vídeo ***********************
        //this.path_video = "C:\\Users\\Franco\\Desktop\\Escritorio\\Universidad\\Memoria\\ANS-plugin\\audio.wav";
        if(path_video !=""){
            videoController = new VideoController();
            MediaPlayer media = videoController.initialize_video(path_video);
            this.media.setMediaPlayer(media);
        }

        //*********************** Gráfico ***********************
        //Modificando atributos de gráfico
        this.chart.setTitle("Frecuencia Cardíaca Instantánea en el Tiempo");
        this.chart.getXAxis().setLabel("Tiempo (s)");
        this.chart.getYAxis().setLabel("Frecuencia Cardíaca (Hz)");
        this.chart.setCreateSymbols(false);

        //Inicializando gráfico
        //this.chart.getData().addAll(layoutController.getSeries_fci(), layoutController.getSeries_points());
        this.chart.getData().addAll(layoutController.getSeries_fci());
        Double min_fci = signalController.getSignal().getMin_max_fci_point().getKey();
        Double max_fci = signalController.getSignal().getMin_max_fci_point().getValue();
        yAxis.setLowerBound(min_fci - 1);
        yAxis.setUpperBound(max_fci + 1);
        xAxis.setLowerBound(signalController.getSignal().getStart_time_signal());
        xAxis.setUpperBound(signalController.getSignal().getEnd_time_signal());
        this.chart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent");
        this.type_view_chart = 1;
        this.chart.getYAxis().setAutoRanging(false);
        this.chart.setAnimated(true);

        //*********************** Tabla de detalles ***********************
        create_table_detail(true);
        //reportController.create_report_excel(this.table_detail, this.signalController.getSignal().getFci(), this.signalController.getSignal().getTimes_fci(), this.signalController.getSignal(), this.getProjectDir(), this.source_name);
       // reportController.create_time_line_excel(signalController.getSignal().getTime_line(), getProjectDir(), 1);

        //Render
        this.slide_chart.setDisable(false);
        this.slide_chart.setMax(signalController.getSignal().getEnd_time_signal());
        this.slide_chart.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if(render_millis_chart >= signalController.getSignal().getEnd_time_signal() || render_millis_chart == 0.0)
                Platform.runLater(() -> {
                    this.btn_play.setGraphic(create_image_btn_render("images/play.png"));
                    this.is_play = false;
                });
            render_millis_chart = newValue.doubleValue();
            render_check_signal(newValue.doubleValue(), 3.0); //Aumenta el contador
        });

    }

    public TimelineElement loadProjectSource(String path) {

        signalController = new SignalController();
        this.source_name = Paths.get(path).getFileName().toString();
        //String complete_path = path + ".xls";
        String complete_path_segment_time_line = getProjectDir() + "/" + "senalAnalisis.xls";
        String complete_path_partitions_time_line = getProjectDir() + "/" + "senalParticiones.xls";
        String complete_path_signal = getProjectDir() + "/" + "fciPoints.xls";
        if(complete_path_segment_time_line != "" && complete_path_segment_time_line != null ){
            File file = new File(complete_path_segment_time_line);
            if(file.exists()){
                // Instanciando controlador de seña
                List<List<String>> time_line_xls = reportController.read_xls(complete_path_segment_time_line,true,1);
                List<List<String>> time_line_original_xls = reportController.read_xls(complete_path_partitions_time_line,true,1);
                List<List<String>> signal_xls = reportController.read_xls(complete_path_signal,true,1);
                System.out.println("time: \n " + time_line_xls);
                System.out.println("Signal: \n " + signal_xls);
                signalController.create_signal_excel(time_line_xls, time_line_original_xls, signal_xls);
                Platform.runLater(() -> {
                    //update application thread
                    openSourceThread(path);
                    //addEventTag();
                });

            }
            else{
                // Pidiendo api
                get_signal_rest(path);
                //Párametros de la línea de tiempo

            }
            Double sourceLength = signalController.get_sourceLength();
            timelineElement = new TimelineElement(path,sourceLength,"Psicofisiológico", Color.valueOf("81b622"));
        }
        return this.timelineElement;
    }

    public void loadProjectTag(String source, String code, String desc, double initTimeInMS, double lengthInMS) {
        Color color;
        signalController.get_particular_data_charge_line_time(initTimeInMS);
        if (signalController.getRatio_coherence() <= 0.5) color = Color.rgb(105,105,105);
         else color = Color.rgb(169,169,169);


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
        signalController.removeAlert(initTimeInMS/1000, (initTimeInMS + lengthInMS)/1000);
        create_table_detail(false);
        reportController.create_time_line_excel(signalController.getSignal().getTime_line(), getProjectDir(), 0);
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

        String nameAlert = "";
        String description = "";
        signalController.get_particular_data_charge_line_time(initTimeInMS);

        TimelineTag tagToUpdate = null;
        for (TimelineTag t : this.getTags()) {
            if(
                    Objects.equals(source, t.getSource()) &&
                            initTimeInMS == t.getInitTimeInMS() &&
                            lengthInMS == t.getLengthInMS()
            ){
                nameAlert = t.getCode();
                description = t.getDescription();

                break;
            }
        }

        Dialog<Project> dialogProject = new Dialog<>();
        dialogProject.setTitle("Descripción de alerta");
        dialogProject.setHeaderText(nameAlert);
        DialogPane dialogPane = dialogProject.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK);


        //Label nameLabel = new Label("Nombre de alerta: " + nameAlert);
        Label descriptionLabel = new Label("Descripción de alerta: " + description);
        Label initialTime = new Label("Tiempo inicial (ms): " + format_number(initTimeInMS/1000));
        Label finalTime = new Label("Tiempo Final (ms): " + format_number(initTimeInMS/1000 + lengthInMS/1000));
        Label emotion = new Label("Emoción: " + signalController.getEmotion());
        Label coherence = new Label("Coherencia cardíaca: "  + signalController.getCardiac_coherence_description());
        //System.out.println( ANSController.class.getResourceAsStream(signalController.getPath_image_emotion()));
        //image_emotion.setImage(new Image( ANSController.class.getResourceAsStream(signalController.getPath_image_emotion())));

        dialogPane.setContent(new VBox(8, descriptionLabel, initialTime, finalTime,  coherence, emotion));

        dialogProject.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK ) {
                dialogProject.close();
            }

            return null;
        });
        Optional<Project> optionalResult = dialogProject.showAndWait();
        optionalResult.ifPresent((Project results) -> {

        });

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
        this.pane_principal.toBack();

    }

    @FXML
    void handleButtonGeneral(ActionEvent event) {
        this.general_btn.setStyle("-fx-background-color: #d8d7f6");
        this.alert_btn.setStyle("-fx-background-color: White");
        this.pane_principal.setLayoutX(this.pane_principal.getLayoutX());
        this.pane_principal.setLayoutY(this.pane_principal.getLayoutY());
        this.pane_principal.toFront();
        this.detail_panel.toBack();

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
                            //emotion.setText(p.toString());
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
        Platform.runLater(() -> {
            //update application thread
            xAxis.setLowerBound(lower_x_axis);
            xAxis.setUpperBound(upper_x_axis);
            xAxis.setAutoRanging(false);
        });

    }

    // Crear tabla con detalles de cada alerta
    @FXML
    void create_table_detail(boolean first_instance){
        if(first_instance) {
            this.segment_detail.setCellValueFactory(new PropertyValueFactory<>("id"));
            this.initial_time_detail.setCellValueFactory(new PropertyValueFactory<>("initial_time"));
            this.final_time_detail.setCellValueFactory(new PropertyValueFactory<>("final_time"));
            this.emotion_detail.setCellValueFactory(new PropertyValueFactory<>("emotion"));
            this.cardiac_detail.setCellValueFactory(new PropertyValueFactory<>("cardiac_coherence"));
            this.alert_detail.setCellValueFactory(new PropertyValueFactory<>("alert"));

            TableColumn ratio_coherence = new TableColumn("Valor Coherencia");
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

            ratio_coherence.setCellValueFactory(new PropertyValueFactory<>("ratio_coherence"));
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

            this.table_detail.getColumns().addAll(ratio_coherence, vlf,lf, hf, lf_hf, fft_total, hr_mean,hr_min, hr_max, sdnn, rmssd, sdsd);
        }

        this.table_detail.getItems().clear();

        ObservableList<SegmentSignal> list = FXCollections.observableArrayList();
        // Creando nuevo segmento de la señal

        
        Integer contador = 1;
        for (Integer i = 0; i < signalController.getSignal().getTime_line().size(); i ++){
            SegmentSignal segmentSignal = new SegmentSignal();
            Float initial_time = signalController.getSignal().getTime_line().get(i).getKey().getStart_time();
            Float final_time = signalController.getSignal().getTime_line().get(i).getKey().getEnd_time();
            String emotion = signalController.getSignal().getTime_line().get(i).getKey().getEmotion().getName();
            String cardiac_coherence = signalController.getSignal().getTime_line().get(i).getKey().getDescription();
            Float ratio_coherence_time = signalController.getSignal().getTime_line().get(i).getKey().getRatio_coherence();
            String alert = signalController.getSignal().getTime_line().get(i).getKey().getText_alert();
            segmentSignal.setId(contador.toString());
            segmentSignal.setInitial_time(initial_time);
            segmentSignal.setFinal_time(final_time);
            segmentSignal.setEmotion(emotion);
            segmentSignal.setCardiac_coherence(cardiac_coherence);
            segmentSignal.setAlert(alert);
            segmentSignal.setRatio_coherence(ratio_coherence_time);
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
        alert.setText("Descripción: " + alert_task );
        cant_segmento_panel.setText("N° Segmento: " + segment_time);
        cardiac_coherence.setText("Valor cardíaco: " + cardiac_coherence_task);
        Float initial_seg =  format_number(Double.parseDouble(initial_time_task))/1000;
        Float final_seg = format_number(Double.parseDouble(final_time_task))/1000;
        segmento_panel.setText("Segmento: [" + initial_seg + "s , " + final_seg + "s]");
        image_emotion.setImage(new Image(path_image_emotion));
        this.emotion_abstract.setText(this.emotion_task);
        this.image_emotion.setFitHeight(181);
        this.image_emotion.setFitWidth(150);

    }

    //Modifica colores y tag de la línea de tiempo
    private void addEventTag(){
        for(Integer i = 1; i < signalController.getSignal().getTime_line().size(); i++) {
            if (signalController.getSignal().getTime_line().get(i).getKey().getRatio_coherence() < 1) {
                double partial_initial_time = signalController.getSignal().getTime_line().get(i).getKey().getStart_time();
                double partial_final_time = signalController.getSignal().getTime_line().get(i).getKey().getEnd_time();
                double partial_ratio_coherence = signalController.getSignal().getTime_line().get(i).getKey().getRatio_coherence();
                Color color;
                if (partial_ratio_coherence <= 0.5 && i != 0)
                    color = Color.rgb(105,105,105);
                else if (i == 0) {
                    color = Color.LIGHTGRAY;

                } else color = Color.rgb(169,169,169);

                TimelineTag timelineTag = new TimelineTag(
                        this.timelineElement.getRectangle().getWidth() * partial_initial_time * 1000 / (this.signalController.getSignal().getEnd_time_signal() * 1000),
                        this.timelineElement.getRectangle().getWidth() * partial_final_time * 1000 / (this.signalController.getSignal().getEnd_time_signal() * 1000) - this.timelineElement.getRectangle().getWidth() * partial_initial_time * 1000 / (this.signalController.getSignal().getEnd_time_signal() * 1000),
                        "Alerta " + i.toString(),
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
        reportController.create_time_line_excel(signalController.getSignal().getTime_line(), getProjectDir(), 0);

        //Creating a dialog
        Dialog<String> dialog = new Dialog<String>();
        //Setting the title
        dialog.setTitle("Información");
        ButtonType type = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        //Setting the content of the dialog
        dialog.setContentText("Se ha creado el reporte en el directorio: " + this.getProjectDir() + " \n Con el nombre: " + "senalAnalisis.xls");
        //Adding buttons to the dialog pane
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.showAndWait();


    }

    //Método que procesa la señal ingresada
    @FXML
    void handleButtonProcessSignal(ActionEvent e){
        VBox vBoxCharge = new VBox();
        dialog_project_create();
        // Create a background Task
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                get_signal_rest(path_signal);
                Platform.runLater(() -> {

                });

                //get_signal_rest(path_signal);
                return null;
            }
        };

        //Mientras se ejecuta la tarea
        task.setOnRunning(wse -> {
           dialog_charge();
           vBoxCharge.getChildren().add(new Label("Procesando señal"));
           dialogContent.setContent(vBoxCharge);
            if(signalController.getSignal()!= null) {
                vBoxCharge.getChildren().add(new Label("Respaldando Procesamiento"));
                dialogContent.setContent(vBoxCharge);
                vBoxCharge.getChildren().add(new Label("Generando alertas automáticas"));
                dialogContent.setContent(vBoxCharge);
            }


        });

        // This method allows us to handle any Exceptions thrown by the task
        task.setOnFailed(wse -> {
            wse.getSource().getException().printStackTrace();
            dialogContent.setContent(new Label("Error: No se ha podido procesar correctamente la señal"));
        });

        // If the task completed successfully, perform other updates here
        task.setOnSucceeded(wse -> {
            dialogContent.setContent(new Label("Se ha procesado la señal exitosamente"));
            System.out.println("Done!");
            dialog.close();
        });



        // Now, start the task on a background thread
        new Thread(task).start();


    }


    @FXML
    void handleButtonEvent(ActionEvent e){
        this.btn_set_emotion.setDisable(true);
        Dialog<Project> dialogProject = new Dialog<>();
        dialogProject.setTitle("Alerta manual");
        dialogProject.setHeaderText("Por favor, ingrese los siguientes datos para crear una alerta manual");
        DialogPane dialogPane = dialogProject.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Label codeLabel = new Label("Nombre de alerta");
        TextField code = new TextField();
        Tooltip  tCode = new Tooltip("Codificación de la alerta");
        tCode.setFont(Font.font("Verdana", FontPosture.REGULAR, 10));
        tCode.setTextAlignment(TextAlignment.RIGHT);
        codeLabel.setTooltip(tCode);

        Label descriptionLabel = new Label("Ingrese la descripción de la alerta");
        TextField desc = new TextField();
        Tooltip  tDescription = new Tooltip("Información sobre la alerta generada");
        tDescription.setFont(Font.font("Verdana", FontPosture.REGULAR, 10));
        tDescription.setTextAlignment(TextAlignment.RIGHT);
        codeLabel.setTooltip(tDescription);

        Label emotionLabel = new Label("Ingrese emoción para alerta");


        dialogPane.setContent(new VBox(8,codeLabel, code, descriptionLabel, desc, emotionLabel, this.combobox_emotions));
        Platform.runLater(code::requestFocus);
        Platform.runLater(desc::requestFocus);

        dialogProject.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK && (code.getText().length() != 0 || desc.getText().length() != 0 || this.combobox_emotions.getValue() != "")) {


                TimelineTag timelineTag = new TimelineTag(this.timelineElement.getSelectionX(),
                        this.timelineElement.getSelectionWidth(),
                        code.getText(),
                        desc.getText(),
                        this.timelineElement.getSourceLength() * 1000 * this.timelineElement.getSelectionX() / this.timelineElement.getWidth(),
                        this.timelineElement.getSourceLength() * 1000 * this.timelineElement.getSelectionWidth() / this.timelineElement.getWidth(),
                        Color.LIGHTBLUE,
                        this.getName(),
                        Paths.get(this.getProjectDir() + "/" + this.source_name).toAbsolutePath().toString());
                this.timelineElement.getChildren().add(timelineTag);
                this.getTags().add(timelineTag);
                root.fireEvent(new AddTagEvent(getName(), this.path_signal, timelineTag.getCode(),
                        timelineTag.getDescription(), timelineTag.getInitTimeInMS(), timelineTag.getLengthInMS()));
                System.out.println( this.timelineElement.getSourceLength());
                System.out.println(signalController.getSignal().getEnd_time_signal());

                // Seteando valores de alerta
                this.signalController.set_manual_alert_aux(timelineTag.getInitTimeInMS()/1000, (timelineTag.getInitTimeInMS()+ timelineTag.getLengthInMS())/1000, this.combobox_emotions.getValue() );
                //Modificando tabla con nueva alerta en segmento específico
                create_table_detail(false);
                reportController.create_time_line_excel(signalController.getSignal().getTime_line(), getProjectDir(), 0);
                this.btn_set_emotion.setDisable(false);
                dialogProject.close();
            } else if (button == ButtonType.CANCEL) {
                dialogProject.close();
            }

            return null;
        });
        Optional<Project> optionalResult = dialogProject.showAndWait();
        optionalResult.ifPresent((Project results) -> {

        });



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

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setAlignment(Pos.CENTER);
        Label label = new Label();
        label.setText("EMCODER-Heart se ecuentra analizando la señal ingresada...");
        vbox.getChildren().addAll(pin, label);
        //Adding buttons to the dialog pane
        dialog_charge.getDialogPane().getChildren().addAll(vbox);
        dialog_charge.showAndWait();
        return this.dialog_charge;
    }



    @FXML
    void handleButtonSetEmotion(ActionEvent event) {
        //Obteniendo valores específicos del segmento seleccionado de la señal con la nueva emoción
        signalController.set_particular_emotion(this.millis, this.combobox_emotions.getValue());

        //Modificando tabla con nueva emoción en segmento específico
        create_table_detail(false);
        reportController.create_time_line_excel(signalController.getSignal().getTime_line(), getProjectDir(), 0);

        //Modificando Labels
        emotion_task = signalController.getEmotion();
        this.emotion_abstract.setText(this.emotion_task);

        //Desactivando botón para la emoción seleccionada en el segmento en específico
        this.btn_set_emotion.setDisable(true);
    }

    @FXML
    void handleButtonExportSegment(ActionEvent event){


        SegmentSignal segmentSignal = this.table_detail.getSelectionModel().getSelectedItem();
        System.out.println(segmentSignal.getEmotion());
    }

    // Botones para pausar, comenzar, retroceder y adelantar señal
    @FXML
    void pushNextSignal(ActionEvent event) {
        //Detiene el avance en play de la señal
        if(is_play){
            is_play = false;
            timer.cancel();
            this.btn_play.setGraphic(create_image_btn_render("images/play.png"));
        }
        if(render_millis_chart < signalController.getSignal().getEnd_time_signal()){
            render_millis_chart = render_millis_chart + 0.5;
            slide_chart.setValue(render_millis_chart); //Activa el listener ubicado en la sección render del método openSourceThread
        }
    }

    @FXML
    void pushReturnSignal(ActionEvent event) {
    //Detiene el avance en play de la señal
        if(is_play){
            is_play = false;
            timer.cancel();
            this.btn_play.setGraphic(create_image_btn_render("images/play.png"));
        }
        if(render_millis_chart > 0){
            render_millis_chart = render_millis_chart - 0.5;
            slide_chart.setValue(render_millis_chart); //Activa el listener ubicado en la sección render del método openSourceThread
        }
    }

    @FXML
    void pushPlaySignal(ActionEvent event) {

        if(is_play){
            is_play = false;
            timer.cancel();
            this.btn_play.setGraphic(create_image_btn_render("images/play.png"));
        }
        else{
            timer = new Timer();
            is_play = true;
            this.btn_play.setGraphic(create_image_btn_render("images/pause.png"));
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {

                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if(render_millis_chart >= signalController.getSignal().getEnd_time_signal()){
                                render_millis_chart = 0.0;
                                slide_chart.setValue(0.0);
                                cancel();
                            }
                            else{
                                //render_check_signal(render_millis_chart, 3.0);
                                Platform.runLater(() -> {
                                    slide_chart.setValue(render_millis_chart); //Activa el listener ubicado en la sección render del método openSourceThread
                                });

                                render_millis_chart = render_millis_chart + 1;
                            }
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    public void render_check_signal(Double render_millis_chart, Double window){
        Double low;
        Double upper;
        if(render_millis_chart < window){
            low = 0.0;
            upper = render_millis_chart + window;
       } else if (render_millis_chart >= window && render_millis_chart < signalController.getSignal().getEnd_time_signal()) {
            low = render_millis_chart - window;
            upper = render_millis_chart + window;
        }
        else{
            low = render_millis_chart - window;
            upper = signalController.getSignal().getEnd_time_signal();
        }

        signalController.get_particular_data(format_number(render_millis_chart*1000));
        alert_task = signalController.getCardiac_coherence_description();
        segment_time = signalController.getN_segment();
        emotion_task = signalController.getEmotion();
        cardiac_coherence_task = signalController.getRatio_coherence().toString();
        initial_time_task = signalController.getPartial_start_time().toString();
        final_time_task = signalController.getPartial_end_time().toString();
        path_image_emotion =  ANSController.class.getResourceAsStream(signalController.getPath_image_emotion());
        System.out.println(type_view_chart);
        Platform.runLater(() -> {
            //update application thread
            System.out.println("lower: " + low);
            System.out.println("upper: " + upper);
            xAxis.setLowerBound(low);
            xAxis.setUpperBound(upper);
            xAxis.setAutoRanging(false);
            set_data_abstact(render_millis_chart);
        });
    }




    // Entrega un número con formato .0000
    public Float format_number(Double number){
        DecimalFormat numeroFormateado = new DecimalFormat("#.00000");
        String textoFormateado = numeroFormateado.format(number);
        String[] list_number = textoFormateado.split(",");
        String new_number = list_number[0] + "." + list_number[1];
        return Float.parseFloat(new_number);
    }



}
