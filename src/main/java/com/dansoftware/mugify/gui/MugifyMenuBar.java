package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.io.MugIO;
import com.dansoftware.mugify.mug.MugRandomizer;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.IOException;

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
        this.getMenus().add(buildHelpMenu());
    }

    private Menu buildFileMenu() {
        var menu = new Menu("File");

        var fileOpenItem = fileOpenMenuItem();
        menu.getItems().add(fileOpenItem);

        var fileSaveItem = fileSaveMenuItem();
        menu.getItems().add(fileSaveItem);

        var generateItem = new MenuItem("Generate new mug");
        generateItem.setOnAction(_ -> randomizer.apply(mugGrid.getMugTuple()));
        menu.getItems().add(generateItem);
        return menu;
    }

    private MenuItem fileSaveMenuItem() {
        var fileSaveItem = new MenuItem("Save file");
        fileSaveItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Mugify File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Mugify Files (*.mugify)", "*.mugify")
            );
            fileChooser.setInitialFileName("mug.mugify");
            var outputFile = fileChooser.showSaveDialog(getScene().getWindow());
            try {
                MugIO.saveToJson(outputFile.getAbsolutePath(), mugGrid.getMugTuple());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return fileSaveItem;
    }

    private MenuItem fileOpenMenuItem() {
        var fileOpenItem = new MenuItem("Open file");
        fileOpenItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Mugify File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Mugify Files (*.mugify)", "*.mugify")
            );
            var file = fileChooser.showOpenDialog(getScene().getWindow());
            try {
                MugIO.loadFromJson(file.getAbsolutePath(), mugGrid.getMugTuple());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return fileOpenItem;
    }

    private Menu buildViewMenu() {
        var menu = new Menu("View");

        var viewportMenu = new Menu("Views");

        ToggleGroup viewportGroup = new ToggleGroup();
        for (MugGrid.Viewport value : MugGrid.Viewport.values()) {
            RadioMenuItem item = new RadioMenuItem(value.name());
            item.setToggleGroup(viewportGroup);
            item.setSelected(mugGrid.getViewport() == value);
            item.setOnAction(_ -> mugGrid.setViewport(value));
            viewportMenu.getItems().add(item);
        }

        menu.getItems().add(viewportMenu);

        var themeGroup = new ToggleGroup();

        var uiThemeMenu = new Menu("UI theme");
        var darkThemeItem = new RadioMenuItem("Dark");
        var lightThemeItem = new RadioMenuItem("Light");
        var syncThemeItem = new RadioMenuItem("Sync with OS");

        darkThemeItem.setToggleGroup(themeGroup);
        lightThemeItem.setToggleGroup(themeGroup);
        syncThemeItem.setToggleGroup(themeGroup);

        uiThemeMenu.getItems().addAll(darkThemeItem, lightThemeItem, syncThemeItem);
        menu.getItems().add(uiThemeMenu);

        return menu;
    }

    private Menu buildHelpMenu() {
        var menu = new Menu("Help");

        var guideItem = new MenuItem("User Guide");
        var aboutItem = new MenuItem("About");

        menu.getItems().add(guideItem);
        menu.getItems().add(aboutItem);

        return menu;
    }
}
