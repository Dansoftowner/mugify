package com.dansoftware.mugify;

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

        var mugGrid = new MugGrid();
        mugGrid.getMugTuple().setHeight(MugBoundaries.MAX_HEIGHT);
        mugGrid.getMugTuple().setHandleRadius(MugBoundaries.MAX_HANDLE_RADIUS);



        var scene = new Scene(mugGrid);
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
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}