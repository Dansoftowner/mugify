package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.MugLike;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignI;

import java.text.NumberFormat;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MugDetailsView extends ScrollPane {
    private static final String CONTENT_STYLE_CLASS = "mug-details-view-content";

    private final MugLike mug;

    public MugDetailsView(MugLike mug) {
        this.mug = mug;
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);
        grid.setPadding(new Insets(10));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        TextField nameField = new TextField();
        nameField.promptTextProperty().bind(val("mug_name"));
        nameField.textProperty().bindBidirectional(mug.nameProperty());
        nameField.textProperty().addListener((_, oldValue, newValue) -> {
            // Should be a valid file name, if it isn't we decline it
            if (newValue.matches(".*[<>:\"/\\\\|?*].*")) {
                nameField.setText(oldValue);
            }
        });

        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setColumnSpan(nameField, 2);
        grid.add(nameField, 0, 0);

        addPropertyRow(grid, 1, "mug_radius", mug.radiusProperty());
        addPropertyRow(grid, 2, "mug_height", mug.heightProperty());
        addPropertyRow(grid, 3, "mug_border_thickness", mug.borderThicknessProperty());
        addPropertyRow(grid, 4, "mug_handle_radius", mug.handleRadiusProperty());
        addPropertyRow(grid, 5, "mug_handle_width", mug.handleWidthProperty());
        addPropertyRow(grid, 6, "mug_surface_area", mug.surfaceAreaProperty());
        addPropertyRow(grid, 7, "mug_volume", mug.volumeProperty());

        VBox vbox = new VBox();

        Label titleLabel = new Label();
        titleLabel.setGraphic(new FontIcon(MaterialDesignI.INFORMATION));
        titleLabel.textProperty().bind(val("mug_details"));
        titleLabel.getStyleClass().add("title-label");
        titleLabel.prefWidthProperty().bind(vbox.widthProperty());

        vbox.getChildren().addAll(titleLabel, grid);
        VBox.setVgrow(grid, Priority.ALWAYS);
        vbox.prefHeightProperty().bind(this.heightProperty());
        vbox.getStyleClass().add(CONTENT_STYLE_CLASS);

        this.setContent(vbox);
    }

    private void addPropertyRow(GridPane grid, int row, String i18nKey, ObservableValue<? extends Number> property) {
        Label label = new Label();
        label.textProperty().bind(val(i18nKey));
        GridPane.setHgrow(label, Priority.ALWAYS);
        StackPane.setAlignment(label, Pos.CENTER_LEFT);

        Label valueLabel = new Label();
        valueLabel.textProperty().bind(createNumberStringBinding(property));

        grid.add(new StackPane(label), 0, row);
        grid.add(valueLabel, 1, row);
    }

    private StringBinding createNumberStringBinding(ObservableValue<? extends Number> numberProperty) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return Bindings.createStringBinding(
                () -> {
                    Number value = numberProperty.getValue();
                    return value != null ? nf.format(value.doubleValue()) : "";
                },
                numberProperty
        );
    }
}