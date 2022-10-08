package com.emcoders.ansplugin.models;

public class Pin {
    private String id;
    private float start_time;
    private float end_time;
    private float duration;

    public Pin(float start_time, float end_time, float duration){
        this.start_time = start_time;
        this.end_time = end_time;
        this.duration = calculate_duration(start_time,end_time);
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

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String channel;
    private String content;


}
