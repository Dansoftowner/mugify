package com.dansoftware.mugify.mug;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableValue;

public class MugSurfaceVolumeCalculator {
    private MugSurfaceVolumeCalculator() {
    }

    public static ObservableValue<? extends Number> getVolumeProperty(MugLike mug) {
        return bodyVolume(mug).add(handleVolume(mug));
    }

    public static ObservableValue<? extends Number> getSurfaceAreaProperty(MugLike mug) {
        return bodySurfaceArea(mug).add(handleSurfaceArea(mug)).subtract(handleBaseArea(mug).multiply(2));
    }

    private static DoubleBinding bodyVolume(MugLike mug) {
        /*
         Calculation:
                Mug body:
                 outer_cylinder_volume = radius^2 * PI * height
                 inner_cylinder_volume =  (radius - border_thickness * 2)^2 * PI * height

                 outer_cylinder_volume - inner_cylinder_volume
         */

        DoubleProperty radius = mug.radiusProperty();
        DoubleProperty borderThickness = mug.borderThicknessProperty();
        DoubleProperty height = mug.heightProperty();

        var radiusPow = radius.multiply(radius); // radius^2
        var outerBodyVolume = radiusPow.multiply(Math.PI).multiply(height); // radius^2*PI*height
        var innerRadius = radius.subtract(borderThickness.multiply(2));
        var innerRadiusPow = innerRadius.multiply(innerRadius);
        var innerBodyVolume = innerRadiusPow.multiply(Math.PI).multiply(height);

        return outerBodyVolume.subtract(innerBodyVolume);
    }

    private static DoubleBinding handleVolume(MugLike mug) {
        /*
         Calculation:
               perimeter = handleRadius * PI
               base = { if handle is rounded: (handleWidth/2)^2 * PI, else: handleWidth^2 }
               handle_volume = base * perimeter
         */

        var baseArea = handleBaseArea(mug);
        var arcLength = mug.handleRadiusProperty().multiply(Math.PI);

        return baseArea.multiply(arcLength);
    }

    private static DoubleBinding bodySurfaceArea(MugLike mug) {
        DoubleProperty radius = mug.radiusProperty();
        DoubleProperty borderThickness = mug.borderThicknessProperty();
        DoubleProperty height = mug.heightProperty();

        var innerRadius = radius.subtract(borderThickness.multiply(2));

        var bottomSurface = radius.multiply(radius).multiply(Math.PI);
        var innerBottomSurface = innerRadius.multiply(innerRadius).multiply(Math.PI);

        var outerBodySurface = radius.multiply(2 * Math.PI).multiply(height);
        var innerBodySurface = innerRadius.multiply(2 * Math.PI).multiply(height);

        var borderSurface = bottomSurface.subtract(innerBottomSurface);

        return outerBodySurface.add(innerBodySurface).add(bottomSurface).add(innerBottomSurface).add(borderSurface);
    }

    private static DoubleBinding handleSurfaceArea(MugLike mug) {
        var arcLength = mug.handleRadiusProperty().multiply(Math.PI);
        var basePerimeter = Bindings.createDoubleBinding(() -> {
            if (mug.isHandleRounded()) {
                double r = mug.getHandleWidth() / 2;
                return 2 * r * Math.PI;
            }
            return 4 * mug.getHandleWidth();
        }, mug.handleRoundedProperty(), mug.handleWidthProperty());

        return basePerimeter.multiply(arcLength);
    }

    private static DoubleBinding handleBaseArea(MugLike mug) {
        return Bindings.createDoubleBinding(() -> {
            if (mug.isHandleRounded()) {
                double r = mug.getHandleWidth() / 2;
                return Math.pow(r, 2) * Math.PI;
            }
            return Math.pow(mug.getHandleWidth(), 2);
        }, mug.handleRoundedProperty(), mug.handleWidthProperty());
    }
}
