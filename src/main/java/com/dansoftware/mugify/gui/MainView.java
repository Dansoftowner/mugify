package com.dansoftware.mugify.gui;

import javafx.scene.layout.BorderPane;
import jfxtras.styles.jmetro.JMetroStyleClass;

public class MainView extends BorderPane {

    private final MugGrid mugGrid;
    private final MugifyMenuBar menuBar;

    public MainView() {
        getStyleClass().add(JMetroStyleClass.BACKGROUND);
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(mugGrid);

        setTop(menuBar);
        setCenter(mugGrid);
        mugGrid.maxWidthProperty().bind(this.widthProperty());
        mugGrid.maxHeightProperty().bind(this.heightProperty());

        // A GridPane minimális méretének beállítása (opcionális)
        mugGrid.setMinSize(0, 0);
    }

    public MugGrid getMugGrid() {
        return mugGrid;
    }

    public MugifyMenuBar getMenuBar() {
        return menuBar;
    }
}
