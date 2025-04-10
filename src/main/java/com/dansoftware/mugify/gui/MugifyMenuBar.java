package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.io.MugIO;
import com.dansoftware.mugify.mug.MugHistory;
import com.dansoftware.mugify.mug.MugRandomizer;
import com.pixelduke.transit.Style;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

import static com.dansoftware.mugify.i18n.I18NUtils.*;

public class MugifyMenuBar extends MenuBar {
    private final MugGrid mugGrid;
    private final MugRandomizer randomizer;
    private final MugHistory mugHistory;
    private Menu recentMenu;

    public MugifyMenuBar(MugGrid mugGrid, MugHistory mugHistory) {
        this.mugGrid = mugGrid;
        this.randomizer = new MugRandomizer();
        this.mugHistory = mugHistory;
        this.mugHistory.addCallback(this::updateRecentMenu);
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

        var generateItem = new MenuItem();
        generateItem.textProperty().bind(val("menu_file_generate"));
        generateItem.setOnAction(_ -> {
            Stage currentStage = (Stage) getScene().getWindow();
            MugHistory.MugEntry currentEntry = mugHistory.getActiveEntryForStage(currentStage);
            if (currentEntry != null)
                mugHistory.removeActive(currentEntry);
            randomizer.apply(mugGrid.getMugTuple());
        });
        generateItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(generateItem);

        var fileOpenItem = fileOpenMenuItem();
        fileOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(fileOpenItem);

        var fileSaveItem = fileSaveMenuItem();
        fileSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(fileSaveItem);

        var fileSaveAsItem = fileSaveAsMenuItem();
        fileSaveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        menu.getItems().add(fileSaveAsItem);

        recentMenu = new Menu();
        recentMenu.textProperty().bind(val("menu_file_recent"));
        updateRecentMenu();
        menu.getItems().add(recentMenu);

        var fileDeleteItem = fileDeleteMenuItem();
        menu.getItems().add(fileDeleteItem);

        var newWindowItem = new MenuItem();
        newWindowItem.textProperty().bind(val("menu_new_window"));
        newWindowItem.setOnAction(_ -> new MainWindow(mugHistory).show());
        menu.getItems().add(newWindowItem);

        return menu;
    }

    private void updateRecentMenu() {
        recentMenu.getItems().clear();
        for (MugHistory.MugEntry entry : mugHistory.listPairs()) {
            MenuItem item = new MenuItem();
            item.setText("%s (%s)".formatted(entry.getName(), entry.getFilePath()));
            item.setOnAction(_ -> {
                if (mugHistory.isActiveFile(entry.getFilePath())) {
                    Stage stage = entry.getStage();
                    if (stage != null) {
                        stage.requestFocus();
                    }
                } else {
                    try {
                        MugIO.loadFromJson(entry.getFilePath(), mugGrid.getMugTuple());
                        Stage currentStage = (Stage) getScene().getWindow();
                        mugHistory.setStage(entry, currentStage);
                        mugHistory.addActive(entry);
                        updateRecentMenu();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            recentMenu.getItems().add(item);
        }
    }

    private MenuItem fileSaveMenuItem() {
        var fileSaveItem = new MenuItem();
        fileSaveItem.textProperty().bind(val("menu_file_save"));
        fileSaveItem.setOnAction(_ -> {
            MugHistory.MugEntry currentEntry = mugHistory.getActiveEntryForStage((Stage) getScene().getWindow());
            if (currentEntry != null) {
                try {
                    MugIO.saveToJson(currentEntry.getFilePath(), mugGrid.getMugTuple());
                    mugHistory.updateEntryName(currentEntry, mugGrid.getMugTuple().getName());
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle(val("alert_save_success_title").get());
                    successAlert.setHeaderText(val("alert_save_success_header").get());
                    successAlert.setContentText(val("alert_save_success_content").get());
                    successAlert.showAndWait();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle(val("filechooser_save_title").get());
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
                );
                fileChooser.setInitialFileName("%s.mugify".formatted(mugGrid.getMugTuple().getName()));
                var outputFile = fileChooser.showSaveDialog(getScene().getWindow());
                if (outputFile != null) {
                    String filePath = outputFile.getAbsolutePath();
                    if (mugHistory.isActiveFile(filePath)) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle(val("alert_save_active_title").get());
                        alert.setHeaderText(val("alert_save_active_header").get());
                        alert.setContentText(val("alert_save_active_content").get());
                        alert.showAndWait();
                    } else {
                        try {
                            MugIO.saveToJson(filePath, mugGrid.getMugTuple());
                            MugHistory.MugEntry entry = mugHistory.findByFilePath(filePath);
                            if (entry == null) {
                                entry = new MugHistory.MugEntry(mugGrid.getMugTuple().getName(), filePath);
                                mugHistory.addMug(entry);
                            }
                            Stage currentStage = (Stage) getScene().getWindow();
                            mugHistory.setStage(entry, currentStage);
                            mugHistory.addActive(entry);
                            updateRecentMenu();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        });
        return fileSaveItem;
    }

    private MenuItem fileSaveAsMenuItem() {
        var fileSaveAsItem = new MenuItem();
        fileSaveAsItem.textProperty().bind(val("menu_file_save_as"));
        fileSaveAsItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(val("filechooser_save_title").get());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
            );
            fileChooser.setInitialFileName("%s.mugify".formatted(mugGrid.getMugTuple().getName()));
            var outputFile = fileChooser.showSaveDialog(getScene().getWindow());
            if (outputFile != null) {
                String filePath = outputFile.getAbsolutePath();
                if (mugHistory.isActiveFile(filePath)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(val("alert_save_active_title").get());
                    alert.setHeaderText(val("alert_save_active_header").get());
                    alert.setContentText(val("alert_save_active_content").get());
                    alert.showAndWait();
                } else {
                    try {
                        MugIO.saveToJson(filePath, mugGrid.getMugTuple());
                        MugHistory.MugEntry entry = mugHistory.findByFilePath(filePath);
                        if (entry == null) {
                            entry = new MugHistory.MugEntry(mugGrid.getMugTuple().getName(), filePath);
                            mugHistory.addMug(entry);
                        }
                        Stage currentStage = (Stage) getScene().getWindow();
                        mugHistory.setStage(entry, currentStage);
                        mugHistory.addActive(entry);
                        updateRecentMenu();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        return fileSaveAsItem;
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
                String filePath = file.getAbsolutePath();
                MugHistory.MugEntry entry = mugHistory.findByFilePath(filePath);
                if (entry != null && mugHistory.isActiveFile(filePath)) {
                    Stage stage = entry.getStage();
                    if (stage != null) {
                        stage.requestFocus();
                    }
                } else {
                    try {
                        MugIO.loadFromJson(filePath, mugGrid.getMugTuple());
                        if (entry == null) {
                            entry = new MugHistory.MugEntry(mugGrid.getMugTuple().getName(), filePath);
                            mugHistory.addMug(entry);
                        }
                        Stage currentStage = (Stage) getScene().getWindow();
                        mugHistory.setStage(entry, currentStage);
                        mugHistory.addActive(entry);
                        updateRecentMenu();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
                String filePath = file.getAbsolutePath();
                MugHistory.MugEntry entry = mugHistory.findByFilePath(filePath);
                Stage currentStage = (Stage) getScene().getWindow();
                if (entry != null && mugHistory.isActiveFile(filePath) && entry.getStage() != currentStage) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle(val("alert_delete_active_title").get());
                    alert.setHeaderText(val("alert_delete_active_header").get());
                    alert.setContentText(val("alert_delete_active_content").get());
                    alert.showAndWait();
                } else {
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle(val("alert_delete_confirm_title").get());
                    confirmAlert.setHeaderText(val("alert_delete_confirm_header").get());
                    confirmAlert.setContentText(val("alert_delete_confirm_content").get());
                    confirmAlert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            if (file.delete()) {
                                mugHistory.deleteByFilePath(filePath);
                                updateRecentMenu();
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