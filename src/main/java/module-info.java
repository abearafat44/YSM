module YSMSample3 {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.base;
    requires poi;
    requires poi.ooxml;
    requires java.prefs;
    requires org.controlsfx.controls;

    opens application.controller;
    opens application.object;
    exports application;
}