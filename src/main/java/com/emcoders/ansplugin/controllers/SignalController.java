package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.models.Alert;
import com.emcoders.ansplugin.models.Signal;
import com.emcoders.scansembox.Utils.TimelineTag;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class SignalController {
    Signal signal;
    Float ratio_coherence;
    String emotion;
    Integer type_alert;

    public SignalController(String path){

        create_signal(path);
    }

    public void create_signal(String path){
        this.signal = new Signal(path);
    }


    public Double get_sourceLength(){
        return this.signal.getEnd_time_signal();
    }


    public void get_particular_data(String time){
        Float time_comparate = Float.parseFloat(time);
        for(int i = 0; i < signal.getTime_line().size(); i++){
            Float parcial_start_time = signal.getTime_line().get(i).getKey().getStart_time();
            Float parcial_end_time = signal.getTime_line().get(i).getKey().getEnd_time();
            if(time_comparate >= parcial_start_time && time_comparate < parcial_end_time){
                ratio_coherence = signal.getTime_line().get(i).getKey().getRatio_coherence();
                emotion = signal.getTime_line().get(i).getKey().getEmotion().getName();
                type_alert = signal.getTime_line().get(i).getKey().getType_alert();
            }
        }
    }

    public List<Double> get_pointsSignal(){
        return this.signal.getPoints_signal();
    }

    public List<Double> get_times(){
        return this.signal.getTimes();
    }

    public Signal getSignal() {
        return signal;
    }

    public Float getRatio_coherence() {
        return ratio_coherence;
    }

    public String getEmotion() {
        return emotion;
    }

    public Integer getType_alert() {
        return type_alert;
    }
}
