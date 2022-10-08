package com.emcoders.ansplugin.models;
import javafx.util.Pair;

import java.util.List;

public class Feature {
    private String id;
    private List<Pair<String, Float>> times_feature;
    private List<Pair<String, Float>> frequency_feature;
    private float start_time;
    private float end_time;

    public Feature(List<Pair<String, Float>> times_feature, List<Pair<String, Float>> frequency_feature, float start_time, float end_time){
        this.times_feature = times_feature;
        this.frequency_feature = frequency_feature;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getId() {
        return id;
    }

    public List<Pair<String, Float>> getTimes_feature() {
        return times_feature;
    }

    public List<Pair<String, Float>> getFrequency_feature() {
        return frequency_feature;
    }

    public float getStart_time() {
        return start_time;
    }

    public float getEnd_time() {
        return end_time;
    }


}
