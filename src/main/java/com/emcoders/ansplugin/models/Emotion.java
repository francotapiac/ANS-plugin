package com.emcoders.ansplugin.models;

public class Emotion {

    private String id;
    private String name;
    private String description;
    private float arousal;
    private float valence;
    private float dominance;
    private String path_image_emotion;

    public Emotion(String name){
        this.name = name;
        this.description = create_description(this.name);

    }

    public String create_description(String emotion){
        return "Una emocion";
    }

    public String create_path(String emotion) {
        if(emotion.equals("Sorpresa"))
            return "images/sorpresa.png";

        else if (emotion.equals("Felicidad")) {
            return "images/felicidad.png";
        }
        else if(emotion.equals("Miedo")){
            return "images/miedo.png";
        }
        else if(emotion.equals("Enojo")){
            return "images/enojo.png";
        }
        else if(emotion.equals("Tristeza")){
            return "images/tristeza.png";
        }
        return "";
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

    public String getPath_image_emotion() {
        return path_image_emotion;
    }

    public void setPath_image_emotion(String path_image_emotion) {
        this.path_image_emotion = path_image_emotion;
    }
}
