package com.emcoders.ansplugin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SegmentSignalTest {

    @Test
    void getId() {
        SegmentSignal segmentSignal = new SegmentSignal();
        segmentSignal.setId("1");
        assertEquals("1", segmentSignal.getId());
    }

    @Test
    void setId() {
    }

    @Test
    void getInitial_time() {
        SegmentSignal segmentSignal = new SegmentSignal();
        segmentSignal.setInitial_time(3.4f);
        assertEquals(3.4f, segmentSignal.getInitial_time());
    }

    @Test
    void setInitial_time() {
    }

    @Test
    void getFinal_time() {
        SegmentSignal segmentSignal = new SegmentSignal();
        segmentSignal.setFinal_time(3.4f);
        assertEquals(3.4f, segmentSignal.getFinal_time());
    }

    @Test
    void setFinal_time() {
    }

    @Test
    void getEmotion() {
        SegmentSignal segmentSignal = new SegmentSignal();
        segmentSignal.setEmotion("Sorpresa");
        assertEquals("Sorpresa", segmentSignal.getEmotion());
    }

    @Test
    void setEmotion() {
    }

    @Test
    void getCardiac_coherence() {
        SegmentSignal segmentSignal = new SegmentSignal();
        segmentSignal.setCardiac_coherence("Alta");
        assertEquals("Alta", segmentSignal.getCardiac_coherence());
    }

    @Test
    void setCardiac_coherence() {
    }

    @Test
    void getAlert() {
    }

    @Test
    void setAlert() {
    }

    @Test
    void getVlf() {
    }

    @Test
    void setVlf() {
    }

    @Test
    void getLf() {
    }

    @Test
    void setLf() {
    }

    @Test
    void getHf() {
    }

    @Test
    void setHf() {
    }

    @Test
    void getLf_hf() {
    }

    @Test
    void setLf_hf() {
    }

    @Test
    void getFft_total() {
    }

    @Test
    void setFft_total() {
    }

    @Test
    void getHr_mean() {
    }

    @Test
    void setHr_mean() {
    }

    @Test
    void getHr_min() {
    }

    @Test
    void setHr_min() {
    }

    @Test
    void getHr_max() {
    }

    @Test
    void setHr_max() {
    }

    @Test
    void getSdnn() {
    }

    @Test
    void setSdnn() {
    }

    @Test
    void getRmssd() {
    }

    @Test
    void setRmssd() {
    }

    @Test
    void getSdsd() {
    }

    @Test
    void setSdsd() {
    }

    @Test
    void getPnn50() {
    }

    @Test
    void setPnn50() {
    }

    @Test
    void getRatio_coherence() {
    }

    @Test
    void setRatio_coherence() {
    }
}