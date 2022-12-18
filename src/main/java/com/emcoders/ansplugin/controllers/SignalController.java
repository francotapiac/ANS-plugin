package com.emcoders.ansplugin.controllers;

import com.emcoders.ansplugin.models.Alert;
import com.emcoders.ansplugin.models.Emotion;
import com.emcoders.ansplugin.models.Feature;
import com.emcoders.ansplugin.models.Signal;
import com.emcoders.scansembox.Utils.TimelineElement;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SignalController {


    Signal signal;
    Float ratio_coherence;
    String emotion;
    Integer type_alert;
    Float partial_start_time;
    Float partial_end_time;
    String cardiac_coherence_description;
    String path_image_emotion;
    int n_segment;
    private List<Double> signal_points_fci;



    public void create_signal(String path){
        this.signal = new Signal(path);
        create_points_signal_fci();
        this.signal.calculate_min_max_fci();
    }

    public void create_signal_excel(List<List<String>> data_signal, List<List<String>> data_signal_original, List<List<String>> signal_xls){
        this.signal = new Signal();
        create_time_line_excel(data_signal_original,0);
        create_time_line_excel(data_signal, 1);
        signal.calculate_length_signal(signal.getPartitions_time_line());         // Obteniendo tiempo final de la señal
        create_signal_time(signal_xls);                             //Creando arreglos con puntos de la señal y tiempo
        signal.get_general_data();                                    //Obteniendo datos generales de la señal
        this.signal.calculate_min_max_fci();
    }

        public void create_time_line_excel(List<List<String>> data_signal, int type){
        String[] list_names_times_features = {"vlf","lf","hf", "lf-hf","fft_total"};
        String[] list_names_freq_features = {"hr_mean","hr_min","hr_max", "sdnn","rmssd","sdsd"};
        for(int i = 1; i < data_signal.size(); i++){

            Float start_time = Float.parseFloat(data_signal.get(i).get(1));
            Float end_time = Float.parseFloat(data_signal.get(i).get(2));
            String name_emotion = data_signal.get(i).get(3);
            Float ratio_coherence = Float.parseFloat(data_signal.get(i).get(6));
            Emotion emotion = new Emotion(name_emotion);
            List<Pair<String, Float>> times_features = new ArrayList<>();
            List<Pair<String, Float>> freq_features = new ArrayList<>();
            int count_times = 0;
            int count_freqs = 0;
            for(int j = 7; j < data_signal.get(i).size(); j++){
                if(j <= 11){
                    Pair p = new Pair(list_names_times_features[count_freqs], Float.parseFloat(data_signal.get(i).get(j)));
                    count_freqs++;
                    freq_features.add(p);
                }
                else{
                    Pair p = new Pair(list_names_freq_features[count_times], Float.parseFloat(data_signal.get(i).get(j)));
                    count_times++;
                    times_features.add(p);

                }

            }
            Feature feature = new Feature(times_features, freq_features, start_time, end_time);
            Alert alert = new Alert(ratio_coherence, "", emotion, start_time, end_time);
            alert.setDescription(data_signal.get(i).get(4));
            alert.setText_alert(data_signal.get(i).get(5));


            //Contando cantidad de emociones y agregando a lista
            signal.append_emotions_signal(name_emotion);

            //Agregando características y alerta del segmento de la señal a la línea de tiempo
            Pair p = new Pair(alert, feature);
           // List<Pair<Alert,Feature>> time_line = signal.getTime_line();
            //time_line.add(p);
            // Si es 0, se crea linea de tiempo con todas particiones
            if(type == 0)
                signal.getPartitions_time_line().add(p);
            //En caso contrario, se crea con segmentos sin particiones
            else
                signal.getTime_line().add(p);
        }
    }

    public void create_signal_time(List<List<String>> signal_xls){
        for(int i = 1; i< signal_xls.size(); i++){
            List<Double> times_fci = new ArrayList<>();
            List<Double> fci = new ArrayList<>();

            times_fci = this.signal.getTimes_fci();
            times_fci.add(Double.parseDouble(signal_xls.get(i).get(1)));
            signal.setTimes_fci(times_fci);

            fci = this.signal.getFci();
            fci.add(Double.parseDouble(signal_xls.get(i).get(0)));
            signal.setFci(fci);

        }
    }

    public Double get_sourceLength(){
        return this.signal.getEnd_time_signal();
    }


    public void get_particular_data(Float time_comparate){
        for(int i = 0; i < signal.getTime_line().size(); i++){
            Float parcial_start_time = signal.getTime_line().get(i).getKey().getStart_time()*1000;
            Float parcial_end_time = signal.getTime_line().get(i).getKey().getEnd_time()*1000;
            if(time_comparate >= parcial_start_time && time_comparate < parcial_end_time){
                this.partial_start_time = parcial_start_time;
                this.partial_end_time = parcial_end_time;
                this.ratio_coherence = signal.getTime_line().get(i).getKey().getRatio_coherence();
                this.emotion = signal.getTime_line().get(i).getKey().getEmotion().getName();
                this.type_alert = signal.getTime_line().get(i).getKey().getType_alert();
                this.cardiac_coherence_description = signal.getTime_line().get(i).getKey().getDescription();
                Emotion emotion_aux = new Emotion("");
                this.path_image_emotion = emotion_aux.create_path(this.emotion);
                this.n_segment = i + 1;
                break;
            }
        }
    }

    public void get_particular_data_charge_line_time(Double time_comparate){
        for(int i = 0; i < signal.getTime_line().size(); i++){
            Float parcial_start_time = signal.getTime_line().get(i).getKey().getStart_time()*1000;
            Float parcial_end_time = signal.getTime_line().get(i).getKey().getEnd_time()*1000;
            if(time_comparate >= parcial_start_time && time_comparate < parcial_end_time){
                this.partial_start_time = parcial_start_time;
                this.partial_end_time = parcial_end_time;
                this.ratio_coherence = signal.getTime_line().get(i).getKey().getRatio_coherence();
                this.emotion = signal.getTime_line().get(i).getKey().getEmotion().getName();
                this.type_alert = signal.getTime_line().get(i).getKey().getType_alert();
                this.cardiac_coherence_description = signal.getTime_line().get(i).getKey().getDescription();
                Emotion emotion_aux = new Emotion("");
                this.path_image_emotion = emotion_aux.create_path(this.emotion);
                this.n_segment = i + 1;
                break;
            }
        }
    }

    public  void set_particular_emotion(Float time_comparate, String new_emotion){
        for(int i = 0; i < signal.getTime_line().size(); i++){
            Float parcial_start_time = signal.getTime_line().get(i).getKey().getStart_time()*1000;
            Float parcial_end_time = signal.getTime_line().get(i).getKey().getEnd_time()*1000;
            if(time_comparate >= parcial_start_time && time_comparate < parcial_end_time){
                this.emotion = new_emotion;
                Emotion emotion_aux = new Emotion(new_emotion);
                signal.getTime_line().get(i).getKey().setEmotion(emotion_aux);
                this.path_image_emotion = emotion_aux.create_path(this.emotion);
                break;
            }
        }
    }

    

    // Obtiene la señal original según los puntos del FCI. Esto, debido a que se busca acotar la señal mostrada
    public void create_points_signal_fci(){
        signal_points_fci = new ArrayList<>();
        List<Double> times_fci = signal.getTimes_fci();
        for(Double time : times_fci){
            Integer index_times_points = signal.getTimes_points().indexOf(time);
            System.out.println("Index: " + index_times_points);
            signal_points_fci.add(signal.getSignal_points().get(index_times_points));

        }
    }

    public void set_manual_alert_aux(Double start_time_select_line, Double end_time_select_line, String name_emotion){
        Float start_time_comparate = Float.parseFloat(start_time_select_line.toString());
        Float end_time_comparate = Float.parseFloat(end_time_select_line.toString());

        Float coherence = 0f;
        List<Pair<String,Float>> freq_features = new ArrayList<>();
        List<Pair<String,Float>> time_features = new ArrayList<>();
        int initial_count_freq = 0;
        int initial_count_time = 0;
        int j = 0;


        for (Pair<Alert,Feature> element: signal.getPartitions_time_line()) {

            // Calculando coherencia cardíaca
            coherence =  (coherence + element.getKey().getRatio_coherence())/2;

            if(element.getValue().getStart_time() >= start_time_comparate && element.getValue().getEnd_time() <= end_time_comparate){


                for (Pair<String, Float> freq: element.getValue().getFrequency_feature()) {
                    if(initial_count_freq == 0){
                        freq_features.add(new Pair<>(freq.getKey(), freq.getValue()));
                    }
                    else{
                        Float new_freq = (freq_features.get(j).getValue() + freq.getValue())/2;
                        freq_features.set(j,new Pair<>(freq.getKey(),new_freq));
                        j++;
                    }
                }
                initial_count_freq = 1;
                j = 0;
                for (Pair<String, Float> time: element.getValue().getTimes_feature()) {
                    if(initial_count_time == 0){
                        time_features.add(new Pair<>(time.getKey(), time.getValue()));
                    }
                    else{
                        Float new_time = (time_features.get(j).getValue() + time.getValue())/2;
                        time_features.set(j,new Pair<>(time.getKey(),new_time));
                        j++;
                    }
                }
                initial_count_time = 1;
                j = 0;
            }
        }
        Emotion emotion = new Emotion(name_emotion);
        Alert alert =  new Alert(coherence,"", emotion, signal.format_number(start_time_comparate), signal.format_number(end_time_comparate));
        alert.setText_alert("Alerta Manual");

        Feature feature = new Feature(time_features, freq_features, signal.format_number(start_time_comparate), signal.format_number(end_time_comparate));
        Pair<Alert,Feature> new_pair = new Pair<>(alert,feature);
        signal.getTime_line().add(new_pair);
    }

    //Cambiando las alertas manuales en tabla
    public void set_manual_alert(Double start_time_select_line, Double end_time_select_line){
        for(int i = 0; i < signal.getTime_line().size(); i++){
            Float start_time_comparate = Float.parseFloat(start_time_select_line.toString());
            Float end_time_comparate = Float.parseFloat(end_time_select_line.toString());
            Float parcial_start_time = signal.getTime_line().get(i).getKey().getStart_time()*1000;
            Float parcial_end_time = signal.getTime_line().get(i).getKey().getEnd_time()*1000;

            System.out.println("compara: [" + start_time_comparate + "," + end_time_comparate + "]");
            System.out.println("partial: [" + parcial_start_time + "," + parcial_end_time + "]");

            // Tamaño de alerta manual igual o mayor al tamaño del segmento
            if(start_time_comparate <= parcial_start_time && end_time_comparate >= parcial_end_time){
                //Alerta completa
                signal.getTime_line().get(i).getKey().setText_alert("Alerta Manual");
                signal.getTime_line().get(i).getKey().setType_alert(1);
                signal.getTime_line().get(i).getKey().setDescription("Alerta Manual en segmento completo");
            }
            //Si alerta manual se encuentra dentro del segmento
            else if (start_time_comparate >= parcial_start_time && end_time_comparate <= parcial_end_time) {
                //Si segmento de alerta manual es igual al inicio pero menor al final
                if (start_time_comparate == parcial_start_time && end_time_comparate < parcial_end_time) {
                    signal.getTime_line().get(i).getKey().setText_alert("Alerta Manual Parcial");
                    signal.getTime_line().get(i).getKey().setType_alert(2);
                    signal.getTime_line().get(i).getKey().setDescription("Alerta Manual con segmento incompleto al inicio");
                }
                //Si segmento de alerta manual es mayor al inicio pero igual al final
                else if (start_time_comparate > parcial_start_time && end_time_comparate == parcial_end_time) {
                    signal.getTime_line().get(i).getKey().setText_alert("Alerta Manual Parcial");
                    signal.getTime_line().get(i).getKey().setType_alert(2);
                    signal.getTime_line().get(i).getKey().setDescription("Alerta Manual con segmento incompleto al final");
                }
                //Si segmento de alerta manual es menor al segmento de la línea de tiempo
                else if (start_time_comparate > parcial_start_time && end_time_comparate < parcial_end_time) {
                    signal.getTime_line().get(i).getKey().setText_alert("Alerta Manual Parcial");
                    signal.getTime_line().get(i).getKey().setType_alert(2);
                    signal.getTime_line().get(i).getKey().setDescription("Alerta Manual con segmento incompleto");
                }
            }
            //Si parte de la alerta manual se encuentra dentro del segmento
            //Si cubre una parte del segmento inicial, pero no el total
            else if(start_time_comparate < parcial_start_time && end_time_comparate < parcial_end_time && end_time_comparate > parcial_start_time){
                signal.getTime_line().get(i).getKey().setText_alert("Alerta Manual Parcial");
                signal.getTime_line().get(i).getKey().setType_alert(2);
                signal.getTime_line().get(i).getKey().setDescription("Alerta Manual con segmento que cubre parte del inicio");
                //Alerta incompleta
            }
            //Si cubre parte del segmento final, pero no el total
            else if (start_time_comparate > parcial_start_time && start_time_comparate < parcial_end_time && end_time_comparate > parcial_end_time) {
                signal.getTime_line().get(i).getKey().setText_alert("Alerta Manual Parcial");
                signal.getTime_line().get(i).getKey().setType_alert(2);
                signal.getTime_line().get(i).getKey().setDescription("Alerta Manual con segmento que cubre parte del final");
            }
        }
    }

    public Integer calculateNumberAlertas(){
        Integer count = 0;
        Integer coherence_type = 0; //1 para coherencia < 0.5 y 2 para coherencia >= 0.5 y < 1
        for (Pair<Alert,Feature> time_line: signal.getTime_line() ) {
            Float ratio_coherence = time_line.getKey().getRatio_coherence();
            if (count == 0 && ratio_coherence < 1) {
                count++;
                if (ratio_coherence < 0.5)
                    coherence_type = 1;
                else {
                    coherence_type = 2;
                }
            }
            if (count > 0 && ratio_coherence < 1) {
                if (ratio_coherence < 0.5 && coherence_type == 2) {
                    coherence_type = 1;
                    count++;
                } else if (ratio_coherence >= 0.5 && coherence_type == 1) {
                    coherence_type = 2;
                    count++;
                }
                System.out.println(ratio_coherence);
                System.out.println(count);
            }
        }
        return count;
    }

    public void removeAlert(Double start_time_select_line, Double end_time_select_line){
        Float start_time_comparate = Float.parseFloat(start_time_select_line.toString());
        Float end_time_comparate = Float.parseFloat(end_time_select_line.toString());
        for (int i = 0; i < signal.getTime_line().size(); i++) {
            System.out.println(start_time_comparate);
            System.out.println(end_time_comparate);
            System.out.println(signal.getTime_line().get(i).getValue().getStart_time());
            System.out.println(signal.getTime_line().get(i).getValue().getEnd_time());
            if (signal.getTime_line().get(i).getValue().getStart_time() >= start_time_comparate && signal.getTime_line().get(i).getValue().getEnd_time() <= end_time_comparate) {
                signal.getTime_line().remove(i);
                break;
            }
        }
    }

    public List<Double> get_pointsSignal(){
        return this.signal.getPoints_signal();
    }

    public List<Double> get_times(){
        return this.signal.getTimes_fci();
    }

    public Signal getSignal() {
        return signal;
    }

    public Float getRatio_coherence() {
        return ratio_coherence;
    }

    public String getEmotion() {
        return emotion;
    }

    public Integer getType_alert() {
        return type_alert;
    }

    public Float getPartial_start_time() {
        return partial_start_time;
    }

    public Float getPartial_end_time() {
        return partial_end_time;
    }

    public String getCardiac_coherence_description() {
        return cardiac_coherence_description;
    }

    public String getPath_image_emotion() {return path_image_emotion;}

    public List<Double> getSignal_points_fci() {
        return signal_points_fci;
    }

    public int getN_segment() {
        return n_segment;
    }

    public void setSignal(Signal signal) {
        this.signal = signal;
    }

}
