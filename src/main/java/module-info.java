module com.emcoders.ansplugin {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires com.emcoders.scansembox;
    requires MaterialFX;
    requires org.freedesktop.gstreamer;
    requires org.json;
    requires opencv;


    opens com.emcoders.ansplugin to javafx.fxml;
    exports com.emcoders.ansplugin;
}