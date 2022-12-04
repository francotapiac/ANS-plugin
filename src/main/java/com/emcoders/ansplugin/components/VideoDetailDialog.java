package com.emcoders.ansplugin.components;
import com.emcoders.ansplugin.HelloApplication;
import com.emcoders.scansembox.Utils.TimelineElement;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Window;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;

import static org.opencv.videoio.Videoio.*;

public class VideoDetailDialog {
    @FXML
    MFXButton previousFrameBtn;
    @FXML
    MFXButton nextFrameBtn;
    @FXML
    MFXButton playBtn;
    @FXML
    Label frameLabel;
    @FXML
    ImageView imgView;

    TimelineElement timelineElement;
    VideoCapture videoCapture;
    private final IntegerProperty FRAME_PERIOD;
    double totalFrames;
    double currentFrame;
    double lowerFrameLimit;
    double higherFrameLimit;

    public VideoDetailDialog() {
        try {
            this.videoCapture = new VideoCapture();
            this.FRAME_PERIOD = new SimpleIntegerProperty();
            this.timelineElement = null;
            this.currentFrame = 1.0;
            this.totalFrames = 0.0;
            this.lowerFrameLimit = 0.0;
            this.higherFrameLimit = 0.0;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HelloApplication.class.getResource("videoDetailDialogPane.fxml"));
            loader.setController(this);
            DialogPane dialogPane = loader.load();

            // Component-method mapping
            this.nextFrameBtn.setOnAction(event -> Platform.runLater(this::getNextFrame));
            this.previousFrameBtn.setOnAction(event -> Platform.runLater(this::getPreviousFrame));
            this.playBtn.setOnAction(event -> Platform.runLater(this::playByFrame));

           // initModality(Modality.APPLICATION_MODAL);
            //setResizable(true);
            //setDialogPane(dialogPane);

            // Allow the user to close the dialog pressing the "X" button
            //Window window = this.getDialogPane().getScene().getWindow();
            //window.setOnCloseRequest(e -> this.hide());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadVideo(String path) {
        this.videoCapture.open(path);
        this.totalFrames = this.videoCapture.get(CAP_PROP_FRAME_COUNT);
        this.videoCapture.set(CAP_PROP_POS_FRAMES, this.currentFrame);
        this.FRAME_PERIOD.set((int) (1000 / this.videoCapture.get(CAP_PROP_FPS)));

        this.reloadComponents();

        Mat frame = grabFrame();
        if(frame == null || frame.empty()) return;
        //Image imageToShow = Utils.mat2Image(frame);
        //updateImageView(imageToShow);

        frame.release();
    }

    private void reloadComponents() {
        int frameLowerLimit = (int) this.currentFrame;
        int frameHigherLimit = this.higherFrameLimit == 0.0 ? (int) this.totalFrames : (int) this.higherFrameLimit;
        this.frameLabel.setText(String.format("%d / %d", frameLowerLimit, frameHigherLimit));

        if (frameLowerLimit == (int) this.currentFrame) this.previousFrameBtn.setDisable(true);
        else if (frameLowerLimit < (int) this.currentFrame) this.previousFrameBtn.setDisable(false);

        if (frameHigherLimit == (int) this.currentFrame) this.nextFrameBtn.setDisable(true);
        else if (frameHigherLimit > (int) this.currentFrame) this.nextFrameBtn.setDisable(false);
    }

    public void reloadFrame() {
        this.videoCapture.set(CAP_PROP_POS_FRAMES, this.currentFrame);
        getNextFrame();
    }

    private void updateImageView(Image image) {
        //Utils.onFXThread(this.imgView.imageProperty(), image);
    }

    private Mat grabFrame() {
        Mat frame = new Mat();
        if (!this.videoCapture.isOpened()) {
            System.err.println("VideoCapture not enable");
            return null;
        }
        if (!this.videoCapture.read(frame)) {
            System.err.println("VideoCapture error");
        }
        return frame;
    }

    private void getNextFrame() {
        Mat frame = grabFrame();
        if(frame == null || frame.empty()) return;
        //Image imageToShow = Utils.mat2Image(frame);
      //  updateImageView(imageToShow);
        frame.release();

        this.reloadComponents();
        this.currentFrame++;
    }

    private void getPreviousFrame() {
        this.videoCapture.set(CAP_PROP_POS_FRAMES, Math.max(this.videoCapture.get(CAP_PROP_POS_FRAMES) - 2, 0));
        Mat frame = grabFrame();
        if(frame == null || frame.empty()) return;
        //Image imageToShow = Utils.mat2Image(frame);
        //updateImageView(imageToShow);
        //frame.release();

        this.reloadComponents();
        this.currentFrame--;
    }

    private void playByFrame() {
        System.out.println("Hola");
    }

    public void setTimelineElement(TimelineElement timelineElement) {
        this.timelineElement = timelineElement;
        String path = this.timelineElement.getPathToSource();
        loadVideo(path);
    }

    public void setFramesInterval(double lowerFrame, double higherFrame) {
        this.lowerFrameLimit = Math.ceil(lowerFrame);
        this.higherFrameLimit = Math.ceil(higherFrame);
        this.currentFrame = this.lowerFrameLimit;
    }

    public double getFrameRate() { return this.videoCapture.get(CAP_PROP_FPS); }
}
