package com.dansoftware.mugify.gui;

import com.pixelduke.transit.Style;
import com.pixelduke.transit.TransitStyleClass;
import com.pixelduke.transit.TransitTheme;
import javafx.application.HostServices;
import javafx.beans.binding.Bindings;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;
import one.jpro.platform.mdfx.MarkdownView;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class UserGuideWindow extends Stage {

    private static final String USER_GUIDE_MD_LOC = "/com/dansoftware/mugify/userguide/user_guide.md";
    private static final String DARK_CSS = UserGuideWindow.class.getResource("/com/dansoftware/mugify/css/md-dark.css").toExternalForm();
    private static final String LIGHT_CSS = UserGuideWindow.class.getResource("/com/dansoftware/mugify/css/md-light.css").toExternalForm();

    public UserGuideWindow(MainWindow owner) {
        this.titleProperty().bind(Bindings.concat("Mugify - ", val("menu_help_guide")));
        this.getIcons().addAll(owner.getIcons());

        var theme = new TransitTheme();
        theme.styleProperty().bind(owner.transitStyleProperty());

        String markdownString = readMarkdownString(USER_GUIDE_MD_LOC);

        var mdView = buildMarkdownView(markdownString, owner.getHostServices());
        mdView.getStyleClass().add(TransitStyleClass.BACKGROUND);

        var scrollPane = new ScrollPane(mdView);
        scrollPane.getStyleClass().add(TransitStyleClass.BACKGROUND);
        scrollPane.setFitToWidth(true);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);


        var scene = new Scene(scrollPane);

        theme.styleProperty().addListener((_, _, newStyle) -> {
            switch (newStyle) {
                case LIGHT -> {
                    scene.getStylesheets().remove(DARK_CSS);
                    scene.getStylesheets().add(LIGHT_CSS);
                }
                case DARK -> {
                    scene.getStylesheets().remove(LIGHT_CSS);
                    scene.getStylesheets().add(DARK_CSS);
                }
            }
        });

        this.setScene(scene);
        this.setOnShown(_ -> {
            theme.setScene(scene);
            scene.getStylesheets().add(theme.getStyle() == Style.DARK ? DARK_CSS : LIGHT_CSS);
        });

        this.centerOnScreen();
        this.setWidth(830);
        this.setHeight(Screen.getPrimary().getVisualBounds().getHeight() * 2 / 3);
    }

    private String readMarkdownString(String markdownLocation) {
        try (var reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(markdownLocation)))) {
            StringBuilder output = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append('\n');
            }

            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MarkdownView buildMarkdownView(String markdownString, HostServices hostServices) {
        return new MarkdownView(markdownString) {
            @Override
            public void setLink(Node node, String link, String description) {
                node.setCursor(Cursor.HAND);
                node.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY)
                        hostServices.showDocument(link);
                });
            }

            @Override
            public Node generateImage(String url) {
                if (url.startsWith("mdi:")) {
                    String enumClassName = "org.kordamp.ikonli.materialdesign2.MaterialDesign" + url.charAt("mdi:".length());
                    try {
                        Class<?> clazz = Class.forName(enumClassName);
                        Field field = clazz.getField(url.substring("mdi:".length()));
                        Ikon icon = (Ikon) field.get(null);
                        return new FontIcon(icon);
                    } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace(System.err);
                        return new Group();
                    }
                }
                return super.generateImage(UserGuideWindow.class.getResource("/com/dansoftware/mugify/userguide/%s".formatted(url)).toExternalForm());
            }
        };
    }
}
