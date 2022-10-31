package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.models.Emotion;
import com.emcoders.ansplugin.models.Signal;

import java.util.ArrayList;
import java.util.List;

public class SignalController {
    Signal signal;
    Float ratio_coherence;
    String emotion;
    Integer type_alert;
    Float partial_start_time;
    Float partial_end_time;
    String cardiac_coherence_description;
    String path_image_emotion;
    private List<Double> signal_points_fci;


    public SignalController(String path){

        create_signal(path);
    }

    public void create_signal(String path){
        this.signal = new Signal(path);
        create_points_signal_fci();
    }



    public Double get_sourceLength(){
        return this.signal.getEnd_time_signal();
    }


    public void get_particular_data(Float time_comparate){
        System.out.println(time_comparate);
        for(int i = 0; i < signal.getTime_line().size(); i++){
            System.out.println(i);
            Float parcial_start_time = signal.getTime_line().get(i).getKey().getStart_time()*1000;
            Float parcial_end_time = signal.getTime_line().get(i).getKey().getEnd_time()*1000;
            if(time_comparate >= parcial_start_time && time_comparate < parcial_end_time){
                this.partial_start_time = parcial_start_time;
                this.partial_end_time = parcial_end_time;
                this.ratio_coherence = signal.getTime_line().get(i).getKey().getRatio_coherence();
                this.emotion = signal.getTime_line().get(i).getKey().getEmotion().getName();
                this.type_alert = signal.getTime_line().get(i).getKey().getType_alert();
                this.cardiac_coherence_description = signal.getTime_line().get(i).getKey().getDescription();
                Emotion emotion_aux = new Emotion("");
                this.path_image_emotion = emotion_aux.create_path(this.emotion);
                break;
            }
        }
    }

    // Obtiene la señal original según los puntos del FCI. Esto, debido a que se busca acotar la señal mostrada
    public void create_points_signal_fci(){
        signal_points_fci = new ArrayList<>();
        List<Double> times_fci = signal.getTimes_fci();
        for(Double time : times_fci){
            Integer index_times_points = signal.getTimes_points().indexOf(time);
            System.out.println("Index: " + index_times_points);
            signal_points_fci.add(signal.getSignal_points().get(index_times_points));

        }
    }

    public List<Double> get_pointsSignal(){
        return this.signal.getPoints_signal();
    }

    public List<Double> get_times(){
        return this.signal.getTimes_fci();
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

    public Float getPartial_start_time() {
        return partial_start_time;
    }

    public Float getPartial_end_time() {
        return partial_end_time;
    }

    public String getCardiac_coherence_description() {
        return cardiac_coherence_description;
    }

    public String getPath_image_emotion() {return path_image_emotion;}

    public List<Double> getSignal_points_fci() {
        return signal_points_fci;
    }
}
