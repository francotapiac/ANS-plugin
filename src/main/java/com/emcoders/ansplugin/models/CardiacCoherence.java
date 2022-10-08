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

    public String create_description(float ratio_coherence){
        if(ratio_coherence < 0.5)
            return "Incoherencia cardíaca";
        else if(ratio_coherence >= 0.5 && ratio_coherence < 1.0)
            return "Baja coherencia cardíaca";
        else if(ratio_coherence >= 0.1 && ratio_coherence < 2.0)
            return "Coherencia cardíaca";
        else
            return "Alta coherencia cardíaca";
    }


}
