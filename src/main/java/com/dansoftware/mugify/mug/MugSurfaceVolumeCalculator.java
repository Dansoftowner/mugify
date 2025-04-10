package com.dansoftware.mugify.mug;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;

public class MugSurfaceVolumeCalculator {
    private MugSurfaceVolumeCalculator() {
    }

    public static ObservableValue<? extends Number> getVolumeProperty(MugLike mug) {
        /*
           Calculation:
              1) Mug body:
                 outer_cylinder_volume = radius^2 * PI * height
                 inner_cylinder_volume =  (radius - border_thickness * 2)^2 * PI * height

                 outer_cylinder_volume - inner_cylinder_volume
              2) Handle:
                 outer_cylinder_volume / 2 = handle_radius^2 * PI * height / 2
                 inner_cylinder_volume / 2 = (handle_radius - handle_width * 2)^2 * PI * height / 2

                 outer_cylinder_volume - inner_cylinder_volume
         */

        DoubleProperty radius = mug.radiusProperty();
        DoubleProperty borderThickness = mug.borderThicknessProperty();
        DoubleProperty height = mug.heightProperty();
        DoubleProperty handleRadius = mug.handleRadiusProperty();
        DoubleProperty handleWidth = mug.handleWidthProperty();

        var radiusPow = radius.multiply(radius); // radius^2
        var outerBodyVolume = radiusPow.multiply(Math.PI).multiply(height); // radius^2*PI*height
        var innerRadius = radius.subtract(borderThickness.multiply(2));
        var innerRadiusPow = innerRadius.multiply(innerRadius);
        var innerBodyVolume = innerRadiusPow.multiply(Math.PI).multiply(height);

        var mugBodyVolume = outerBodyVolume.subtract(innerBodyVolume);

        var handleRadiusPow = handleRadius.multiply(handleRadius);
        var outerHandleVolume = handleRadiusPow.multiply(Math.PI).multiply(handleWidth).divide(2);
        var innerHandleRadius = handleRadius.subtract(handleWidth.multiply(2));
        var innerHandleRadiusPow = innerHandleRadius.multiply(innerHandleRadius);
        var innerHandleVolume = innerHandleRadiusPow.multiply(Math.PI).multiply(handleWidth).divide(2);

        var handleVolume = outerHandleVolume.subtract(innerHandleVolume);

        return mugBodyVolume.add(handleVolume);
    }

    public static ObservableValue<? extends Number> getSurfaceAreaProperty(MugLike mug) {

        DoubleProperty radius = mug.radiusProperty();
        DoubleProperty borderThickness = mug.borderThicknessProperty();
        DoubleProperty height = mug.heightProperty();
        DoubleProperty handleRadius = mug.handleRadiusProperty();
        DoubleProperty handleWidth = mug.handleWidthProperty();

        var innerRadius = radius.subtract(borderThickness.multiply(2));

        var bottomSurface = radius.multiply(radius).multiply(Math.PI);
        var innerBottomSurface = innerRadius.multiply(innerRadius).multiply(Math.PI);

        var outerBodySurface = radius.multiply(2 * Math.PI).multiply(height);
        var innerBodySurface = innerRadius.multiply(2 * Math.PI).multiply(height);

        // TODO: handle surface

        return outerBodySurface.add(innerBodySurface);
    }
}
