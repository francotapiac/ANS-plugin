package com.emcoders.ansplugin;

import com.emcoders.ansplugin.controllers.SignalController;
import com.emcoders.ansplugin.models.Alert;
import com.emcoders.ansplugin.models.Emotion;
import com.emcoders.ansplugin.models.Feature;
import com.emcoders.ansplugin.models.Signal;
import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {

    @Test
    void processCardiacSignal() {

        //Creando señal
        SignalController signalController = new SignalController();
        Signal signal = new Signal();
        signalController.setSignal(signal);
        List<Pair<Alert, Feature>> time_line = new ArrayList<>();

        //Creando línea de tiempo
        List<Pair<String,Float>> param = new ArrayList<>();
        param.add(new Pair("param",3.0));

        //Elemento 1
        Emotion emotion = new Emotion("Sorpresa");
        Alert alert = new Alert(1.0f,"",emotion,3.1f,3.3f);
        Feature feature = new Feature(param, param, 3.1f,3.3f );
        time_line.add(new Pair<>(alert,feature));

        //Elemento 2
        Emotion emotion2 = new Emotion("Enojo");
        Alert alert2 = new Alert(1.0f,"",emotion2,3.3f,3.5f);
        Feature feature2 = new Feature(param, param, 3.3f,3.5f );
        time_line.add(new Pair<>(alert2,feature2));

        //Guardando linea de tiempo en controllador
        signalController.getSignal().setTime_line(time_line);

        //Calculando largo de línea de tiempo
        signalController.getSignal().calculate_length_signal(time_line);

        //Creando puntos fci
        List<Double> fci = new ArrayList<>();
        fci.add(71.0);
        fci.add(73.12);
        fci.add(75.1);
        fci.add(72.3);
        signalController.getSignal().setFci(fci);

        //Obteniendo puntos máximos y minimos
        signalController.getSignal().calculate_min_max_fci();
        HelloController helloController = new HelloController();
        boolean value = helloController.processSignal(true);
        assertEquals(true, value);


    }

    @Test
    void loadingPlugin(){
        HelloController helloController = new HelloController();
        boolean value = helloController.loadingPlugin(true);
        assertEquals(true, value);

    }
}