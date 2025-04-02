package com.dansoftware.mugify.mug;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
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
        this.mugs = List.copyOf(mugs);
        this.init(this.mugs);
    }

    private void init(List<MugLike> mugs) {
        for (MugLike mug : mugs) {
            mug.borderThicknessProperty().bind(this.borderThickness);
            mug.radiusProperty().bind(this.radius);
            mug.heightProperty().bind(this.height);
            mug.outerColorProperty().bind(this.outerColor);
            mug.innerColorProperty().bind(this.innerColor);
            mug.bottomColorProperty().bind(this.bottomColor);
            mug.handleRadiusProperty().bind(this.handleRadius);
            mug.handleColorProperty().bind(this.handleColor);
            mug.handleWidthProperty().bind(this.handleWidth);
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

    @Override
    public double getHandleWidth() {
        return handleWidth.get();
    }

    @Override
    public DoubleProperty handleWidthProperty() {
        return handleWidth;
    }

    public void setHandleWidth(double handleWidth) {
        this.handleWidth.set(handleWidth);
    }

    public List<MugLike> getMugs() {
        return mugs;
    }
}
