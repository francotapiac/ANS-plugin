package com.emcoders.ansplugin.models;

public class Project {
    private String name_project;
    private String path_project;
    private Double sampling_rate;
    private Double window_sampling;
    private Double shif_sampling;

    public Project(String name_project, String path_project, Double sampling_rate, Double window_sampling, Double shif_sampling){
        this.name_project = name_project;
        this.path_project = path_project;
        this.sampling_rate = sampling_rate;
        this.window_sampling = window_sampling;
        this.shif_sampling = shif_sampling;
    }

    public String getName_project() {
        return name_project;
    }

    public void setName_project(String name_project) {
        this.name_project = name_project;
    }

    public String getPath_project() {
        return path_project;
    }

    public void setPath_project(String path_project) {
        this.path_project = path_project;
    }

    public Double getSampling_rate() {
        return sampling_rate;
    }

    public void setSampling_rate(Double sampling_rate) {
        this.sampling_rate = sampling_rate;
    }

    public Double getWindow_sampling() {
        return window_sampling;
    }

    public void setWindow_sampling(Double window_sampling) {
        this.window_sampling = window_sampling;
    }

    public Double getShif_sampling() {
        return shif_sampling;
    }

    public void setShif_sampling(Double shif_sampling) {
        this.shif_sampling = shif_sampling;
    }
}
