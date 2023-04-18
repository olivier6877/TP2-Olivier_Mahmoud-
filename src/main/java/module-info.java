module courseudem.demo {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens courseudem.demo to javafx.fxml;
    exports courseudem.demo.client;
    exports courseudem.demo.server.models;
    exports courseudem.demo.server;
}