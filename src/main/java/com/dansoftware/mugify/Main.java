package com.dansoftware.mugify;

import com.dansoftware.mugify.gui.MainView;
import com.dansoftware.mugify.gui.MugGrid;
import com.dansoftware.mugify.mug.MugBoundaries;
import com.dansoftware.mugify.mug.MugRandomizer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    private double[] startPos = new double[2];
    @Override
    public void start(Stage stage) throws IOException {


        var mainView = new MainView();
        var mugGrid = mainView.getMugGrid();

        var scene = new Scene(mainView);
        scene.setFill(Color.WHITESMOKE);

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    new MugRandomizer().apply(mugGrid.getMugTuple());


                });
            }

        });
    }

    public static void main(String[] args) {
        launch();
    }
}