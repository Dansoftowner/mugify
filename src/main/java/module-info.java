module com.dansoftware.mugify {
    requires com.google.gson;
    requires com.pixelduke.transit;
    requires com.jthemedetector;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.materialdesign2;
    requires org.kordamp.ikonli.core;
    requires one.jpro.platform.mdfx;
    requires animatefx;

    exports com.dansoftware.mugify;

    opens com.dansoftware.mugify.io to com.google.gson;
    opens com.dansoftware.mugify.config to com.google.gson;
}