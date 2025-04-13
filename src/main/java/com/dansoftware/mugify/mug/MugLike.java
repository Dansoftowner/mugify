package com.dansoftware.mugify.mug;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

public interface MugLike {
    double getBorderThickness();
    void setBorderThickness(double borderThickness);
    DoubleProperty borderThicknessProperty();

    double getRadius();
    void setRadius(double radius);
    DoubleProperty radiusProperty();

    double getHeight();
    void setHeight(double height);
    DoubleProperty heightProperty();

    Color getOuterColor();
    void setOuterColor(Color outerColor);
    ObjectProperty<Color> outerColorProperty();

    Color getInnerColor();
    void setInnerColor(Color innerColor);
    ObjectProperty<Color> innerColorProperty();

    Color getBottomColor();
    void setBottomColor(Color bottomColor);
    ObjectProperty<Color> bottomColorProperty();

    double getHandleRadius();
    void setHandleRadius(double handleRadius);
    DoubleProperty handleRadiusProperty();
    ReadOnlyDoubleProperty maxHandleRadiusProperty();


    Color getHandleColor();
    void setHandleColor(Color handleColor);
    ObjectProperty<Color> handleColorProperty();

    double getHandleWidth();
    void setHandleWidth(double handleWidth);
    DoubleProperty handleWidthProperty();

    boolean isHandleRounded();
    void setHandleRounded(boolean handleRounded);
    BooleanProperty handleRoundedProperty();

    String getName();
    StringProperty nameProperty();
    void setName(String name);

    default ObservableValue<? extends Number> surfaceAreaProperty() {
        return MugSurfaceVolumeCalculator.getSurfaceAreaProperty(this);
    }

    default ObservableValue<? extends Number> volumeProperty() {
        return MugSurfaceVolumeCalculator.getVolumeProperty(this);
    }
}