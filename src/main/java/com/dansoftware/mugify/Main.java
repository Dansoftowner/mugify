package com.dansoftware.mugify;

import com.dansoftware.mugify.config.ConfigIO;
import com.dansoftware.mugify.config.Preferences;
import com.dansoftware.mugify.gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import static com.dansoftware.mugify.util.UncheckedAction.run;

public class Main extends Application {

    private volatile Preferences preferences;

    @Override
    public void start(Stage stage) {
        var window = new MainWindow(preferences, getHostServices());

        Thread.setDefaultUncaughtExceptionHandler(new MugifyExceptionHandler(window));

        window.show();
    }

    @Override
    public void init() throws Exception {
        preferences = ConfigIO.load();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> run(() -> ConfigIO.save(preferences))));
    }

    public static void main(String[] args) {
        launch();
    }
}