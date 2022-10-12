package com.emcoders.ansplugin;

import com.emcoders.ansplugin.components.TagDialog;
import com.emcoders.ansplugin.services.SourceService;
import com.emcoders.ansplugin.utils.Utils;
import com.emcoders.scansembox.Events.AddTagEvent;
import com.emcoders.scansembox.Events.UpdateTagEvent;
import com.emcoders.scansembox.Models.CanalModel;
import com.emcoders.scansembox.Utils.TimelineElement;
import com.emcoders.scansembox.Utils.TimelineTag;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import io.github.palexdev.materialfx.controls.MFXButton;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.PlayBin;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainController extends CanalModel {

    // FXML Elements
    @FXML
    Pane root;
    @FXML
    MFXButton processingBtn;
    @FXML
    MFXButton exportDetailsBtn;
    @FXML
    MFXButton addTagBtn;
    @FXML
    MFXButton checkTagBtn;
    @FXML
    ImageView imgView;
    @FXML
    TagDialog tagDialog;
    @FXML
    //VideoDetailDialog videoDetailDialog;

    // Instance state //
    private volatile boolean isLoaded = false;
    // Timeline element generated
    private TimelineElement timelineElement;
    // Model to follow the business logic
    private SignalController signalController;
    // Bool to know if timelineElement was selected
    private BooleanProperty timelineSelected = null;
    private ArrayList<BooleanProperty> tagsSelected = new ArrayList<>();
    private PlayBin playBin;
    // Services //
    private SourceService sourceService;

    @Override
    public Pane getLayout() {
        /*
          Initial method. Which load the layout and perform the plugin init
          @return Pane
         */
        if (this.isLoaded) return null;
        this.isLoaded = true;

        setName("ANS Plug-in");
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("layout.fxml"));
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
        SignalController signal = new SignalController("http://127.0.0.1:5000");


    }


    @Override
    public void play() {
        /*
         * Play method for the video
         * @return void
         */
        this.playBin.play();
    }

    @Override
    public void pause() {
        /*
          Pause method for the video
          @return void
         */
        this.playBin.pause();
    }

    @Override
    public void seek(double millis) {
        /*
          Seek method for the video
          @param double milliseconds to seek
          @return void
         */
        this.playBin.seek((long) millis, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean canOpen(String path) {
        /*
          Method created to check if plugin can open a file
          @param string absolute file path as string
          @return boolean
         */
        //return this.sourceService.canOpenSourceVideo(path);
        return true;
    }

    @Override
    public TimelineElement openSource(String path){
        /*
          Method to load a file using addVideoSource
          @param string absolute file path as string
         * @return TimelineElement
         */
        this.sourceService.openSource(path,"");
        this.timelineElement = new TimelineElement(
                path,
                this.sourceService.getSourceLength(),
                this.getName(),
                javafx.scene.paint.Color.valueOf("#089FD3")
        );
        //this.videoDetailDialog.setTimelineElement(this.timelineElement);
        this.playBin.stop();
        this.playBin.setURI(
                Paths.get(this.sourceService.getProjectDir() + "/" + this.sourceService.getSourceName())
                        .toUri()
        );
        // TODO: Fix this logic, improve the initialization of this.timer
        play();
        pause();
        // Map events from the generated timelineElement
        this.timelineSelected = this.timelineElement.getTransparent().selectedProperty();
        this.timelineSelected.addListener((observable, oldValue, newValue) -> {
            processingBtn.setDisable(!newValue);
            exportDetailsBtn.setDisable(!newValue);
            checkTagBtn.setDisable(true);
            addTagBtn.setDisable(!newValue);
        });
        return this.timelineElement;
    }

    @Override
    public TimelineElement loadProjectSource(String path){
        /*
          Load source saved on the project settings
          @param string absolute file path as string
         * @return TimelineElement
         */
        System.out.println(path);
        return openSource(path);
    }

    @Override
    public void loadProjectTag(String source, String code, String desc, double initTimeInMS, double lengthInMS){
        /*
          Method to load old tag on the timeline
          @param string source path
         * @param string tag code
         * @param string description
         * @param double start millisecond
         * @param double millisecond length
         * @return void
         */
        TimelineTag timelineTag = new TimelineTag(
                this.timelineElement.getRectangle().getWidth() * initTimeInMS/ (this.sourceService.getSourceLength() * 1000),
                this.timelineElement.getRectangle().getWidth() * lengthInMS / (this.sourceService.getSourceLength() * 1000),
                code,
                desc,
                initTimeInMS,
                lengthInMS,
                Color.ORANGE,
                getName(),
                source
        );
        this.timelineElement.getChildren().add(timelineTag);
        this.getTags().add(timelineTag);
        this.tagsSelected.add(timelineTag.selectedProperty());
        this.tagsSelected.get(this.tagsSelected.size() - 1).addListener(
                (observable, oldValue, newValue) -> {
                    processingBtn.setDisable(newValue);
                    exportDetailsBtn.setDisable(true);
                    checkTagBtn.setDisable(!newValue);
                    addTagBtn.setDisable(newValue);
                }
        );
    }

    // TODO: Refactor to this method
    @Override
    public void requestUpdate(String source, String code, String desc, double initTimeInMS, double lengthInMS){
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
            this.imgView.fireEvent(
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
                    )
            );
            finalTagToUpdate.setDescription(this.tagDialog.getDescText());
            finalTagToUpdate.setCode(this.tagDialog.getCodeText());
        });
    }

    @Override
    public void deleteTag(String source, String code, String desc, double initTimeInMS, double lengthInMS){
        /*
          Method to delete a timeline's tag
          @param string source path
         * @param string tag code
         * @param string description
         * @param double start millisecond
         * @param double millisecond length
         * @return void
         */
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

    // TODO: Move this method to the business layer
    public void addTag() {
        this.tagDialog.showAndWait();
        if(!this.tagDialog.isCompleted()) return;

        Platform.runLater(() -> {
            TimelineTag tag = new TimelineTag(
                    timelineElement.getSelectionX(),
                    timelineElement.getSelectionWidth(),
                    this.tagDialog.getCodeText(),
                    this.tagDialog.getDescText(),
                    timelineElement.getSelectionX() * (sourceService.getSourceLength() * 1000d)/ timelineElement.getRectangle().getWidth(),
                    timelineElement.getSelectionWidth() * (sourceService.getSourceLength() * 1000d) / timelineElement.getRectangle().getWidth(),
                    Color.ORANGE,
                    getName(),
                    Paths.get(this.sourceService.getProjectDir() + "/" + this.sourceService.getSourceName()).toAbsolutePath().toString()
            );
            this.getTags().add(tag);
            this.timelineElement.getChildren().add(tag);
            this.tagsSelected.add(tag.selectedProperty());
            this.tagsSelected.get(this.tagsSelected.size() - 1).addListener(
                    (observable, oldValue, newValue) -> {
                        processingBtn.setDisable(newValue);
                        exportDetailsBtn.setDisable(true);
                        checkTagBtn.setDisable(!newValue);
                        addTagBtn.setDisable(newValue);
                    }
            );
            this.root.fireEvent(
                    new AddTagEvent(
                            getName(),
                            sourceService.getProjectDir() + "/" + sourceService.getSourceName(),
                            this.tagDialog.getCodeText(),
                            this.tagDialog.getDescText(),
                            tag.getInitTimeInMS(),
                            tag.getLengthInMS()
                    )
            );
        });
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
        //double frameRate = this.videoDetailDialog.getFrameRate();
       // double lowerFrame = Utils.ms2FrameNumber(initTimeInMS, frameRate);
        //double higherFrame = Utils.ms2FrameNumber(initTimeInMS + lengthInMS, frameRate);
        //this.videoDetailDialog.setFramesInterval(lowerFrame, higherFrame);
        //this.videoDetailDialog.reloadFrame();
        //this.videoDetailDialog.showAndWait();
    }

    @Override
    public void shutdown(){
        /*
          Stop all the current processes running
          @return void
         */
        if (this.sourceService != null) this.sourceService.shutdownBackgroundTasks();
       // if (this.facialModel != null) this.facialModel.shutdownBackgroundTasks();
    }

    private void processingChange(Boolean processingValue) {
        if (processingValue) {
            System.out.println("Processing...");
            //this.loadingDialog.show();
            return;
        }
        System.out.println("Processed.");
     //   Window window = this.loadingDialog.getDialogPane().getScene().getWindow();
      //  window.hide();
    }

    private void processSource() {
        System.out.println("processSource");
        this.signalController = new SignalController(this.timelineElement.getPathToSource());
    }







}
