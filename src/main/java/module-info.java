module com.emcoders.ansplugin {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires com.emcoders.scansembox;
    requires MaterialFX;
    requires org.freedesktop.gstreamer;
    requires org.json;
    requires opencv;
    requires javafx.media;
    requires com.jfoenix;
    requires org.apache.poi.poi;


    opens com.emcoders.ansplugin to javafx.fxml;
    exports com.emcoders.ansplugin;
    exports com.emcoders.ansplugin.controllers;
    exports com.emcoders.ansplugin.models;
    opens com.emcoders.ansplugin.controllers to javafx.fxml;
}