package com.dansoftware.mugify.gui;

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

    private final BooleanProperty editorVisible = new SimpleBooleanProperty(true);

    public MainView() {
        mugGrid = new MugGrid();
        menuBar = new MugifyMenuBar(mugGrid);
        mugEditorTabPane = new MugEditorTabPane(mugGrid.getMugTuple());

        mugEditorTabPane.visibleProperty().bind(editorVisible);
        mugEditorTabPane.managedProperty().bind(editorVisible);

        setTop(new VBox(menuBar, new BottomToolbar()));
        setCenter(mugGrid);
        setRight(mugEditorTabPane);

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

    private final class BottomToolbar extends BorderPane {
        private static final String STYLE_CLASS = "bottom-toolbar";

        BottomToolbar() {
            this.setRight(buildEditorToggle());
            getStyleClass().add(TransitStyleClass.BACKGROUND);
            getStyleClass().add(STYLE_CLASS);
        }

        private ToggleButton buildEditorToggle() {
            var toggle = new ToggleButton();
            toggle.setSelected(true);
            toggle.textProperty().bind(val("editor"));
            toggle.selectedProperty().bindBidirectional(MainView.this.editorVisible);
            return toggle;
        }
    }
}
