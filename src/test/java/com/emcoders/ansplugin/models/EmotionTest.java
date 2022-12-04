package com.emcoders.ansplugin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmotionTest {

    @Test
    void create_description() {
        Emotion emotion = new Emotion("Sorpresa");
        String description = emotion.create_description(emotion.getName());
        assertEquals("Una emocion", description);
    }

    @Test
    void create_path() {
        Emotion emotion1 = new Emotion("Sorpresa");
        String path = emotion1.create_path(emotion1.getName());
        assertEquals("images/sorpresa.png", path);

        Emotion emotion2 = new Emotion("Miedo");
        String path2 = emotion2.create_path(emotion2.getName());
        assertEquals("images/miedo.png", path2);
    }
    @Test
    void getId(){
        Emotion emotion = new Emotion("Surprise");
        String id = emotion.getId();
        assertEquals(null,id);
    }

    @Test
    void setId(){
        Emotion emotion = new Emotion("Sorpresa");
        emotion.setId("1");
        assertEquals("1",emotion.getId());
    }
}