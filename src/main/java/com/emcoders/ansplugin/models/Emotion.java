package com.emcoders.ansplugin.models;

public class Emotion {

    private String id;
    private String name;
    private String description;
    private float arousal;
    private float valence;
    private float dominance;

    public Emotion(String name){
        this.name = name;
        this.description = create_description(this.name);

    }

    public String create_description(String emotion){
        return "Una emocion";
    }
}
