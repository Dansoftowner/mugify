package com.dansoftware.mugify;

import com.dansoftware.mugify.gui.MainWindow;
import com.dansoftware.mugify.mug.MugHistory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        var window = new MainWindow(new MugHistory());
        window.show();
    }

    public static void main(String[] args) {
        launch();
    }
}