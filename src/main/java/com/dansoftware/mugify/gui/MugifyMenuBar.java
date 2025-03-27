package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.MugRandomizer;
import javafx.scene.control.*;

public class MugifyMenuBar extends MenuBar {
    private final MugGrid mugGrid;
    private final MugRandomizer randomizer;

    public MugifyMenuBar(MugGrid mugGrid) {
        this.mugGrid = mugGrid;
        this.randomizer = new MugRandomizer();
        this.buildMenuStructure();
    }

    private void buildMenuStructure() {
        this.getMenus().add(buildFileMenu());
        this.getMenus().add(buildViewMenu());
    }

    private Menu buildFileMenu() {
        var menu = new Menu("File");

        var fileOpenItem = new MenuItem("Open file");
        menu.getItems().add(fileOpenItem);

        var fileSaveItem = new MenuItem("Save file");
        menu.getItems().add(fileSaveItem);

        var generateItem = new MenuItem("Generate new mug");
        generateItem.setOnAction(_ -> randomizer.apply(mugGrid.getMugTuple()));
        menu.getItems().add(generateItem);
        return menu;
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
