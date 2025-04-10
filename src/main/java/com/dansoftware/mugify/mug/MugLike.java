package com.dansoftware.mugify.mug;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
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
    ObservableValue<? extends Number> maxHandleRadiusProperty();


    Color getHandleColor();
    void setHandleColor(Color handleColor);
    ObjectProperty<Color> handleColorProperty();

    double getHandleWidth();
    void setHandleWidth(double handleWidth);
    DoubleProperty handleWidthProperty();

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