package com.emcoders.ansplugin.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertTest {

    @Test
    void create_type_alert() {
        Alert alert = new Alert(1.0f,"",null,3.1f,3.3f);
         int type_alert = alert.create_type_alert(alert.ratio_coherence);
         assertEquals(0,type_alert);
    }

    @Test
    void create_alert() {
        Alert alert = new Alert(1.0f,"",null,3.1f,3.3f);
        String alert_name = alert.create_alert(alert.ratio_coherence);
        assertEquals("",alert_name);
    }

    @Test
    void create_name_alert() {
        Alert alert = new Alert(1.0f,"",null,3.1f,3.3f);
        String alert_name = alert.create_name_alert(1);
        assertEquals("Alerta " + "-" + alert.getStart_time() + "-" + alert.getEnd_time(),alert_name);
    }

    @Test
    void calculate_duration() {
        Alert alert = new Alert(1.0f,"",null,3.1f,3.3f);
        float duration = alert.calculate_duration(3.1f, 3.3f);
        assertEquals(0.20000005f,duration);
    }

}