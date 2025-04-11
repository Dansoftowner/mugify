module com.dansoftware.mugify {
    requires javafx.fxml;
    requires com.google.gson;
    requires com.pixelduke.transit;
    requires com.jthemedetector;
    requires javafx.base;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;

    exports com.dansoftware.mugify;

    opens com.dansoftware.mugify.io to com.google.gson;
}