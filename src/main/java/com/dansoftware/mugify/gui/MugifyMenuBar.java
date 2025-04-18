package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.config.Preferences;
import com.dansoftware.mugify.io.MugIO;
import com.dansoftware.mugify.mug.MugChangeObserver;
import com.dansoftware.mugify.mug.MugRandomizer;
import com.pixelduke.transit.Style;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.util.Locale;
import java.util.Optional;

import static com.dansoftware.mugify.i18n.I18NUtils.*;
import static com.dansoftware.mugify.util.UncheckedAction.run;

public class MugifyMenuBar extends MenuBar {

    public static final int MAX_RECENT_FILES = 10;

    public enum PersistenceState {
        /**
         * Represents a state when a mug is not saved into any file.
         */
        UNSAVED,

        /**
         * Represents a state when a mug is saved into a file, but some changes are not commited.
         */
        UNSAVED_CHANGES,

        /**
         * Represents a state when a mug is completely saved to the disk.
         */
        SAVED
    }

    private final Preferences preferences;
    private final MainView mainView;
    private final MugGrid mugGrid;
    private final MugRandomizer randomizer;
    private final MugChangeObserver mugChangeObserver;
    private final StringProperty mugFilePath;
    private final ObjectBinding<PersistenceState> persistenceState;
    private final ObservableList<String> recentFiles = FXCollections.synchronizedObservableList(
            FXCollections.observableArrayList()
    );

    public MugifyMenuBar(Preferences preferences, MainView mainView) {
        this.preferences = preferences;
        this.mainView = mainView;
        this.mugFilePath = new SimpleStringProperty();
        this.mugGrid = mainView.getMugGrid();
        this.randomizer = new MugRandomizer();
        this.mugChangeObserver = new MugChangeObserver(mugGrid.getMugTuple());

        this.persistenceState = Bindings.createObjectBinding(() -> {
            if (mugFilePath.get() == null)
                return PersistenceState.UNSAVED;
            else if (mugChangeObserver.isChanged())
                return PersistenceState.UNSAVED_CHANGES;
            else
                return PersistenceState.SAVED;
        }, mugFilePath, mugChangeObserver.changedProperty());

        recentFiles.addAll(preferences.getRecentFiles());
        preferences.setRecentFiles(recentFiles); // persisting recent files

        this.initWindowCloseMechanism();
        this.initWindowTitleMechanism();
        this.buildMenuStructure();
    }

    private void initWindowCloseMechanism() {
        EventHandler<WindowEvent> closeRequestHandler = (event) -> {
            if (persistenceState.get() == PersistenceState.UNSAVED_CHANGES)
                if (!showUnsavedChangesAlert())
                    event.consume();
        };

        sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                newValue.windowProperty().addListener(new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                        var stage = (Stage) newValue;
                        stage.setOnCloseRequest(closeRequestHandler);
                        observable.removeListener(this);
                    }
                });
                observable.removeListener(this);
            }
        });
    }

    private void initWindowTitleMechanism() {
        sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
                newValue.windowProperty().addListener(new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
                        var stage = (Stage) newValue;
                        String baseTitle = stage.getTitle();

                        StringBinding newTitle = Bindings.createStringBinding(() -> {
                            if (mugFilePath.get() == null)
                                return baseTitle;

                            String changeIndicator = persistenceState.get() == PersistenceState.UNSAVED_CHANGES ?
                                                            "*" : "";

                            return "%s - %s%s".formatted(baseTitle, changeIndicator, mugFilePath.get());
                        }, mugFilePath, persistenceState);

                        stage.titleProperty().bind(newTitle);
                        observable.removeListener(this);
                    }
                });
                observable.removeListener(this);
            }
        });
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
        menu.setGraphic(new FontIcon(MaterialDesignG.GOOGLE_TRANSLATE));

        ToggleGroup languageGroup = new ToggleGroup();

        for (Locale locale : getSupportedLocales()) {
            RadioMenuItem item = new RadioMenuItem(locale.getDisplayLanguage());
            item.setToggleGroup(languageGroup);
            item.setOnAction(_ -> setLocale(locale));
            if (locale.equals(Locale.getDefault()))
                item.setSelected(true);
            menu.getItems().add(item);
            localeProperty().addListener((_, _, newLocale) -> {
                item.setSelected(locale.equals(newLocale));
            });
        }

        return menu;
    }

    private Menu buildFileMenu() {
        var menu = new Menu();
        menu.textProperty().bind(val("menu_file"));
        menu.setGraphic(new FontIcon(MaterialDesignF.FOLDER));

        var fileOpenItem = fileOpenMenuItem();
        fileOpenItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(fileOpenItem);

        menu.getItems().add(buildRecentFilesMenu());

        var fileSaveItem = fileSaveMenuItem();
        fileSaveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(fileSaveItem);

        var fileSaveAsItem = fileSaveAsMenuItem();
        fileSaveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        menu.getItems().add(fileSaveAsItem);

        var generateItem = generateItem();
        generateItem.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
        menu.getItems().add(generateItem);

        var fileDeleteItem = fileDeleteMenuItem();
        menu.getItems().add(fileDeleteItem);

        return menu;
    }

    private Menu buildRecentFilesMenu() {
        Menu menu = new Menu();
        menu.textProperty().bind(val("menu_file_recent"));
        menu.setGraphic(new FontIcon(MaterialDesignH.HISTORY));
        menu.setOnShowing(_ -> updateRecentFilesMenu(menu));
        updateRecentFilesMenu(menu);
        return menu;
    }

    private void updateRecentFilesMenu(Menu menu) {
        menu.getItems().clear();
        if (recentFiles.isEmpty()) {
            MenuItem item = new MenuItem();
            item.textProperty().bind(val("no_recent_files"));
            item.setDisable(true);
            menu.getItems().add(item);
        } else {
            for (String filePath : recentFiles) {
                MenuItem item = new MenuItem(filePath);
                item.setOnAction(_ -> openFile(filePath));
                menu.getItems().add(item);
            }
        }
    }

    private void addToRecentFiles(String filePath) {
        recentFiles.remove(filePath);
        recentFiles.addFirst(filePath);
        if (recentFiles.size() > MAX_RECENT_FILES) {
            recentFiles.remove(MAX_RECENT_FILES);
        }
    }

    private void openFile(String filePath) {
        if (persistenceState.get() == PersistenceState.UNSAVED_CHANGES) {
            if (!showUnsavedChangesAlert()) {
                return;
            }
        }
        run(() -> MugIO.loadFromJson(filePath, mugGrid.getMugTuple()));
        mugChangeObserver.commit();
        mugFilePath.set(filePath);
        addToRecentFiles(filePath);
    }

    private MenuItem fileSaveMenuItem() {
        var fileSaveItem = new MenuItem();
        fileSaveItem.textProperty().bind(val("menu_file_save"));
        fileSaveItem.setGraphic(new FontIcon(MaterialDesignF.FLOPPY));
        fileSaveItem.disableProperty().bind(mugChangeObserver.changedProperty().not());
        fileSaveItem.setOnAction(_ -> {
            if (persistenceState.get() == PersistenceState.UNSAVED) {
                if (saveMugToNewFile())
                    mugChangeObserver.commit();
            } else if (persistenceState.get() == PersistenceState.UNSAVED_CHANGES) {
                run(() -> MugIO.saveToJson(mugFilePath.get(), mugGrid.getMugTuple()));
                mugChangeObserver.commit();
            }
        });
        return fileSaveItem;
    }

    private MenuItem fileSaveAsMenuItem() {
        var fileSaveAsItem = new MenuItem();
        fileSaveAsItem.textProperty().bind(val("menu_file_save_as"));
        fileSaveAsItem.setGraphic(new FontIcon(MaterialDesignF.FLOPPY));
        fileSaveAsItem.setOnAction(_ -> {
            if (saveMugToNewFile())
                mugChangeObserver.commit();
        });
        return fileSaveAsItem;
    }

    private boolean saveMugToNewFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(val("filechooser_save_title").get());
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
        );
        fileChooser.setInitialFileName("%s.mugify".formatted(mugGrid.getMugTuple().getName()));
        var outputFile = fileChooser.showSaveDialog(getScene().getWindow());
        if (outputFile != null) {
            run(() -> MugIO.saveToJson(outputFile.getAbsolutePath(), mugGrid.getMugTuple()));
            mugFilePath.set(outputFile.getAbsolutePath());
            addToRecentFiles(outputFile.getAbsolutePath());
            return true;
        }
        return false;
    }

    private MenuItem fileOpenMenuItem() {
        var fileOpenItem = new MenuItem();
        fileOpenItem.textProperty().bind(val("menu_file_open"));
        fileOpenItem.setGraphic(new FontIcon(MaterialDesignF.FOLDER_OPEN));
        fileOpenItem.setOnAction(_ -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle(val("filechooser_open_title").get());
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter(val("filechooser_mugify_filter").get(), "*.mugify")
            );
            var file = fileChooser.showOpenDialog(getScene().getWindow());
            if (file != null)
                openFile(file.getAbsolutePath());
        });
        return fileOpenItem;
    }

    private MenuItem generateItem() {
        var generateItem = new MenuItem();
        generateItem.textProperty().bind(val("menu_file_generate"));
        generateItem.setGraphic(new FontIcon(MaterialDesignS.SHUFFLE));
        generateItem.setOnAction(_ -> {
            if (persistenceState.get() == PersistenceState.UNSAVED_CHANGES) {
                // if current mug isn't saved
                if (!showUnsavedChangesAlert()) {
                    return;
                }
            }

            randomizer.apply(mugGrid.getMugTuple());
            mugFilePath.set(null);
            mainView.getMugGrid().playBounceAnimation();
        });
        return generateItem;
    }

    private MenuItem fileDeleteMenuItem() {
        var fileDeleteItem = new MenuItem();
        fileDeleteItem.textProperty().bind(val("menu_file_delete"));
        fileDeleteItem.setGraphic(new FontIcon(MaterialDesignD.DELETE));
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
                            if (file.getAbsolutePath().equals(mugFilePath.get())) {
                                // if it is the currently opened one
                                mugFilePath.set(null);
                            }

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
        menu.setGraphic(new FontIcon(MaterialDesignV.VIEW_GRID));

        var mugDetailsItem = new CheckMenuItem();
        mugDetailsItem.textProperty().bind(val("mug_details"));
        mugDetailsItem.setGraphic(new FontIcon(MaterialDesignI.INFORMATION));
        mugDetailsItem.selectedProperty().bindBidirectional(mainView.detailsVisibleProperty());
        mugDetailsItem.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN));

        menu.getItems().add(mugDetailsItem);

        var mugEditorItem = new CheckMenuItem();
        mugEditorItem.textProperty().bind(val("mug_details"));
        mugEditorItem.setGraphic(new FontIcon(MaterialDesignP.PENCIL));
        mugEditorItem.selectedProperty().bindBidirectional(mainView.editorVisibleProperty());
        mugEditorItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN));

        menu.getItems().add(mugEditorItem);

        var viewportMenu = new Menu();
        viewportMenu.textProperty().bind(val("menu_view_views"));
        viewportMenu.setGraphic(new FontIcon(MaterialDesignE.EYE));

        ToggleGroup viewportGroup = new ToggleGroup();
        for (MugGrid.Viewport value : MugGrid.Viewport.values()) {
            RadioMenuItem item = new RadioMenuItem();
            item.setGraphic(new FontIcon(value.getIcon()));
            item.textProperty().bind(val(value.getId()));
            item.setToggleGroup(viewportGroup);
            item.setAccelerator(value.getKeyCombination());
            mugGrid.viewportProperty().addListener((_, _, newValue) -> {
                item.setSelected(newValue == value);
            });
            item.setSelected(mugGrid.getViewport() == value);
            item.setOnAction(_ -> mugGrid.setViewport(value));
            viewportMenu.getItems().add(item);
        }

        menu.getItems().add(viewportMenu);

        var themeGroup = new ToggleGroup();

        var uiThemeMenu = new Menu();
        uiThemeMenu.textProperty().bind(val("menu_view_ui_theme"));
        uiThemeMenu.setGraphic(new FontIcon(MaterialDesignT.THEME_LIGHT_DARK));

        var darkThemeItem = new RadioMenuItem();
        darkThemeItem.textProperty().bind(val("theme_dark"));
        darkThemeItem.setGraphic(new FontIcon(MaterialDesignM.MOON_WANING_CRESCENT));

        var lightThemeItem = new RadioMenuItem();
        lightThemeItem.textProperty().bind(val("theme_light"));
        lightThemeItem.setGraphic(new FontIcon(MaterialDesignW.WHITE_BALANCE_SUNNY));

        var syncThemeItem = new RadioMenuItem();
        syncThemeItem.textProperty().bind(val("theme_sync"));
        syncThemeItem.setGraphic(new FontIcon(MaterialDesignS.SYNC));

        sceneProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene oldScene, Scene newScene) {
                newScene.windowProperty().addListener(new ChangeListener<>() {
                    @Override
                    public void changed(ObservableValue<? extends Window> observableValue, Window oldWin, Window win) {
                        var window = (MainWindow) win;
                        darkThemeItem.setSelected(Style.DARK == window.getTransitStyle());
                        darkThemeItem.setOnAction(_ -> window.setTransitStyle(Style.DARK));

                        lightThemeItem.setSelected(Style.LIGHT == window.getTransitStyle());
                        lightThemeItem.setOnAction(_ -> window.setTransitStyle(Style.LIGHT));

                        syncThemeItem.setSelected(window.isSyncTheme());
                        syncThemeItem.setOnAction(_ -> window.setSyncTheme(true));

                        ChangeListener<? super Object> themeListener = (_, _, _) -> {
                            darkThemeItem.setSelected(Style.DARK == window.getTransitStyle());
                            lightThemeItem.setSelected(Style.LIGHT == window.getTransitStyle());
                            syncThemeItem.setSelected(window.isSyncTheme());
                        };

                        window.transitStyleProperty().addListener(themeListener);
                        window.syncThemeProperty().addListener(themeListener);

                        observableValue.removeListener(this);
                    }
                });
                observableValue.removeListener(this);
            }
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
        menu.setGraphic(new FontIcon(MaterialDesignH.HELP));

        var guideItem = new MenuItem();
        guideItem.textProperty().bind(val("menu_help_guide"));
        guideItem.setGraphic(new FontIcon(MaterialDesignH.HELP_CIRCLE));
        guideItem.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
        guideItem.setOnAction(_ -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof UserGuideWindow) {
                    window.requestFocus();
                    return;
                }
            }

            var userGuideWindow = new UserGuideWindow((MainWindow) this.getScene().getWindow());
            userGuideWindow.show();
        });

        var aboutItem = new MenuItem();
        aboutItem.textProperty().bind(val("menu_help_about"));
        aboutItem.setGraphic(new FontIcon(MaterialDesignI.INFORMATION));
        aboutItem.setOnAction(e -> new AboutWindow(((MainWindow) this.getScene().getWindow())).show());

        menu.getItems().add(guideItem);
        menu.getItems().add(aboutItem);

        return menu;
    }

    /**
     *
     * @return {@code true} if user said 'OK' (meaning he doesn't care about unsaved changes)
     */
    private boolean showUnsavedChangesAlert() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(getScene().getWindow());
        alert.setTitle(val("unsaved.changes.dialog.title").get());
        alert.setHeaderText(val("unsaved.changes.dialog.header").get());
        alert.setContentText(val("unsaved.changes.dialog.content").get());
        ButtonType yesButton = new ButtonType(val("unsaved.changes.dialog.yes").get(), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(val("unsaved.changes.dialog.cancel").get(), ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.OK_DONE;
    }

    public ObjectBinding<PersistenceState> persistenceState() {
        return this.persistenceState;
    }
}