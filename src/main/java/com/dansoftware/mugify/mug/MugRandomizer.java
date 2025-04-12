package com.dansoftware.mugify.mug;

import javafx.scene.paint.Color;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MugRandomizer {

    private final Random random;

    public MugRandomizer() {
        this.random = new Random();
    }

    public MugRandomizer(long seed) {
        this.random = new Random(seed);
    }

    public void apply(MugLike mug) {
        mug.setName(randomMugName());
        mug.setHeight(randomDouble(MugBoundaries.MIN_HEIGHT, MugBoundaries.MAX_HEIGHT));
        mug.setRadius(randomDouble(MugBoundaries.MIN_RADIUS, MugBoundaries.MAX_RADIUS));
        mug.setBorderThickness(randomDouble(MugBoundaries.MIN_BORDER_THICKNESS, MugBoundaries.MAX_BORDER_THICKNESS));
        mug.setHandleRadius(randomDouble(MugBoundaries.MIN_HANDLE_RADIUS, MugBoundaries.MAX_HANDLE_RADIUS));
        mug.setHandleWidth(randomDouble(MugBoundaries.MIN_HANDLE_WIDTH, MugBoundaries.MAX_HANDLE_WIDTH));
        mug.setHandleRounded(randomBoolean());
        mug.setBottomColor(randomColor());
        mug.setInnerColor(randomColor());
        mug.setOuterColor(randomColor());
        mug.setHandleColor(randomColor());
    }

    private double randomDouble(double min, double max) {
        return this.random.nextDouble(min, max + 0.001);
    }

    private boolean randomBoolean() {
        var randomInt = random.nextInt(100);
        return randomInt > 10; // 10% chance for 'false'
    }

    private Color randomColor() {
        var red = random.nextDouble(1.0);
        var green = random.nextDouble(1.0);
        var blue = random.nextDouble(1.0);
        return new Color(red, green, blue, 1.0);
    }

    public static String randomMugName() {
        String baseName = val("mug_base_name").get();
        LocalDateTime dt = LocalDateTime.now();
        String dateString = dt.format(DateTimeFormatter.ofPattern("yyMMdd_hhmmss"));
        long randomFactor = System.currentTimeMillis() % 1000;
        return "%s_%s_%d".formatted(baseName, dateString, randomFactor);
    }
}
