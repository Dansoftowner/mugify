package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.config.Preferences;
import com.dansoftware.mugify.i18n.I18NUtils;
import com.jthemedetecor.OsThemeDetector;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;
import javafx.application.HostServices;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.List;
import java.util.function.Consumer;

public class MainWindow extends Stage {
    private static final String STYLESHEET = MainWindow.class.getResource("/com/dansoftware/mugify/css/styles.css").toExternalForm();
    private static final String DARK_STYLESHEET = MainWindow.class.getResource("/com/dansoftware/mugify/css/dark.css").toExternalForm();
    private static final String LIGHT_STYLESHEET = MainWindow.class.getResource("/com/dansoftware/mugify/css/light.css").toExternalForm();

    private final TransitTheme transitTheme;
    private final HostServices hostServices;

    private final SimpleBooleanProperty syncTheme = new SimpleBooleanProperty(this, "syncTheme");
    private final Consumer<Boolean> osThemeListener;

    private final Preferences preferences;

    public MainWindow(Preferences preferences, HostServices hostServices) {
        this.preferences = preferences;
        this.hostServices = hostServices;
        this.transitTheme = new TransitTheme(preferences.getTheme());
        this.osThemeListener = isDark -> applyUIStyle(isDark ? Style.DARK : Style.LIGHT);

        var mainView = new MainView(preferences);

        var scene = new Scene(mainView);
        scene.getStylesheets().add(STYLESHEET);
        scene.setFill(Color.TRANSPARENT);

        setTitle("Mugify");
        setScene(scene);

        setOnShown(_ -> {
            this.transitTheme.setScene(scene);
            if (preferences.isSyncTheme())
                setSyncTheme(true);
            else
                applyUIStyle(preferences.getTheme());

            mainView.getMugGrid().playStartupAnimation();
        });

        initLocalePersistence();

        setSize();
        centerOnScreen();
        initIcon();
    }

    private void initLocalePersistence() {
        I18NUtils.setLocale(preferences.getLocale());
        I18NUtils.localeProperty().addListener(
                (_, _, newLocale) -> preferences.setLocale(newLocale));
    }

    private void setSize() {
        var screen = Screen.getPrimary();

        double width = (3.0 / 4) * screen.getBounds().getWidth();
        double height = (3.0 / 4) * screen.getBounds().getHeight();

        setWidth(width);
        setHeight(height);
    }

    private void initIcon() {
        for (int resolution : List.of(16, 24, 32, 64, 256)) {
            System.out.println();
            this.getIcons().add(new Image(getClass().getResourceAsStream("/com/dansoftware/mugify/img/cup_%dpx.png".formatted(resolution))));
        }
    }

    public boolean isSyncTheme() {
        return syncTheme.get();
    }

    public void setSyncTheme(boolean syncTheme) {
        if (this.syncTheme.get() == syncTheme)
            return;

        this.syncTheme.set(syncTheme);
        this.preferences.setSyncTheme(syncTheme);

        if (syncTheme)
            OsThemeDetector.getDetector().registerListener(osThemeListener);
        else
            OsThemeDetector.getDetector().removeListener(osThemeListener);
        applyUIStyle(OsThemeDetector.getDetector().isDark() ? Style.DARK : Style.LIGHT);
    }

    public ReadOnlyBooleanProperty syncThemeProperty() {
        return syncTheme;
    }

    /**
     * Sets the UI style and turns os-theme synchronization off (if necessary)
     */
    public void setTransitStyle(Style style) {
        setSyncTheme(false);
        applyUIStyle(style);
    }

    /**
     * Applies the transit theme's style and the custom css assets as well.
     *
     * @param style
     */
    private void applyUIStyle(Style style) {
        transitTheme.setStyle(style);
        switch (style) {
            case LIGHT -> {
                getScene().getStylesheets().remove(DARK_STYLESHEET);
                getScene().getStylesheets().add(LIGHT_STYLESHEET);
            }
            case DARK -> {
                getScene().getStylesheets().remove(LIGHT_STYLESHEET);
                getScene().getStylesheets().add(DARK_STYLESHEET);
            }
        }
        preferences.setTheme(style);
    }

    public Style getTransitStyle() {
        return transitTheme.getStyle();
    }

    public ReadOnlyObjectProperty<Style> transitStyleProperty() {
        return transitTheme.styleProperty();
    }

    public HostServices getHostServices() {
        return hostServices;
    }
}
