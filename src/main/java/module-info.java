module com.emcoders.ansplugin {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;


    opens com.emcoders.ansplugin to javafx.fxml;
    exports com.emcoders.ansplugin;
}