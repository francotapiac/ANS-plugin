package com.emcoders.ansplugin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardiacCoherenceTest {

    @Test
    void create_description() {
        CardiacCoherence cardiacCoherence = new CardiacCoherence(1.0f, "", null, 3.1f,3.3f);
        String description = cardiacCoherence.create_description(1.0f);
        assertEquals("Coherencia cardíaca", description);
    }
}