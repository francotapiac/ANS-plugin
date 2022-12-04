package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.models.Alert;
import com.emcoders.ansplugin.models.Feature;
import com.emcoders.ansplugin.models.Signal;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignalControllerTest {

    @Test
    void create_signal() {
    }

    @Test
    void create_signal_excel() {
    }

    @Test
    void create_time_line_excel() {
    }

    @Test
    void create_signal_time() {
    }

    @Test
    void get_sourceLength() {
        SignalController signalController = new SignalController();
        Signal signal = new Signal();
        signalController.setSignal(signal);

        List<Pair<String, Float>> param = new ArrayList<>();
        param.add(new Pair("param", 3.0));
        Alert alert = new Alert(1.0f, "", null, 3.1f, 3.3f);
        Feature feature = new Feature(param, param, 3.1f, 3.3f);
        List<Pair<Alert, Feature>> time_line = new ArrayList<>();
        time_line.add(new Pair<>(alert, feature));
        signalController.getSignal().setTime_line(time_line);
        signalController.getSignal().calculate_length_signal(signal.getTime_line());
        Double length = signalController.get_sourceLength();
        Double test = 3.299999952316284;
        assertEquals(test, length);

    }

}