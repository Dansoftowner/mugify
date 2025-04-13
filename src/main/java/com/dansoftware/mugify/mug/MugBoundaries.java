package com.dansoftware.mugify.mug;

/**
 * Utility class for constants regarding the Mugs' properties
 */
public class MugBoundaries {
    public static double MAX_HEIGHT = 200;
    public static double MIN_HEIGHT = 70;

    public static double MAX_RADIUS = 80;
    public static double MIN_RADIUS = 40;

    public static double MAX_BORDER_THICKNESS = 15;
    public static double MIN_BORDER_THICKNESS = 2;

    public static double MAX_HANDLE_RADIUS = 60;
    public static double MIN_HANDLE_RADIUS = 21;

    public static double MAX_HANDLE_WIDTH = 15;
    public static double MIN_HANDLE_WIDTH = 5;

    /**
     * The required minimum value for {@code height / handle_radius}.
     */
    public static double HEIGHT_HANDLE_RADIUS_RATIO = 200.0 / 60;

    public static double calculateMaxHandleRadius(double height) {
         double handleRadius = MAX_HANDLE_RADIUS;
         double h = 0.1;
         while (height / handleRadius < HEIGHT_HANDLE_RADIUS_RATIO)
             handleRadius -= h;
         return handleRadius;
    }

    public static boolean isHandleRadiusTooLarge(double height, double handleRadius) {
        return MugBoundaries.HEIGHT_HANDLE_RADIUS_RATIO > height / handleRadius;
    }

    /**
     * Not instantiable
     */
    private MugBoundaries() {
    }
}
