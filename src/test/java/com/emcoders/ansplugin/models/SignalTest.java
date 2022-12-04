package com.emcoders.ansplugin.models;

import javafx.util.Pair;
import org.apache.poi.wp.usermodel.Paragraph;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignalTest {


    @Test
    void get_json_time_line() {
    }

    @Test
    void create_time_line_signal() {
    }

    @Test
    void json_object_to_pair_float() {
    }

    @Test
    void calculate_length_signal() {
        List<Pair<String,Float>> param = new ArrayList<>();
        param.add(new Pair("param",3.0));
        Alert alert = new Alert(1.0f,"",null,3.1f,3.3f);
        Feature feature = new Feature(param, param, 3.1f,3.3f );
        Signal signal = new Signal();
        List<Pair<Alert, Feature>> time_line = new ArrayList<>();
        time_line.add(new Pair<>(alert,feature));

        signal.setTime_line(time_line);
        signal.calculate_length_signal(signal.getTime_line());
        assertEquals(3.1f,signal.getStart_time_signal());
        assertEquals(3.3f,signal.getEnd_time_signal());
    }

    @Test
    void create_signal_time() {
    }

    @Test
    void append_emotions_signal() {
        Signal signal = new Signal();
        signal.append_emotions_signal("sorpresa");
        signal.append_emotions_signal("enojo");
        signal.append_emotions_signal("enojo");
        assertEquals(2,signal.getCant_emotions_signal().get(1));
    }

    @Test
    void get_general_data() {
    }

    @Test
    void set_predominant_emotion_name() {
        Signal signal = new Signal();
        String emotion = signal.set_predominant_emotion_name("surprise");
        assertEquals("Sorpresa",emotion);
    }

    @Test
    void format_number() {
        Signal signal = new Signal();
        Float format = signal.format_number(4.51f);
        assertEquals(4.5100f,format);
    }

    @Test
    void format_number_double() {
        Signal signal = new Signal();
        Double format = signal.format_number_double(4.51);
        assertEquals(4.5100,format);
    }

    @Test
    void calculate_min_max_fci() {
        Signal signal = new Signal();
        List<Double> points = new ArrayList<>();
        points.add(2.0);
        points.add(1.0);
        points.add(4.0);
        points.add(3.0);
        signal.setFci(points);

        signal.calculate_min_max_fci();
        assertEquals(new Pair<>(1.0,4.0), signal.getMin_max_fci_point());


    }
}