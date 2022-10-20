package com.emcoders.ansplugin.models;

public class SegmentSignal {

    private String id;
    private Float initial_time;
    private Float final_time;
    private String emotion;
    private String cardiac_coherence;
    private String alert;

    public SegmentSignal(String id, Float initial_time, Float final_time, String emotion, String cardiac_coherence, String alert){
        this.id = id;
        this.initial_time = initial_time;
        this.final_time = final_time;
        this.emotion = emotion;
        this.cardiac_coherence = cardiac_coherence;
        this.alert = alert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Float getInitial_time() {
        return initial_time;
    }

    public void setInitial_time(Float initial_time) {
        this.initial_time = initial_time;
    }

    public Float getFinal_time() {
        return final_time;
    }

    public void setFinal_time(Float final_time) {
        this.final_time = final_time;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public String getCardiac_coherence() {
        return cardiac_coherence;
    }

    public void setCardiac_coherence(String cardiac_coherence) {
        this.cardiac_coherence = cardiac_coherence;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }
}
