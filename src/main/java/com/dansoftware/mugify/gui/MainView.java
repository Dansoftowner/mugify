package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.config.Preferences;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitStyleClass;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.util.Locale;

import static com.dansoftware.mugify.i18n.I18NUtils.*;

public class MainView extends BorderPane {

    private final MugGrid mugGrid;
    private final MugifyMenuBar menuBar;
    private final MugEditorTabPane mugEditorTabPane;
    private final MugDetailsView mugDetailsView;

    private final BooleanProperty editorVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(true);

    public MainView(Preferences preferences) {
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(preferences, this);
        mugEditorTabPane = new MugEditorTabPane(mugGrid.getMugTuple());
        mugDetailsView = new MugDetailsView(mugGrid.getMugTuple());

        mugEditorTabPane.visibleProperty().bind(editorVisible);
        mugEditorTabPane.managedProperty().bind(editorVisible);

        mugDetailsView.visibleProperty().bind(detailsVisible);
        mugDetailsView.managedProperty().bind(detailsVisible);

        setTop(new VBox(menuBar, new TopToolbar()));
        setCenter(mugGrid);
        setRight(mugEditorTabPane);
        setLeft(mugDetailsView);
        setBottom(new BottomToolbar());

        // for proper responsiveness
        mugGrid.maxWidthProperty().bind(this.widthProperty());
        mugGrid.maxHeightProperty().bind(this.heightProperty());
        mugGrid.setMinSize(0, 0);
    }

    public boolean isEditorVisible() {
        return editorVisible.get();
    }

    public BooleanProperty editorVisibleProperty() {
        return editorVisible;
    }

    public void setEditorVisible(boolean editorVisible) {
        this.editorVisible.set(editorVisible);
    }

    public boolean isDetailsVisible() {
        return detailsVisible.get();
    }

    public BooleanProperty detailsVisibleProperty() {
        return detailsVisible;
    }

    public void setDetailsVisible(boolean detailsVisible) {
        this.detailsVisible.set(detailsVisible);
    }

    public MugGrid getMugGrid() {
        return mugGrid;
    }

    private final class TopToolbar extends BorderPane {
        private static final String STYLE_CLASS = "top-toolbar";

        TopToolbar() {
            this.setLeft(buildEditorToggle("mug_details", MainView.this.detailsVisible, new FontIcon(MaterialDesignC.COFFEE)));
            this.setRight(buildEditorToggle("editor", MainView.this.editorVisible, new FontIcon(MaterialDesignP.PENCIL)));
            this.setCenter(buildPersistenceLabel());
            getStyleClass().add(TransitStyleClass.BACKGROUND);
            getStyleClass().add(STYLE_CLASS);
        }

        private Node buildPersistenceLabel() {
            StackPane container = new StackPane();

            Label unsavedLabel = new Label();
            unsavedLabel.setGraphic(new FontIcon(MaterialDesignF.FILE_HIDDEN));
            unsavedLabel.textProperty().bind(val("persistence_unsaved"));
            unsavedLabel.getStyleClass().addAll("persistence-label", "persistence-unsaved-label");
            unsavedLabel.visibleProperty().bind(
                    menuBar.persistenceState().isEqualTo(MugifyMenuBar.PersistenceState.UNSAVED));
            unsavedLabel.managedProperty().bind(
                    menuBar.persistenceState().isEqualTo(MugifyMenuBar.PersistenceState.UNSAVED));

            Label unsavedChangesLabel = new Label();
            unsavedChangesLabel.setGraphic(new FontIcon(MaterialDesignA.ALERT));
            unsavedChangesLabel.textProperty().bind(val("persistence_unsaved_changes"));
            unsavedChangesLabel.getStyleClass().addAll("persistence-label", "persistence-unsaved-changes-label");
            unsavedChangesLabel.visibleProperty().bind(
                    menuBar.persistenceState().isEqualTo(MugifyMenuBar.PersistenceState.UNSAVED_CHANGES));
            unsavedChangesLabel.managedProperty().bind(
                    menuBar.persistenceState().isEqualTo(MugifyMenuBar.PersistenceState.UNSAVED_CHANGES));

            Label savedChangesLabel = new Label();
            savedChangesLabel.setGraphic(new FontIcon(MaterialDesignC.CHECK_BOLD));
            savedChangesLabel.textProperty().bind(val("persistence_saved"));
            savedChangesLabel.getStyleClass().addAll("persistence-label", "persistence-saved-label");
            savedChangesLabel.visibleProperty().bind(
                    menuBar.persistenceState().isEqualTo(MugifyMenuBar.PersistenceState.SAVED));
            savedChangesLabel.managedProperty().bind(
                    menuBar.persistenceState().isEqualTo(MugifyMenuBar.PersistenceState.SAVED));

            container.getChildren().addAll(unsavedLabel, unsavedChangesLabel, savedChangesLabel);
            return container;
        }

        private ToggleButton buildEditorToggle(String i18nProp, BooleanProperty target, Node graphic) {
            var toggle = new ToggleButton();
            toggle.setGraphic(graphic);
            toggle.setSelected(true);
            toggle.textProperty().bind(val(i18nProp));
            toggle.selectedProperty().bindBidirectional(target);
            return toggle;
        }
    }

    private final class BottomToolbar extends BorderPane {

        BottomToolbar() {
            getStyleClass().add(TransitStyleClass.BACKGROUND);

            var themeButton = buildThemeButton();
            var languageButton = buildLanguageButton();

            setCenter(new Group(new HBox(10, themeButton, languageButton)));
        }

        private MenuButton buildThemeButton() {
            var themeGroup = new ToggleGroup();
            var darkThemeItem = new RadioMenuItem();
            darkThemeItem.textProperty().bind(val("theme_dark"));
            darkThemeItem.setGraphic(new FontIcon(MaterialDesignM.MOON_WANING_CRESCENT));

            var lightThemeItem = new RadioMenuItem();
            lightThemeItem.textProperty().bind(val("theme_light"));
            lightThemeItem.setGraphic(new FontIcon(MaterialDesignW.WHITE_BALANCE_SUNNY));

            var syncThemeItem = new RadioMenuItem();
            syncThemeItem.textProperty().bind(val("theme_sync"));
            syncThemeItem.setGraphic(new FontIcon(MaterialDesignS.SYNC));

            MenuButton themeButton = new MenuButton();
            themeButton.textProperty().bind(val("menu_view_ui_theme"));
            themeButton.getItems().addAll(darkThemeItem, lightThemeItem, syncThemeItem);

            themeButton.setGraphic(new FontIcon(MaterialDesignT.THEME_LIGHT_DARK));

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

                            window.transitStyleProperty().addListener((_, _, _) -> {
                                if (!window.isSyncTheme()) {
                                    darkThemeItem.setSelected(Style.DARK == window.getTransitStyle());
                                    lightThemeItem.setSelected(Style.LIGHT == window.getTransitStyle());
                                }
                            });

                            window.syncThemeProperty().addListener((_, _, syncTheme) -> {
                                syncThemeItem.setSelected(syncTheme);
                            });

                            observableValue.removeListener(this);
                        }
                    });
                    observableValue.removeListener(this);
                }
            });

            darkThemeItem.setToggleGroup(themeGroup);
            lightThemeItem.setToggleGroup(themeGroup);
            syncThemeItem.setToggleGroup(themeGroup);

            return themeButton;
        }

        private MenuButton buildLanguageButton() {
            var menu = new MenuButton();
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

    }
}
