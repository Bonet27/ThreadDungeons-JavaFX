module com.bonet.threaddungeons {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bonet.threaddungeons to javafx.fxml;
    exports com.bonet.threaddungeons;
}