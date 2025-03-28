package com.dansoftware.mugify.gui;

import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final MugGrid mugGrid;
    private final MugifyMenuBar menuBar;
    private final MugEditorTabPane mugEditorTabPane;

    public MainView() {
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(mugGrid);
        mugEditorTabPane = new MugEditorTabPane(mugGrid.getMugTuple());

        setTop(menuBar);
        setCenter(mugGrid);
        setRight(mugEditorTabPane);

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
