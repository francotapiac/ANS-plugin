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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
