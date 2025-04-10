package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.io.MugIO;
import com.dansoftware.mugify.mug.MugRandomizer;
import com.pixelduke.transit.Style;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.util.Locale;

import static com.dansoftware.mugify.i18n.I18NUtils.*;

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
        this.getMenus().add(buildLanguageMenu());
        this.getMenus().add(buildHelpMenu());
    }

    private Menu buildLanguageMenu() {
        Menu menu = new Menu();
        menu.textProperty().bind(val("menu_language"));

        ToggleGroup languageGroup = new ToggleGroup();

        for (Locale locale : getSupportedLocales()) {
            RadioMenuItem item = new RadioMenuItem(locale.getDisplayLanguage());
            item.setToggleGroup(languageGroup);
            item.setOnAction(_ -> setLocale(locale));
            if (locale.equals(Locale.getDefault()))
                item.setSelected(true);
            menu.getItems().add(item);
        }

        return menu;
    }

    private Menu buildFileMenu() {
        var menu = new Menu();
        menu.textProperty().bind(val("menu_file"));

        var fileOpenItem = fileOpenMenuItem();
        fileOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(fileOpenItem);

        var fileSaveItem = fileSaveMenuItem();
        fileSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(fileSaveItem);

        var generateItem = new MenuItem();
        generateItem.textProperty().bind(val("menu_file_generate"));
        generateItem.setOnAction(_ -> randomizer.apply(mugGrid.getMugTuple()));
        generateItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(generateItem);

        var fileDeleteItem = fileDeleteMenuItem();
        menu.getItems().add(fileDeleteItem);

        return menu;
    }

    private MenuItem fileSaveMenuItem() {
        var fileSaveItem = new MenuItem();
        fileSaveItem.textProperty().bind(val("menu_file_save"));
        fileSaveItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(val("filechooser_save_title").get());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
            );
            fileChooser.setInitialFileName("%s.mugify".formatted(mugGrid.getMugTuple().getName()));
            var outputFile = fileChooser.showSaveDialog(getScene().getWindow());
            if (outputFile != null) {
                try {
                    MugIO.saveToJson(outputFile.getAbsolutePath(), mugGrid.getMugTuple());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return fileSaveItem;
    }

    private MenuItem fileOpenMenuItem() {
        var fileOpenItem = new MenuItem();
        fileOpenItem.textProperty().bind(val("menu_file_open"));
        fileOpenItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(val("filechooser_open_title").get());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
            );
            var file = fileChooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                try {
                    MugIO.loadFromJson(file.getAbsolutePath(), mugGrid.getMugTuple());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return fileOpenItem;
    }

    private MenuItem fileDeleteMenuItem() {
        var fileDeleteItem = new MenuItem();
        fileDeleteItem.textProperty().bind(val("menu_file_delete"));
        fileDeleteItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        fileDeleteItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(val("filechooser_delete_title").get());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
            );
            var file = fileChooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle(val("alert_delete_confirm_title").get());
                alert.setHeaderText(val("alert_delete_confirm_header").get());
                alert.setContentText(val("alert_delete_confirm_content").get());
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (file.delete()) {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle(val("alert_delete_success_title").get());
                            successAlert.setHeaderText(val("alert_delete_success_header").get());
                            successAlert.setContentText(val("alert_delete_success_content").get());
                            successAlert.showAndWait();
                        } else {
                            Alert failureAlert = new Alert(Alert.AlertType.ERROR);
                            failureAlert.setTitle(val("alert_delete_failure_title").get());
                            failureAlert.setHeaderText(val("alert_delete_failure_header").get());
                            failureAlert.setContentText(val("alert_delete_failure_content").get());
                            failureAlert.showAndWait();
                        }
                    }
                });
            }
        });
        return fileDeleteItem;
    }


    private Menu buildViewMenu() {
        var menu = new Menu();
        menu.textProperty().bind(val("menu_view"));

        var viewportMenu = new Menu();
        viewportMenu.textProperty().bind(val("menu_view_views"));

        ToggleGroup viewportGroup = new ToggleGroup();
        for (MugGrid.Viewport value : MugGrid.Viewport.values()) {
            RadioMenuItem item = new RadioMenuItem();
            item.textProperty().bind(val(value.getId()));
            item.setToggleGroup(viewportGroup);
            item.setSelected(mugGrid.getViewport() == value);
            item.setOnAction(_ -> mugGrid.setViewport(value));
            viewportMenu.getItems().add(item);
        }

        menu.getItems().add(viewportMenu);

        var themeGroup = new ToggleGroup();

        var uiThemeMenu = new Menu();
        uiThemeMenu.textProperty().bind(val("menu_view_ui_theme"));

        var darkThemeItem = new RadioMenuItem();
        darkThemeItem.textProperty().bind(val("theme_dark"));
        var lightThemeItem = new RadioMenuItem();
        lightThemeItem.textProperty().bind(val("theme_light"));
        var syncThemeItem = new RadioMenuItem();
        syncThemeItem.textProperty().bind(val("theme_sync"));

        sceneProperty().addListener((_, _, _) -> {
            getScene().windowProperty().addListener((_, _, win) -> {
                var window = (MainWindow) win;
                darkThemeItem.setSelected(Style.DARK == window.getTransitStyle());
                darkThemeItem.setOnAction(_ -> window.setTransitStyle(Style.DARK));

                lightThemeItem.setSelected(Style.LIGHT == window.getTransitStyle());
                lightThemeItem.setOnAction(_ -> window.setTransitStyle(Style.LIGHT));

                syncThemeItem.setSelected(window.isSyncTheme());
                syncThemeItem.setOnAction(_ -> window.setSyncTheme(true));
            });
        });

        darkThemeItem.setToggleGroup(themeGroup);
        lightThemeItem.setToggleGroup(themeGroup);
        syncThemeItem.setToggleGroup(themeGroup);

        uiThemeMenu.getItems().addAll(darkThemeItem, lightThemeItem, syncThemeItem);
        menu.getItems().add(uiThemeMenu);

        return menu;
    }

    private Menu buildHelpMenu() {
        var menu = new Menu();
        menu.textProperty().bind(val("menu_help"));

        var guideItem = new MenuItem();
        guideItem.textProperty().bind(val("menu_help_guide"));
        var aboutItem = new MenuItem();
        aboutItem.textProperty().bind(val("menu_help_about"));

        menu.getItems().add(guideItem);
        menu.getItems().add(aboutItem);

        return menu;
    }
}