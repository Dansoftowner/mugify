package com.dansoftware.mugify.gui;

import com.pixelduke.transit.TransitStyleClass;
import com.pixelduke.transit.TransitTheme;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class AboutWindow extends Stage {
    private static final String APP_VERSION = "1.0.0";
    private static final String LOGO_ATTRIBUTION_LINK = "https://www.flaticon.com/free-icon/cup_1206954?term=mug&page=2&position=32&origin=search&related_id=1206954";

    public AboutWindow(MainWindow owner) {
        titleProperty().bind(val("menu_help_about"));
        initOwner(owner);
        initModality(Modality.APPLICATION_MODAL);
        getIcons().addAll(owner.getIcons());
        setResizable(false);
        var theme = new TransitTheme();
        theme.styleProperty().bind(owner.transitStyleProperty());

        var logo = new ImageView(new Image(getClass().getResourceAsStream("/com/dansoftware/mugify/img/cup_64px.png")));
        var titleLabel = new Label();
        titleLabel.textProperty().bind(val("about.title"));
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        var versionLabel = new Label();
        versionLabel.textProperty().bind(Bindings.concat(val("about.version"), " ", APP_VERSION));

        var javaInfoLabel = new Label();
        javaInfoLabel.textProperty().bind(Bindings.concat(
                val("about.java"), " ",
                System.getProperty("java.vm.name"), " ",
                System.getProperty("java.version"), " by ",
                System.getProperty("java.vendor")
        ));

        var creatorLabel = new Label();
        creatorLabel.textProperty().bind(Bindings.concat(val("about.creator"), ": ", "Györffy Dániel"));

        var logoCreatorLink = new Hyperlink("App icon is created by Freepik - Flaticon");
        logoCreatorLink.setOnMouseClicked(_ -> {
            owner.getHostServices().showDocument(LOGO_ATTRIBUTION_LINK);
        });

        var copyButton = new Button();
        copyButton.textProperty().bind(val("copy"));
        copyButton.setOnAction(_ -> {
            var cp = new ClipboardContent();
            cp.putString(String.join("\n", "Mugify", versionLabel.getText(), javaInfoLabel.getText()));
            Clipboard.getSystemClipboard().setContent(cp);
        });

        var vBox = new VBox(10, logo, titleLabel, versionLabel, javaInfoLabel, creatorLabel, copyButton, logoCreatorLink);
        vBox.getStyleClass().add(TransitStyleClass.BACKGROUND);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(20));

        var scene = new Scene(vBox);
        setScene(scene);
        setOnShown(_ -> theme.setScene(scene));
    }
}