package com.emcoders.ansplugin.services;

import com.emcoders.ansplugin.controllers.SignalController;
import com.emcoders.scansembox.Events.AddSourceEvent;
import com.emcoders.scansembox.Events.DeleteSourceEvent;
import io.github.palexdev.materialfx.controls.MFXProgressBar;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialog;
import io.github.palexdev.materialfx.dialogs.MFXGenericDialogBuilder;
import io.github.palexdev.materialfx.dialogs.MFXStageDialog;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import org.json.JSONArray;
import org.json.JSONObject;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.opencv.videoio.Videoio.CAP_PROP_FPS;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_COUNT;

public class SourceService {

    // Root element
    private final Pane root;

    //Instancia para obtener señal de sujeto investigado
    private SignalController signal_controller;
    // Instance for capturing the frames
    private final VideoCapture videoCapture;
    // ExecutorService for background threads
    protected ExecutorService backgroundTasks;
    // List with all the know sources
    private JSONArray knownSources;
    // Directory of the project
    private final String projectDir;
    // Directory of the plugin
    private final String pluginDir;
    // Name of the source file
    private String sourceName;
    // Name of the channel
    private final String channelName;
    // Seconds of duration of the source file
    private double sourceLength;
    // Integer to take control of the frame refresh rate
    private final IntegerProperty FRAME_PERIOD;
    // Double as the frames total
    private double FRAMES;

    public SourceService(Pane root, String projectDir, String name) {
        this.videoCapture = new VideoCapture();
        this.root = root;
        this.projectDir = projectDir;
        this.channelName = name;
        this.pluginDir = this.projectDir + "/.ansplugin";
        this.sourceName = null;
        this.FRAME_PERIOD = new SimpleIntegerProperty();
        this.backgroundTasks = Executors.newCachedThreadPool();

        // Logic to load the knowSources
        Path pluginFolder = Paths.get(this.pluginDir);
        try {
            if (!Files.exists(pluginFolder)) Files.createDirectory(pluginFolder);
        }  catch (IOException e){
            e.printStackTrace();
        }
        String knownSourcesPath = this.pluginDir + "/knownSources.json";
        if (Files.exists(Path.of(knownSourcesPath))) {
            try {
                String rawFile = new String(Files.readAllBytes(Path.of(knownSourcesPath)));
                this.knownSources = new JSONArray(rawFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.knownSources = new JSONArray();
            try {
                Files.createFile(Path.of(knownSourcesPath));
                Files.write(Path.of(knownSourcesPath), Collections.singleton(knownSources.toString(4)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean canOpenSource(String path_video) {
        try {
            if(path_video !="")
                this.videoCapture.open(path_video);
        } catch (Exception e){
            e.printStackTrace();
        }
        return this.videoCapture.isOpened();
    }


    public void openSource(String path_signal, String path_video) {
        if (path_video == null) throw new RuntimeException("addVideoSource");
        if (this.videoCapture.isOpened()) {
            this.root.fireEvent(
                    new DeleteSourceEvent(this.projectDir + "/" + this.sourceName, this.channelName)
            );
        }
        this.signal_controller = new SignalController(path_signal);
        this.videoCapture.open(path_video);
        if (!this.videoCapture.isOpened()) throw new RuntimeException("Cannot add source");
        if (!isSourceKnown(path_video)) {
            processNewSource(path_signal,path_video);
            return;
        }
        Task<Image[]> loadTask = loadSource(path_video);
        // Create dialog to show load bar
        VBox content = new VBox();
        content.setAlignment(Pos.CENTER);
        MFXProgressBar progressBar = new MFXProgressBar(0);
        content.getChildren().add(progressBar);
        Thread t = new Thread(loadTask);
        t.start();
        progressBar.progressProperty().bind(loadTask.progressProperty());
    }

    protected boolean isSourceKnown(String path) {
        for(Object file : this.knownSources) {
            if(!(file instanceof JSONObject)) return false;
            if(Objects.equals(((JSONObject) file).get("originalFileDir"), path)) return true;
        }
        return false;
    }

    protected Task<Image[]> loadSource(String path_signal) {
        this.sourceName = Paths.get(path_signal).getFileName().toString();
        this.videoCapture.open(this.pluginDir + "/" + sourceName);
        if(!this.videoCapture.isOpened()) return null;
        // Load FPS and frames logic
        this.FRAME_PERIOD.set((int) (1000 / this.videoCapture.get(CAP_PROP_FPS)));
        this.FRAMES = this.videoCapture.get(CAP_PROP_FRAME_COUNT);
        // Set the source length
        this.sourceLength = this.videoCapture.get(CAP_PROP_FRAME_COUNT) / this.videoCapture.get(CAP_PROP_FPS);

        // Return a Task instance with the first frame
        return new Task() {
            @Override
            protected Image[] call() {
                return new Image[0];
            }
        };
    }

    protected void processNewSource(String path_signal, String path_video) {
        VideoCapture localCapture = this.videoCapture;
        this.sourceName = Paths.get(path_signal).getFileName().toString();
        this.FRAME_PERIOD.set((int) (1000 / this.videoCapture.get(CAP_PROP_FPS)));
        this.FRAMES = localCapture.get(CAP_PROP_FRAME_COUNT);
        this.sourceLength = localCapture.get(CAP_PROP_FRAME_COUNT) / localCapture.get(CAP_PROP_FPS);

        // Build video writer to process the file frame by frame
        VideoWriter writer = new VideoWriter(
                this.pluginDir + "/" + this.sourceName,
                VideoWriter.fourcc('M','J','P','G'),
                localCapture.get(CAP_PROP_FPS),
                new Size(720, 480)
        );

        // Build processing dialog
        VBox content = new VBox();
        MFXProgressBar progressBar = new MFXProgressBar();
        content.setAlignment(Pos.CENTER);
        content.getChildren().add(progressBar);
        MFXGenericDialog dialogContent = MFXGenericDialogBuilder.build()
                .setContent(content)
                .setShowClose(false)
                .setShowAlwaysOnTop(false)
                .setShowMinimize(false)
                .setHeaderText("Procesando video...")
                .get();
        MFXStageDialog dialog = MFXGenericDialogBuilder.build(dialogContent)
                .toStageDialogBuilder()
                .initOwner(root.getScene().getWindow())
                .initModality(Modality.APPLICATION_MODAL)
                .setDraggable(false)
                .setTitle("Procesando video...")
                .setOwnerNode((BorderPane) root.getScene().getRoot())
                .setScrimOwner(true)
                .get();

        // Task to process the frames as a thread
        Task task = new Task() {
            @Override
            protected Object call() throws RuntimeException {
                System.out.println("Comenzamos a procesar la señal y el video");
                int i = 0;
                Mat frame = new Mat();
                Mat resizedFrame = new Mat();
                if(!writer.isOpened()) {
                    throw new RuntimeException("Not able to open the VideoWriter");
                }
                while(localCapture.read(frame)) {
                    Imgproc.resize(frame, resizedFrame, new Size(720, 480));
                    writer.write(resizedFrame);
                    updateProgress(i++, localCapture.get(CAP_PROP_FRAME_COUNT));
                }
                frame.release();
                resizedFrame.release();
                localCapture.release();
                writer.release();
                System.out.println("Terminó procesamiento de la señal y el video");
                openSource(path_signal,path_video);
                return null;
            }
        };
        task.progressProperty().addListener(
                (observable, oldValue, newValue) -> progressBar.setProgress((double) newValue)
        );
        task.setOnSucceeded((EventHandler<WorkerStateEvent>) event -> dialog.close());
        task.setOnFailed(event -> System.out.println("task failed"));
        this.backgroundTasks.submit(task);
        dialog.show();

        JSONObject newSource = new JSONObject();
        newSource.put("originalFileDir", path_signal);
        newSource.put("compressedFileDir", this.pluginDir + "/" + this.sourceName);
        this.knownSources.put(newSource);
        try {
            Files.write(
                    Paths.get(this.pluginDir + "/knownSources.json"),
                    Collections.singleton(this.knownSources.toString(4))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.root.fireEvent(
                new AddSourceEvent(this.projectDir + "/" + this.sourceName, this.channelName)
        );
    }

    public double getSourceLength() { return this.sourceLength; }

    public double getFRAMES() { return this.FRAMES; }

    public String getProjectDir() { return this.projectDir; }

    public String getSourceName() { return this.sourceName; }

    public int getFRAME_PERIOD() { return this.FRAME_PERIOD.get(); }

    public void shutdownBackgroundTasks() { this.backgroundTasks.shutdown(); }
}
