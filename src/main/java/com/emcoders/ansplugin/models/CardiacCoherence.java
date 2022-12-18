package com.emcoders.ansplugin.models;

public class CardiacCoherence {

    public String id;
    public float ratio_coherence;
    public String scale;
    public Emotion emotion;
    public String description;
    public float start_time;
    public float end_time;

    public CardiacCoherence(float ratio_coherence, String scale, Emotion emotion, float start_time, float end_time){
        this.ratio_coherence = ratio_coherence;
        this.emotion = emotion;
        this.description = create_description(this.ratio_coherence);
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String create_description(float ratio_coherence) {
        if (ratio_coherence < 0.5)
            return "Incoherencia cardíaca";
        else if (ratio_coherence >= 0.5 && ratio_coherence < 1.0)
            return "Baja coherencia cardíaca";
        else
            return "Coherencia cardíaca";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRatio_coherence() {
        return ratio_coherence;
    }

    public void setRatio_coherence(float ratio_coherence) {
        this.ratio_coherence = ratio_coherence;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public Emotion getEmotion() {
        return emotion;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getStart_time() {
        return start_time;
    }

    public void setStart_time(float start_time) {
        this.start_time = start_time;
    }

    public float getEnd_time() {
        return end_time;
    }

    public void setEnd_time(float end_time) {
        this.end_time = end_time;
    }
}
