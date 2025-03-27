package com.dansoftware.mugify.mug;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.paint.Color;

public interface MugLike {
    double getBorderThickness();
    DoubleProperty borderThicknessProperty();
    double getRadius();
    DoubleProperty radiusProperty();
    double getHeight();
    DoubleProperty heightProperty();
    Color getOuterColor();
    ObjectProperty<Color> outerColorProperty();
    Color getInnerColor();
    ObjectProperty<Color> innerColorProperty();
    Color getBottomColor();
    ObjectProperty<Color> bottomColorProperty();
    double getHandleRadius();
    DoubleProperty handleRadiusProperty();
    void setBorderThickness(double borderThickness);
    void setRadius(double radius);
    void setHeight(double height);
    void setOuterColor(Color outerColor);
    void setInnerColor(Color innerColor);
    void setBottomColor(Color bottomColor);
    void setHandleRadius(double handleRadius);
    Color getHandleColor();
    ObjectProperty<Color> handleColorProperty();
    void setHandleColor(Color handleColor);
    double getHandleWidth();
    DoubleProperty handleWidthProperty();
    void setHandleWidth(double handleWidth);
}
