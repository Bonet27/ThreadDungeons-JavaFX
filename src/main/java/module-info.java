module com.bonet.threaddungeons {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    opens com.bonet.threaddungeons to com.google.gson, javafx.fxml;
    exports com.bonet.threaddungeons;
    exports com.bonet.threaddungeons.server;
    opens com.bonet.threaddungeons.server to com.google.gson;
}
