package com.emcoders.ansplugin;


import com.emcoders.ansplugin.models.Signal;
import javafx.util.Pair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class SignalController {
    Signal signal;

    public SignalController(String path){
        signal = new Signal(path);
    }

}
