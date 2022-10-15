package com.emcoders.ansplugin.models;

import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Signal {
    private String id;
    private List<Double> points_signal = new ArrayList<>();
    private List<Double> times = new ArrayList<>();
    private List<Pair<Alert,Feature>> time_line;
    private double end_time_signal;

    //Atributos información general de la señal
    private Integer count_alerts;
    private List<String> emotions_signal;
    private List<Integer> cant_emotions_signal;
    private String predominant_emotion;

    public Signal(String path_api){
        //get_signal( System.getProperty("user.dir")  + "\\signals\\ecg.csv");
        JSONArray dataObject = get_json_time_line(path_api);
        this.time_line = create_time_line_signal(dataObject);       // Creando línea de tiempo de la señal
        this.end_time_signal = calculate_length_signal(time_line);  // Obteniendo tiempo final de la señal
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
                    this.points_signal.add(Double.parseDouble(myReader.nextLine().split(";")[1]));
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

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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

        for (int i =0; i < dataObject.size()-2; i++) {

            //Obteniendo datos de json
            JSONObject jsonObject = (JSONObject) dataObject.get(i);
            float start_time = Float.parseFloat(jsonObject.get("start_time").toString());
            float end_time = Float.parseFloat(jsonObject.get("end_time").toString());
            String name_emotion = jsonObject.get("emotion").toString();
            float ratio_coherence = Float.parseFloat(jsonObject.get("ratio_coherence").toString());
            List<Pair<String, Float>> times_features = json_object_to_pair_float(jsonObject.get("time_df"));
            List<Pair<String, Float>> freq_features = json_object_to_pair_float(jsonObject.get("freq_df"));

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
    /*
    Convierte un json a una lista de pares String-Float
    @param object:  Json del análisis de la señal leída
    @return:        Lista de pares String-Float
     */
    public List<Pair<String, Float>> json_object_to_pair_float(Object object){
        String[] list_array_object = object.toString().split(",");
        List<Pair<String, Float>> list_pairs = new ArrayList<>();
        for(int i = 0; i < list_array_object.length - 1; i = i + 2){
            Pair p = new Pair(list_array_object[i], list_array_object[i+1]);
            list_pairs.add(p);
        }
        return list_pairs;
    }

    public double calculate_length_signal(List<Pair<Alert,Feature>> time_line){
        double length_signal = time_line.get(time_line.size()-1).getValue().getEnd_time();
        return length_signal;
    }

    public void create_signal_time(JSONArray dataObject){

        // Obteniendo objetos de señal y tiempos
        JSONObject object_points_signal = (JSONObject) dataObject.get(dataObject.size() - 2);
        JSONObject object_times = (JSONObject) dataObject.get(dataObject.size() - 1);

        // Lista de string
        String[] list_string_points_signal = object_points_signal.get("points_signal").toString().split(",");
        String[] list_string_times = object_times.get("times").toString().split(",");

        //Transformando a double cada elemento de cada arreglo
        for(int i = 1; i < list_string_points_signal.length-1; i++){
            this.points_signal.add(Double.parseDouble(list_string_points_signal[i]));
            this.times.add(Double.parseDouble(list_string_times[i]));
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
        for(int j = 0; j < this.emotions_signal.size(); j++){
            Integer max_value = Collections.max(this.cant_emotions_signal);
            Integer index = this.cant_emotions_signal.indexOf(max_value);
            this.predominant_emotion = this.emotions_signal.get(index);
        }
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

    public List<Double> getPoints_signal() {
        return points_signal;
    }

    public List<Double> getTimes() {
        return times;
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
}
