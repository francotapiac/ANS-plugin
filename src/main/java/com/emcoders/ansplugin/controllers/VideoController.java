package com.emcoders.ansplugin.controllers;


import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import java.lang.Thread;

import java.io.File;

public class VideoController {
    public MediaPlayer initialize_video(String path){
        if(path == "")
            return null;

        //Instantiating Media class
        Media media = new Media(new File(path).toURI().toString());
        try{
            Thread.sleep(10000);
            System.out.println(media.getDuration());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println((new File(path).toURI().toString()));
        //Instantiating MediaPlayer class
        MediaPlayer mediaPlayer = new MediaPlayer(media);

        return mediaPlayer;
    }
}
