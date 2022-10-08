package com.emcoders.ansplugin.models;

import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Signal {
    private String id;
    private String name_signal;
    private float sampling;
    private List<Float> points_signal = new ArrayList<>();
    private List<Float> times_signal;
    private List<Float> filtered_signal;
    private List<Float> rr_peaks;
    private List<Float> signal_Segment;
    private List<Float> times_segment;
    private List<Pair<Alert,Feature>> time_line;

    public Signal(String path, float sampling, Integer type_signal) throws IOException {
        get_name_signal(type_signal);   //Obtiene el nombre de la señal
        this.sampling = sampling;
        get_signal(path); //Obtiene point_signal
        get_rpeaks(1);
    }

    public Signal(List<Pair<Alert,Feature>> time_line){
        this.time_line = time_line;
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
                    this.points_signal.add(Float.parseFloat(myReader.nextLine().split(";")[1]));
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
    Define el nombre la señal según su tipo
    @param type_signal: tipo de señal
    @return:            string con nombre de señal
     */
    public void get_name_signal(Integer type_signal){
        if(type_signal == 1)
            this.name_signal = "ECG";
        else
            this.name_signal = "PPG";
    }

    /*
    Obtiene series de R-Peaks usando biosppy
    @param signal:  señal cardiaca
    @param type_signal: tipo de señal (ECG y PPG)
    @param sampling:frecuencia de muestreo
    @return:        rango de tiempo de la señal, señal filtrada y rpeaks
     */
    public void get_rpeaks(Integer type_signal) throws IOException {
        String path = System.getProperty("user.dir") + "\\signals\\ecg.csv";
        String cmd = System.getProperty("user.dir") + "\\pythonScripts\\signal.py";
        if(type_signal == 1){
            ProcessBuilder builder = new ProcessBuilder("py", cmd, path, "1","1000");
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader readers = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String lines = null;

            while((lines=reader.readLine()) != null) {
                System.out.println("lines" + lines);
            }
            while((lines=readers.readLine())!=null){
                System.out.println("Error lines"  + lines);
            }

        }
        else{

        }

    }






    /*
    * Sección de Getter y Setter
    * */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_signal() {
        return name_signal;
    }

    public float getSampling() {
        return sampling;
    }

    public void setSampling(float sampling) {
        this.sampling = sampling;
    }

    public void setName_signal(String name_signal) {
        this.name_signal = name_signal;
    }

    public List<Float> getPoints_signal() {
        return points_signal;
    }

    public void setPoints_signal(List<Float> points_signal) {
        this.points_signal = points_signal;
    }

    public List<Float> getTimes_signal() {
        return times_signal;
    }

    public void setTimes_signal(List<Float> times_signal) {
        this.times_signal = times_signal;
    }

    public List<Float> getFiltered_signal() {
        return filtered_signal;
    }

    public void setFiltered_signal(List<Float> filtered_signal) {
        this.filtered_signal = filtered_signal;
    }

    public List<Float> getRr_peaks() {
        return rr_peaks;
    }

    public void setRr_peaks(List<Float> rr_peaks) {
        this.rr_peaks = rr_peaks;
    }

    public List<Float> getSignal_Segment() {
        return signal_Segment;
    }

    public void setSignal_Segment(List<Float> signal_Segment) {
        this.signal_Segment = signal_Segment;
    }

    public List<Float> getTimes_segment() {
        return times_segment;
    }

    public void setTimes_segment(List<Float> times_segment) {
        this.times_segment = times_segment;
    }

    public List<Pair<Alert, Feature>> getTime_line() {
        return time_line;
    }

    public void setTime_line(List<Pair<Alert, Feature>> time_line) {
        this.time_line = time_line;
    }
}
