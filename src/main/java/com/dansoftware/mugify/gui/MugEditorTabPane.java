package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.MugBoundaries;
import com.dansoftware.mugify.mug.MugLike;
import com.pixelduke.transit.TransitStyleClass;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MugEditorTabPane extends TabPane {
    private final MugLike mug;

    public MugEditorTabPane(MugLike mug) {
        this.mug = mug;
        buildTabs();
        setSide(Side.RIGHT);
        getStyleClass().add(TransitStyleClass.UNDERLINE_TAB_PANE);
        getStyleClass().add(TransitStyleClass.BACKGROUND);
    }

    private void buildTabs() {
        getTabs().add(buildBodyTab());
        getTabs().add(buildInteriorTab());
        getTabs().add(buildHandleTab());
        getTabs().add(buildBottomTab());
    }

    private Tab buildBodyTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.textProperty().bind(val("tab_body"));
        GridPane grid = createGridPane();

        Label titleLabel = createTitleLabel("tab_body");
        grid.add(titleLabel, 0, 0, 2, 1);

        Label heightLabel = createLabel("mug_height");
        Slider heightSlider = createSlider(MugBoundaries.MIN_HEIGHT, MugBoundaries.MAX_HEIGHT, mug.heightProperty());
        heightSlider.setOrientation(Orientation.VERTICAL);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        addToGrid(grid, heightLabel, heightSlider, 1);

        Label radiusLabel = createLabel("mug_radius");
        Slider radiusSlider = createSlider(MugBoundaries.MIN_RADIUS, MugBoundaries.MAX_RADIUS, mug.radiusProperty());
        radiusSlider.setShowTickLabels(true);
        radiusSlider.setShowTickMarks(true);
        addToGrid(grid, radiusLabel, radiusSlider, 2);

        Label outerColorLabel = createLabel("mug_outer_color");
        ColorPicker outerColorPicker = createColorPicker(mug.outerColorProperty());
        addToGrid(grid, outerColorLabel, outerColorPicker, 3);

        tab.setContent(createScrollPane(grid));
        return tab;
    }

    private Tab buildInteriorTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.textProperty().bind(val("tab_interior"));
        GridPane grid = createGridPane();

        Label titleLabel = createTitleLabel("tab_interior");
        grid.add(titleLabel, 0, 0, 2, 1);

        Label borderThicknessLabel = createLabel("mug_border_thickness");
        Slider borderThicknessSlider = createSlider(MugBoundaries.MIN_BORDER_THICKNESS, MugBoundaries.MAX_BORDER_THICKNESS, mug.borderThicknessProperty());
        borderThicknessSlider.setShowTickLabels(true);
        borderThicknessSlider.setShowTickMarks(true);
        addToGrid(grid, borderThicknessLabel, borderThicknessSlider, 1);

        Label innerColorLabel = createLabel("mug_inner_color");
        ColorPicker innerColorPicker = createColorPicker(mug.innerColorProperty());
        addToGrid(grid, innerColorLabel, innerColorPicker, 2);

        tab.setContent(createScrollPane(grid));
        return tab;
    }

    private Tab buildHandleTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.textProperty().bind(val("tab_handle"));
        GridPane grid = createGridPane();

        Label titleLabel = createTitleLabel("tab_handle");
        grid.add(titleLabel, 0, 0, 2, 1);

        Label handleRadiusLabel = createLabel("mug_handle_radius");
        Slider handleRadiusSlider = createSlider(MugBoundaries.MIN_HANDLE_RADIUS, MugBoundaries.MAX_HANDLE_RADIUS, mug.handleRadiusProperty());
        handleRadiusSlider.setShowTickLabels(true);
        handleRadiusSlider.setShowTickMarks(true);
        addToGrid(grid, handleRadiusLabel, handleRadiusSlider, 1);

        Label handleWidthLabel = createLabel("mug_handle_width");
        Slider handleWidthSlider = createSlider(MugBoundaries.MIN_HANDLE_WIDTH, MugBoundaries.MAX_HANDLE_WIDTH, mug.handleWidthProperty());
        handleWidthSlider.setShowTickLabels(true);
        handleWidthSlider.setShowTickMarks(true);
        addToGrid(grid, handleWidthLabel, handleWidthSlider, 2);

        Label handleColorLabel = createLabel("mug_handle_color");
        ColorPicker handleColorPicker = createColorPicker(mug.handleColorProperty());
        addToGrid(grid, handleColorLabel, handleColorPicker, 3);

        tab.setContent(createScrollPane(grid));
        return tab;
    }

    private Tab buildBottomTab() {
        Tab tab = new Tab();
        tab.setClosable(false);
        tab.textProperty().bind(val("tab_bottom"));
        GridPane grid = createGridPane();

        Label titleLabel = createTitleLabel("tab_bottom");
        grid.add(titleLabel, 0, 0, 2, 1);

        Label bottomColorLabel = createLabel("mug_bottom_color");
        ColorPicker bottomColorPicker = createColorPicker(mug.bottomColorProperty());
        addToGrid(grid, bottomColorLabel, bottomColorPicker, 1);

        tab.setContent(createScrollPane(grid));
        return tab;
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        return grid;
    }

    private Label createLabel(String key) {
        Label label = new Label();
        label.textProperty().bind(val(key));
        return label;
    }

    private Label createTitleLabel(String key) {
        Label label = new Label();
        label.textProperty().bind(val(key));
        label.getStyleClass().add("title-label");
        return label;
    }

    private Slider createSlider(double min, double max, DoubleProperty property) {
        Slider slider = new Slider(min, max, property.get());
        slider.valueProperty().bindBidirectional(property);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        return slider;
    }

    private ColorPicker createColorPicker(ObjectProperty<Color> property) {
        ColorPicker colorPicker = new ColorPicker(property.get());
        colorPicker.valueProperty().bindBidirectional(property);
        return colorPicker;
    }

    private void addToGrid(GridPane grid, Label label, Control control, int row) {
        grid.add(label, 0, row);
        grid.add(control, 1, row);
    }

    private ScrollPane createScrollPane(GridPane grid) {
        ScrollPane scrollPane = new ScrollPane(grid);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }
}