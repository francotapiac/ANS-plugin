package com.emcoders.ansplugin.models;

public class Alert extends CardiacCoherence{

    private String id = "";
    private String text_alert;
    private Integer type_alert;
    private String name_alert;
    private float duration;

    public Alert(float ratio_coherence, String scale, Emotion emotion, float start_time, float end_time){
        super(ratio_coherence, scale, emotion, start_time,end_time);
        this.type_alert = create_type_alert(ratio_coherence);
        this.name_alert = create_name_alert(this.type_alert);
        this.duration = calculate_duration(start_time, end_time);
        this.text_alert = create_alert(ratio_coherence);
    };

    /*
    Calcula el tipo de alerta según el ratio de coherencia
     */
    public Integer create_type_alert(float ratio_coherence){
        if(ratio_coherence < 1.0)
            return 1;
        return 0;
    }

    public String create_alert(float ratio_coherence){
        if(ratio_coherence < 1.0)
            return "Alerta Automática";
        return "";
    }

    public String create_name_alert(Integer type_alert){
        if(type_alert == 1)
            return "Alerta " + this.id + "-" + this.start_time + "-" + this.end_time;
        return "Sin alerta " + this.id + "-" + this.start_time + "-" + this.end_time;
    }
    public float calculate_duration(float start_time, float end_time){
        float duration = end_time - start_time;
        if(duration > 0)
            return duration;
        else{
            return  0;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getType_alert() {
        return type_alert;
    }

    public void setType_alert(Integer type_alert) {
        this.type_alert = type_alert;
    }

    public String getName_alert() {
        return name_alert;
    }

    public void setName_alert(String name_alert) {
        this.name_alert = name_alert;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getText_alert() {
        return text_alert;
    }

    public void setText_alert(String text_alert) {
        this.text_alert = text_alert;
    }
}
