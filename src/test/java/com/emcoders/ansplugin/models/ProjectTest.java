package com.emcoders.ansplugin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProjectTest {

    @Test
    void getName_project() {
        Project project = new Project("proyecto","ruta",1000.0,20.0,1.0);
        assertEquals("proyecto", project.getName_project());
    }

    @Test
    void setName_project() {
    }

    @Test
    void getPath_project() {
        Project project = new Project("proyecto","ruta",1000.0,20.0,1.0);
        assertEquals("ruta", project.getPath_project());
    }

    @Test
    void setPath_project() {
    }

    @Test
    void getSampling_rate() {
        Project project = new Project("proyecto","ruta",1000.0,20.0,1.0);
        assertEquals(1000.0, project.getSampling_rate());
    }

    @Test
    void setSampling_rate() {
    }

    @Test
    void getWindow_sampling() {
        Project project = new Project("proyecto","ruta",1000.0,20.0,1.0);
        assertEquals(20.0, project.getWindow_sampling());
    }

    @Test
    void setWindow_sampling() {
    }

    @Test
    void getShif_sampling() {
        Project project = new Project("proyecto","ruta",1000.0,20.0,1.0);
        assertEquals(1.0, project.getShif_sampling());
    }

    @Test
    void setShif_sampling() {
    }
}