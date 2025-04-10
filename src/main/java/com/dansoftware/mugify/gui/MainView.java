package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.MugHistory;
import com.pixelduke.transit.TransitStyleClass;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MainView extends BorderPane {

    private final MugGrid mugGrid;
    private final MugifyMenuBar menuBar;
    private final MugEditorTabPane mugEditorTabPane;
    private final MugDetailsView mugDetailsView;

    private final BooleanProperty editorVisible = new SimpleBooleanProperty(true);
    private final BooleanProperty detailsVisible = new SimpleBooleanProperty(true);

    public MainView(MugHistory mugHistory) {
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(mugGrid, mugHistory);
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

    public MugGrid getMugGrid() {
        return mugGrid;
    }

    public MugifyMenuBar getMenuBar() {
        return menuBar;
    }

    private final class TopToolbar extends BorderPane {
        private static final String STYLE_CLASS = "top-toolbar";

        TopToolbar() {
            this.setLeft(buildEditorToggle("mug_details", MainView.this.detailsVisible));
            this.setRight(buildEditorToggle("editor", MainView.this.editorVisible));
            getStyleClass().add(TransitStyleClass.BACKGROUND);
            getStyleClass().add(STYLE_CLASS);
        }

        private ToggleButton buildEditorToggle(String i18nProp, BooleanProperty target) {
            var toggle = new ToggleButton();
            toggle.setSelected(true);
            toggle.textProperty().bind(val(i18nProp));
            toggle.selectedProperty().bindBidirectional(target);
            return toggle;
        }
    }
}
