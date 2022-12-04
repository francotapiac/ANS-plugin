package com.emcoders.ansplugin.controllers;

import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VideoControllerTest {

    @Test
    void initialize_video() {
        VideoController videoController = new VideoController();
        MediaPlayer mediaView = videoController.initialize_video("");
        assertEquals(null, mediaView);
    }
}