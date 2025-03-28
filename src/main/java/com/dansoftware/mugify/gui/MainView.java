package com.dansoftware.mugify.gui;

import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final MugGrid mugGrid;
    private final MugifyMenuBar menuBar;

    public MainView() {
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(mugGrid);

        setTop(menuBar);
        setCenter(mugGrid);

        // for proper responsiveness
        mugGrid.maxWidthProperty().bind(this.widthProperty());
        mugGrid.maxHeightProperty().bind(this.heightProperty());
        mugGrid.setMinSize(0, 0);
    }

    public MugGrid getMugGrid() {
        return mugGrid;
    }

    public MugifyMenuBar getMenuBar() {
        return menuBar;
    }
}
