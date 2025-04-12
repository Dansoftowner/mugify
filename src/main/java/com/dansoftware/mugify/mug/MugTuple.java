package com.dansoftware.mugify.mug;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

import java.util.Collection;
import java.util.List;

public class MugTuple implements MugLike {

    private final List<MugLike> mugs;

    private final DoubleProperty borderThickness;
    private final DoubleProperty radius;
    private final DoubleProperty height;
    private final ObjectProperty<Color> outerColor;
    private final ObjectProperty<Color> innerColor;
    private final ObjectProperty<Color> bottomColor;
    private final DoubleProperty handleRadius;
    private final ObjectProperty<Color> handleColor;
    private final DoubleProperty handleWidth;
    private final BooleanProperty handleRounded;
    private final StringProperty name;

    public MugTuple(Collection<? extends MugLike> mugs) {
        this.borderThickness = new SimpleDoubleProperty(Mug.DEFAULT_BORDER_THICKNESS);
        this.radius = new SimpleDoubleProperty(Mug.DEFAULT_RADIUS);
        this.height = new SimpleDoubleProperty(Mug.DEFAULT_HEIGHT);
        this.outerColor = new SimpleObjectProperty<>(Mug.DEFAULT_OUTER_COLOR);
        this.innerColor = new SimpleObjectProperty<>(Mug.DEFAULT_INNER_COLOR);
        this.bottomColor = new SimpleObjectProperty<>(Mug.DEFAULT_BOTTOM_COLOR);
        this.handleRadius = new SimpleDoubleProperty(Mug.DEFAULT_HANDLE_RADIUS);
        this.handleColor = new SimpleObjectProperty<>(Mug.DEFAULT_HANDLE_COLOR);
        this.handleWidth = new SimpleDoubleProperty(Mug.DEFAULT_HANDLE_WIDTH);
        this.handleRounded = new SimpleBooleanProperty(Mug.DEFAULT_HANDLE_ROUNDED);
        this.name = new SimpleStringProperty(Mug.DEFAULT_NAME);
        this.mugs = List.copyOf(mugs);
        this.init(this.mugs);
    }

    private void init(List<MugLike> mugs) {
        for (MugLike mug : mugs) {
            mug.borderThicknessProperty().bindBidirectional(this.borderThickness);
            mug.radiusProperty().bindBidirectional(this.radius);
            mug.heightProperty().bindBidirectional(this.height);
            mug.outerColorProperty().bindBidirectional(this.outerColor);
            mug.innerColorProperty().bindBidirectional(this.innerColor);
            mug.bottomColorProperty().bindBidirectional(this.bottomColor);
            mug.handleRadiusProperty().bindBidirectional(this.handleRadius);
            mug.handleColorProperty().bindBidirectional(this.handleColor);
            mug.handleWidthProperty().bindBidirectional(this.handleWidth);
            mug.handleRoundedProperty().bindBidirectional(this.handleRounded);
            mug.nameProperty().bindBidirectional(this.name);
        }
    }

    public double getBorderThickness() {
        return borderThickness.get();
    }

    public DoubleProperty borderThicknessProperty() {
        return borderThickness;
    }

    public void setBorderThickness(double borderThickness) {
        this.borderThickness.set(borderThickness);
    }

    public double getRadius() {
        return radius.get();
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public Color getOuterColor() {
        return outerColor.get();
    }

    public ObjectProperty<Color> outerColorProperty() {
        return outerColor;
    }

    public void setOuterColor(Color outerColor) {
        this.outerColor.set(outerColor);
    }

    public Color getInnerColor() {
        return innerColor.get();
    }

    public ObjectProperty<Color> innerColorProperty() {
        return innerColor;
    }

    public void setInnerColor(Color innerColor) {
        this.innerColor.set(innerColor);
    }

    public Color getBottomColor() {
        return bottomColor.get();
    }

    public ObjectProperty<Color> bottomColorProperty() {
        return bottomColor;
    }

    public void setBottomColor(Color bottomColor) {
        this.bottomColor.set(bottomColor);
    }

    public double getHandleRadius() {
        return handleRadius.get();
    }

    public DoubleProperty handleRadiusProperty() {
        return handleRadius;
    }

    public ObservableValue<? extends Number> maxHandleRadiusProperty() {
        return this.mugs.getFirst().maxHandleRadiusProperty();
    }

    public void setHandleRadius(double handleRadius) {
        this.handleRadius.set(handleRadius);
    }

    public Color getHandleColor() {
        return handleColor.get();
    }

    public ObjectProperty<Color> handleColorProperty() {
        return handleColor;
    }

    public void setHandleColor(Color handleColor) {
        this.handleColor.set(handleColor);
    }

    public double getHandleWidth() {
        return handleWidth.get();
    }

    public DoubleProperty handleWidthProperty() {
        return handleWidth;
    }

    public void setHandleWidth(double handleWidth) {
        this.handleWidth.set(handleWidth);
    }

    public boolean isHandleRounded() {
        return handleRounded.get();
    }

    @Override
    public BooleanProperty handleRoundedProperty() {
        return handleRounded;
    }

    public void setHandleRounded(boolean handleRounded) {
        this.handleRounded.set(handleRounded);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public List<MugLike> getMugs() {
        return mugs;
    }
}