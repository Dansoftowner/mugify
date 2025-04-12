package com.dansoftware.mugify.gui;

import com.pixelduke.transit.TransitStyleClass;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignA;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MainView extends BorderPane {

    private final MugGrid mugGrid;
    private final MugifyMenuBar menuBar;
    private final MugEditorTabPane mugEditorTabPane;
    private final MugDetailsView mugDetailsView;

    private final BooleanProperty editorVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(true);

    public MainView() {
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(this);
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
}
