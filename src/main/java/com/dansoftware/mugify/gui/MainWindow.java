package com.dansoftware.mugify.gui;

import com.jthemedetecor.OsThemeDetector;
import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitTheme;
import javafx.application.HostServices;
import javafx.beans.property.ReadOnlyObjectProperty;
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

    private static final Style DEFAULT_UI_STYLE = Style.DARK;

    private final TransitTheme transitTheme;
    private final HostServices hostServices;

    private boolean syncTheme;
    private final Consumer<Boolean> osThemeListener;


    public MainWindow(HostServices hostServices) {
        this.hostServices = hostServices;
        this.transitTheme = new TransitTheme(DEFAULT_UI_STYLE);
        this.osThemeListener = isDark -> applyUIStyle(isDark ? Style.DARK : Style.LIGHT);

        var mainView = new MainView();

        var scene = new Scene(mainView);
        scene.getStylesheets().add(STYLESHEET);
        scene.setFill(Color.TRANSPARENT);

        setTitle("Mugify");
        setScene(scene);

        setOnShown(_ -> {
            this.transitTheme.setScene(scene);
            applyUIStyle(DEFAULT_UI_STYLE); // to make the custom css applied
        });

        setSize();
        centerOnScreen();
        initIcon();
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
        return syncTheme;
    }

    public void setSyncTheme(boolean syncTheme) {
        if (this.syncTheme == syncTheme)
            return;

        this.syncTheme = syncTheme;
        if (syncTheme)
            OsThemeDetector.getDetector().registerListener(osThemeListener);
        else
            OsThemeDetector.getDetector().removeListener(osThemeListener);
        setTransitStyle(OsThemeDetector.getDetector().isDark() ? Style.DARK : Style.LIGHT);
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
