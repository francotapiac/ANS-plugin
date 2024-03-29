package com.emcoders.ansplugin.models;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

public class Signal {
    private String id;
    private List<Double> fci = new ArrayList<>();
    private Pair<Double, Double> min_max_fci_point;
    private List<Double> rr_peaks = new ArrayList<>();
    private List<Double> times_fci = new ArrayList<>();
    private List<Double> signal_points = new ArrayList<>();
    private List<Double> times_points = new ArrayList<>();
    private List<Pair<Alert,Feature>> time_line;
    private List<Pair<Alert,Feature>> partitions_time_line;
    private double start_time_signal;
    private double end_time_signal;

    //Atributos información general de la señal
    private Integer count_alerts;
    private List<String> emotions_signal;
    private List<Integer> cant_emotions_signal;
    private String predominant_emotion;

    private String emotion_image_path;

    public Signal(){
        this.time_line = new ArrayList<>();
        this.emotions_signal = new ArrayList<>();
        this.partitions_time_line = new ArrayList<>();
        this.cant_emotions_signal = new ArrayList<>();
    }
    public Signal(String path_api){
        //get_signal( System.getProperty("user.dir")  + "\\signals\\ecg.csv");
        JSONArray dataObject = get_json_time_line(path_api);
        this.time_line = create_time_line_signal_aux(dataObject);       // Creando línea de tiempo de la señal con segmentos
        this.partitions_time_line = create_time_line_signal(dataObject);// Creando línea de tiempo con todas las particiones
        calculate_length_signal(partitions_time_line);                             // Obteniendo tiempo final de la señal
        create_signal_time(dataObject);                             //Creando arreglos con puntos de la señal y tiempo
        get_general_data();                                         //Obteniendo datos generales de la señal
    }

    /*
    Obtiene la señal cardiaca de un archivo
    @param path:    archivo con señal cardiaca
    @return:        arreglo con puntos de la señal
     */
    public void get_signal(String path){
        try {
            File myObj = new File(path);
            Scanner myReader = new Scanner(myObj);
            int contador = 0;
            while (myReader.hasNextLine()) {
                if(contador != 0){
                    this.fci.add(Double.parseDouble(myReader.nextLine().split(";")[1]));
                }
                else{
                    contador++;
                    myReader.nextLine();
                }
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /*
   Obtiene time line en formato json del back proveniente de una API
   @return:    Json con time line
    */
    public JSONArray get_json_time_line(String path_api){
        final String URL_API = path_api;
        JSONArray dataObject = null;


        try {
            URL url = new URL(URL_API);
            URL test = new URL("http://127.0.0.1:5000/");
            HttpURLConnection conn = (HttpURLConnection) test.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //Check if connect is made
            int responseCode = conn.getResponseCode();

            // 200 OK
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                //Close the scanner
                scanner.close();

                //JSON simple library Setup with Maven is used to convert strings to JSON
                JSONParser parse = new JSONParser();
                dataObject = (JSONArray) parse.parse(String.valueOf(informationString));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObject;
    }

   /*
   Crea una línea de tiempo a partir del json leído en el servidor
   @param dataObject:   Json con lista de objetos del análisis de la señal
   @return:             Lista de Alert y Feature de cada segmento de la señal analizada
    */
    public List<Pair<Alert,Feature>> create_time_line_signal(JSONArray dataObject){
        List<Pair<Alert,Feature>> time_line = new ArrayList<>();
        this.emotions_signal = new ArrayList<>();
        this.cant_emotions_signal = new ArrayList<>();

        for (int i =0; i < dataObject.size()-6; i++) {

            //Obteniendo datos de json
            JSONObject jsonObject = (JSONObject) dataObject.get(i);
            float start_time = format_number(Float.parseFloat(jsonObject.get("start_time").toString()));
            float end_time = format_number(Float.parseFloat(jsonObject.get("end_time").toString()));
            String name_emotion = set_predominant_emotion_name(jsonObject.get("emotion").toString());
            float ratio_coherence = Float.parseFloat(jsonObject.get("ratio_coherence").toString());
            List<Pair<String, Float>> times_features = json_object_to_pair_float(jsonObject.get("time_df"));
            List<Pair<String, Float>> freq_features = json_object_to_pair_float(jsonObject.get("freq_df"));
            System.out.println(times_features);
            System.out.println(freq_features);
            //Creando objetos para cada segmento analizado
            Emotion emotion = new Emotion(name_emotion);
            Feature feature = new Feature(times_features, freq_features, start_time, end_time);
            Alert alert = new Alert(ratio_coherence, "", emotion, start_time, end_time);

            //Contando cantidad de emociones y agregando a lista
            append_emotions_signal(name_emotion);

            //Agregando características y alerta del segmento de la señla a la línea de tiempo
            Pair p = new Pair(alert, feature);
            time_line.add(p);

        }

        return time_line;
    }

    public List<Pair<Alert,Feature>> create_time_line_signal_aux(JSONArray dataObject){
        List<Pair<Alert,Feature>> time_line = new ArrayList<>();
        this.emotions_signal = new ArrayList<>();
        this.cant_emotions_signal = new ArrayList<>();
        int index_prev_segment = -1;
        String prevCoherenceDescription = "";
        Float promCoherence = 0.f;
        for (int i =0; i < dataObject.size()-6; i++) {


            //Obteniendo datos de json
            JSONObject jsonObject = (JSONObject) dataObject.get(i);
            float start_time = format_number(Float.parseFloat(jsonObject.get("start_time").toString()));
            float end_time = format_number(Float.parseFloat(jsonObject.get("end_time").toString()));
            String name_emotion = set_predominant_emotion_name(jsonObject.get("emotion").toString());

            float ratio_coherence = Float.parseFloat(jsonObject.get("ratio_coherence").toString());
            List<Pair<String, Float>> times_features = json_object_to_pair_float(jsonObject.get("time_df"));
            List<Pair<String, Float>> freq_features = json_object_to_pair_float(jsonObject.get("freq_df"));
            System.out.println(times_features);
            System.out.println(freq_features);
            //Creando objetos para cada segmento analizado
            Emotion emotion = new Emotion(name_emotion);
            Feature feature = new Feature(times_features, freq_features, start_time, end_time);
            Alert alert = new Alert(ratio_coherence, "", emotion, start_time, end_time);

            //Contando cantidad de emociones y agregando a lista
            append_emotions_signal(name_emotion);

            if(alert.getDescription().equals(prevCoherenceDescription)){
                promCoherence = format_number((promCoherence + alert.getRatio_coherence())/2);
                time_line.get(index_prev_segment).getKey().setEnd_time(end_time);
                time_line.get(index_prev_segment).getValue().setEnd_time(end_time);
                time_line.get(index_prev_segment).getKey().setRatio_coherence(promCoherence);

            }
            else{
                index_prev_segment++;
                prevCoherenceDescription = alert.getDescription();
                promCoherence = format_number(alert.getRatio_coherence());
                //Agregando características y alerta del segmento de la señla a la línea de tiempo
                Pair p = new Pair(alert, feature);
                time_line.add(p);
            }


        }

        return time_line;
    }
    /*
    Convierte un json a una lista de pares String-Float
    @param object:  Json del análisis de la señal leída
    @return:        Lista de pares String-Float
     */
    public List<Pair<String, Float>> json_object_to_pair_float(Object object){
        System.out.println(object);
        String object_string = object.toString();
        String object_string_1 = object_string.replace("[","");
        String object_string_2 = object_string_1.replace("]","");
        String object_string_3 = object_string_2.replace("{","");
        String object_string_4 = object_string_3.replace("}","");
        String[] list_array_objects = object_string_4.split(",");
        List<Pair<String, Float>> list_pairs = new ArrayList<>();
        for(int i = 0; i < list_array_objects.length; i = i + 1){
            String[] pair_object_1 =  list_array_objects[i].split(":");
            StringBuilder key = new StringBuilder(pair_object_1[0]);
            StringBuilder key_2 = key.deleteCharAt(0);
            StringBuilder key_3 = key_2.deleteCharAt(key.length()-1);
            Pair p = new Pair(key_3.toString(), Float.parseFloat(pair_object_1[1]));
            list_pairs.add(p);
        }
        return list_pairs;
    }

    public void calculate_length_signal(List<Pair<Alert,Feature>> time_line){

        this.start_time_signal = format_number(time_line.get(0).getValue().getStart_time());
        this.end_time_signal = format_number(time_line.get(time_line.size()-1).getValue().getEnd_time());
    }

    public void create_signal_time(JSONArray dataObject){

        // Obteniendo objetos de señal y tiempos
        JSONObject object_points_signal = (JSONObject) dataObject.get(dataObject.size() - 5);
        JSONObject object_times = (JSONObject) dataObject.get(dataObject.size() - 4);
        JSONObject object_rr_peaks = (JSONObject) dataObject.get(dataObject.size() - 3);
        JSONObject object_signal_points = (JSONObject) dataObject.get(dataObject.size() - 2);
        JSONObject object_times_points = (JSONObject) dataObject.get(dataObject.size() - 1);

        // Lista de string
        String[] list_string_points_signal = object_points_signal.get("fci").toString().split(",");
        String[] list_string_times = object_times.get("times_fci").toString().split(",");
        String[] list_rr_peaks = object_rr_peaks.get("rr").toString().split(",");
        String[] list_signal_points = object_signal_points.get("signal").toString().split(",");
        String[] list_times_points = object_times_points.get("time_signal").toString().split(",");

        //Transformando a double cada elemento de cada arreglo
        for(int i = 1; i < list_string_points_signal.length-1; i++){
            this.fci.add(format_number_double(Double.parseDouble(list_string_points_signal[i])));
            this.times_fci.add(format_number_double(Double.parseDouble(list_string_times[i])));
            this.rr_peaks.add(format_number_double(Double.parseDouble(list_rr_peaks[i])));
        }


        for(int i = 1; i < list_signal_points.length -1; i++){
            this.signal_points.add(format_number_double(Double.parseDouble(list_signal_points[i])));
            this.times_points.add(format_number_double(Double.parseDouble(list_times_points[i])));
        }
    }

    /*
    Agregando cantidad de emociones en señal
     */
    public void append_emotions_signal(String name_signal){
        if(!this.emotions_signal.isEmpty() && this.emotions_signal.contains(name_signal)){
            Integer index = this.emotions_signal.indexOf(name_signal);
            this.cant_emotions_signal.set(index, this.cant_emotions_signal.get(index) + 1);
        }
        else {
            this.emotions_signal.add(name_signal);
            this.cant_emotions_signal.add(1);
        }
    }

    /*
    Obteniendo datos generales
     */
    public void get_general_data(){
        this.count_alerts = 0;               // Cantidad de alertas
        // Contando cantidad de alertas
        for(int i = 0; i < this.time_line.size(); i++){
            if(this.time_line.get(i).getKey().getType_alert() == 1){
                this.count_alerts++;
            }
        }
        // Obteniendo emoción predominante
        Integer max_value = Collections.max(this.cant_emotions_signal);
        Integer index = this.cant_emotions_signal.indexOf(max_value);
        this.predominant_emotion = this.emotions_signal.get(index);
        Emotion emotion = new Emotion("");
        this.emotion_image_path = emotion.create_path(this.predominant_emotion);
    }

    public String set_predominant_emotion_name(String emotion_name){
        if(emotion_name.equals("surprise"))
            return "Sorpresa";
        else if (emotion_name.equals("happiness")) {
            return "Felicidad";
        }
        else if(emotion_name.equals("fear")){
            return "Miedo";
        }
        else if(emotion_name.equals("anger")){
            return "Enojo";
        }
        else if(emotion_name.equals("sadness")){
            return "Tristeza";
        }
        return "";

    }

    // Entrega un número con formato .0000
    public Float format_number(Float number){
        DecimalFormat numeroFormateado = new DecimalFormat("#.00000");
        String textoFormateado = numeroFormateado.format(number);
        String[] list_number = textoFormateado.split(",");
        String new_number = list_number[0] + "." + list_number[1];
        return Float.parseFloat(new_number);
    }
    public Double format_number_double(Double number){
        DecimalFormat numeroFormateado = new DecimalFormat("#.00000");
        String textoFormateado = numeroFormateado.format(number);
        String[] list_number = textoFormateado.split(",");
        String new_number = list_number[0] + "." + list_number[1];
        return Double.parseDouble(new_number);
    }

    public Pair<Double,Double> calculate_min_max_fci(){
        //Creando variables para ver menor y mayor punto de fci
        Double min_fci = 200.0;
        Double max_fci = 0.0;
        for(Double point: fci){
            //Verificando menor y mayor fci
            if(point < min_fci){
                min_fci = point;
            }
            if(point > max_fci){
                max_fci =point;
            }
        }
        //Instanciando min_max para el fci
        min_max_fci_point = new Pair<>(min_fci,max_fci);
        return min_max_fci_point;
    }


    /*
    Sección de Getter y Setter
    */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Double> getRr_peaks() {
        return rr_peaks;
    }

    public List<Double> getPoints_signal() {
        return fci;
    }

    public List<Double> getTimes_fci() {
        return times_fci;
    }

    public List<Pair<Alert, Feature>> getTime_line() {
        return time_line;
    }

    public double getEnd_time_signal() {
        return end_time_signal;
    }

    public Integer getCount_alerts() {
        return count_alerts;
    }

    public String getPredominant_emotion() {
        return predominant_emotion;
    }

    public double getStart_time_signal() {
        return start_time_signal;
    }

    public String getEmotion_image_path() {
        return emotion_image_path;
    }

    public List<Double> getFci() {
        return fci;
    }

    public List<Double> getSignal_points() {
        return signal_points;
    }

    public List<Double> getTimes_points() {
        return times_points;
    }

    public void setFci(List<Double> fci) {
        this.fci = fci;
    }

    public void setRr_peaks(List<Double> rr_peaks) {
        this.rr_peaks = rr_peaks;
    }

    public void setTimes_fci(List<Double> times_fci) {
        this.times_fci = times_fci;
    }

    public void setSignal_points(List<Double> signal_points) {
        this.signal_points = signal_points;
    }

    public void setTimes_points(List<Double> times_points) {
        this.times_points = times_points;
    }

    public void setTime_line(List<Pair<Alert, Feature>> time_line) {
        this.time_line = time_line;
    }

    public void setStart_time_signal(double start_time_signal) {
        this.start_time_signal = start_time_signal;
    }

    public void setEnd_time_signal(double end_time_signal) {
        this.end_time_signal = end_time_signal;
    }

    public void setCount_alerts(Integer count_alerts) {
        this.count_alerts = count_alerts;
    }

    public List<String> getEmotions_signal() {
        return emotions_signal;
    }

    public void setEmotions_signal(List<String> emotions_signal) {
        this.emotions_signal = emotions_signal;
    }

    public List<Integer> getCant_emotions_signal() {
        return cant_emotions_signal;
    }

    public void setCant_emotions_signal(List<Integer> cant_emotions_signal) {
        this.cant_emotions_signal = cant_emotions_signal;
    }

    public void setPredominant_emotion(String predominant_emotion) {
        this.predominant_emotion = predominant_emotion;
    }

    public void setEmotion_image_path(String emotion_image_path) {
        this.emotion_image_path = emotion_image_path;
    }

    public Pair<Double, Double> getMin_max_fci_point() {
        return min_max_fci_point;
    }

    public void setMin_max_fci_point(Pair<Double, Double> min_max_fci_point) {
        this.min_max_fci_point = min_max_fci_point;
    }

    public List<Pair<Alert, Feature>> getPartitions_time_line() {
        return partitions_time_line;
    }

    public void setPartitions_time_line(List<Pair<Alert, Feature>> partitions_time_line) {
        this.partitions_time_line = partitions_time_line;
    }
}
