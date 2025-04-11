package com.dansoftware.mugify;

import com.dansoftware.mugify.gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        var window = new MainWindow(getHostServices());
        window.show();
    }

    public static void main(String[] args) {
        launch();
    }
}