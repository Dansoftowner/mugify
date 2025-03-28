module com.dansoftware.mugify {
    requires javafx.fxml;
    requires com.google.gson;
    requires com.pixelduke.transit;

    exports com.dansoftware.mugify;

    opens com.dansoftware.mugify.io to com.google.gson;
}