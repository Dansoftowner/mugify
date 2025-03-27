module com.dansoftware.mugify {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    exports com.dansoftware.mugify;

    opens com.dansoftware.mugify.io to com.google.gson;
}