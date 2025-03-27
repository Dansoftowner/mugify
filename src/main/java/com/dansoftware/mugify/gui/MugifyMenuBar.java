package com.dansoftware.mugify.gui;

import javafx.scene.control.*;

public class MugifyMenuBar extends MenuBar {
    private final MugGrid mugGrid;

    public MugifyMenuBar(MugGrid mugGrid) {
        this.mugGrid = mugGrid;
        this.buildMenuStructure();
    }

    private void buildMenuStructure() {
        this.getMenus().add(new Menu("File"));
        this.getMenus().add(buildViewMenu());
    }

    private Menu buildViewMenu() {
        var menu = new Menu("View");

        ToggleGroup itemGrp = new ToggleGroup();
        for (MugGrid.Viewport value : MugGrid.Viewport.values()) {
            RadioMenuItem item = new RadioMenuItem(value.name());
            item.setToggleGroup(itemGrp);
            item.setSelected(mugGrid.getViewport() == value);
            item.setOnAction(_ -> mugGrid.setViewport(value));
            menu.getItems().add(item);
        }

        return menu;
    }


}
