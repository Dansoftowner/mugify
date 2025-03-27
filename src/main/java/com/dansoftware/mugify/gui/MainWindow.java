package com.dansoftware.mugify.gui;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainWindow extends Stage {
    public MainWindow() {
        var mainView = new MainView();

        var scene = new Scene(mainView);
        scene.setFill(Color.WHITESMOKE);

        setTitle("Mugify");
        setScene(scene);

        setSize();
        centerOnScreen();
    }

    private void setSize() {
        var screen = Screen.getPrimary();

        double width = screen.getBounds().getWidth() / 2;
        double height = screen.getBounds().getHeight() / 2;

        setWidth(width);
        setHeight(height);
    }
}
