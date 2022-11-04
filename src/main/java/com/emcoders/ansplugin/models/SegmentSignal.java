package com.emcoders.ansplugin.models;

import javafx.scene.control.TableColumn;

public class SegmentSignal {

    private String id;
    private Float initial_time;
    private Float final_time;
    private String emotion;
    private String cardiac_coherence;
    private String alert;
    private  Float vlf;
    private Float lf;
    private Float hf;
    private Float lf_hf;
    private Float fft_total;
    private Float hr_mean;
    private Float hr_min;
    private Float hr_max;
    private Float sdnn;
    private Float rmssd;
    private Float sdsd;
    private Float pnn50;
    private Float ratio_coherence;

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

    public Float getVlf() {
        return vlf;
    }

    public void setVlf(Float vlf) {
        this.vlf = vlf;
    }

    public Float getLf() {
        return lf;
    }

    public void setLf(Float lf) {
        this.lf = lf;
    }

    public Float getHf() {
        return hf;
    }

    public void setHf(Float hf) {
        this.hf = hf;
    }

    public Float getLf_hf() {
        return lf_hf;
    }

    public void setLf_hf(Float lf_hf) {
        this.lf_hf = lf_hf;
    }

    public Float getFft_total() {
        return fft_total;
    }

    public void setFft_total(Float fft_total) {
        this.fft_total = fft_total;
    }

    public Float getHr_mean() {
        return hr_mean;
    }

    public void setHr_mean(Float hr_mean) {
        this.hr_mean = hr_mean;
    }

    public Float getHr_min() {
        return hr_min;
    }

    public void setHr_min(Float hr_min) {
        this.hr_min = hr_min;
    }

    public Float getHr_max() {
        return hr_max;
    }

    public void setHr_max(Float hr_max) {
        this.hr_max = hr_max;
    }

    public Float getSdnn() {
        return sdnn;
    }

    public void setSdnn(Float sdnn) {
        this.sdnn = sdnn;
    }

    public Float getRmssd() {
        return rmssd;
    }

    public void setRmssd(Float rmssd) {
        this.rmssd = rmssd;
    }

    public Float getSdsd() {
        return sdsd;
    }

    public void setSdsd(Float sdsd) {
        this.sdsd = sdsd;
    }

    public Float getPnn50() {
        return pnn50;
    }

    public void setPnn50(Float pnn50) {
        this.pnn50 = pnn50;
    }

    public Float getRatio_coherence() {
        return ratio_coherence;
    }

    public void setRatio_coherence(Float ratio_coherence) {
        this.ratio_coherence = ratio_coherence;
    }
}
