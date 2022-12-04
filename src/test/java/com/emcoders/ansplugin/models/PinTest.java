package com.emcoders.ansplugin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PinTest {

    @Test
    void calculate_duration() {
        Pin pin = new Pin(3.1f, 3.3f,0.2f );
        float duration = pin.calculate_duration(3.4f, 3.1f);
        assertEquals(0,duration);
    }

    @Test
    void getId() {
    }

    @Test
    void setId() {
    }

    @Test
    void getStart_time() {
    }

    @Test
    void setStart_time() {
    }

    @Test
    void getEnd_time() {
    }

    @Test
    void setEnd_time() {
    }

    @Test
    void getDuration() {
    }

    @Test
    void setDuration() {
    }

    @Test
    void getChannel() {
    }

    @Test
    void setChannel() {
    }

    @Test
    void getContent() {
    }

    @Test
    void setContent() {
    }
}