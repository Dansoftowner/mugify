package com.dansoftware.mugify.mug;

import javafx.scene.paint.Color;

import java.util.Random;

public class MugRandomizer {

    private final Random random;

    public MugRandomizer() {
        this.random = new Random();
    }
    public MugRandomizer(long seed) {
        this.random = new Random(seed);
    }

    public void apply(MugLike mug) {
        mug.setHeight(randomDouble(MugBoundaries.MIN_HEIGHT, MugBoundaries.MAX_HEIGHT));
        mug.setRadius(randomDouble(MugBoundaries.MIN_RADIUS, MugBoundaries.MAX_RADIUS));
        mug.setBorderThickness(randomDouble(MugBoundaries.MIN_BORDER_THICKNESS, MugBoundaries.MAX_BORDER_THICKNESS));
        mug.setHandleRadius(randomDouble(MugBoundaries.MIN_HANDLE_RADIUS, MugBoundaries.MAX_HANDLE_RADIUS));
        mug.setHandleWidth(randomDouble(MugBoundaries.MIN_HANDLE_WIDTH, MugBoundaries.MAX_HANDLE_WIDTH));
        mug.setBottomColor(randomColor());
        mug.setInnerColor(randomColor());
        mug.setOuterColor(randomColor());
        mug.setHandleColor(randomColor());
    }

    private double randomDouble(double min, double max) {
        return this.random.nextDouble(min, max + 0.001);
    }

    private Color randomColor() {
        var red = random.nextDouble(1.0);
        var green = random.nextDouble(1.0);
        var blue = random.nextDouble(1.0);
        return new Color(red, green, blue, 1.0);
    }
}
