package com.dansoftware.mugify.io;

import com.dansoftware.mugify.mug.MugLike;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.paint.Color;

import java.io.*;

public class MugIO {

    public static void saveToJson(String filePath, MugLike mugLike) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorSerializer())
                .setPrettyPrinting()
                .create();

        MugLikeData data = new MugLikeData(
                mugLike.getBorderThickness(),
                mugLike.getRadius(),
                mugLike.getHeight(),
                mugLike.getOuterColor(),
                mugLike.getInnerColor(),
                mugLike.getBottomColor(),
                mugLike.getHandleRadius(),
                mugLike.isHandleRounded(),
                mugLike.getHandleColor(),
                mugLike.getHandleWidth(),
                mugLike.getName()
        );

        try (var writer = new BufferedWriter(new FileWriter(filePath))) {
            gson.toJson(data, writer);
        }
    }

    public static void loadFromJson(String filePath, MugLike mugLike) throws IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, new ColorDeserializer())
                .create();

        try (var reader = new BufferedReader(new FileReader(filePath))) {
            MugLikeData data = gson.fromJson(reader, MugLikeData.class);
            mugLike.setBorderThickness(data.borderThickness);
            mugLike.setRadius(data.radius);
            mugLike.setHeight(data.height);
            mugLike.setOuterColor(data.outerColor);
            mugLike.setInnerColor(data.innerColor);
            mugLike.setBottomColor(data.bottomColor);
            mugLike.setHandleRadius(data.handleRadius);
            mugLike.setHandleColor(data.handleColor);
            mugLike.setHandleWidth(data.handleWidth);
            mugLike.setHandleRounded(data.handleRounded);
            mugLike.setName(data.name);
        }
    }

    private static class MugLikeData {
        double borderThickness;
        double radius;
        double height;
        Color outerColor;
        Color innerColor;
        Color bottomColor;
        double handleRadius;
        Color handleColor;
        double handleWidth;
        boolean handleRounded;
        String name;

        public MugLikeData(double borderThickness,
                           double radius,
                           double height,
                           Color outerColor,
                           Color innerColor,
                           Color bottomColor,
                           double handleRadius,
                           boolean handleRounded,
                           Color handleColor,
                           double handleWidth,
                           String name) {
            this.borderThickness = borderThickness;
            this.radius = radius;
            this.height = height;
            this.outerColor = outerColor;
            this.innerColor = innerColor;
            this.bottomColor = bottomColor;
            this.handleRadius = handleRadius;
            this.handleColor = handleColor;
            this.handleWidth = handleWidth;
            this.handleRounded = handleRounded;
            this.name = name;
        }
    }

    private static class ColorSerializer implements com.google.gson.JsonSerializer<Color> {
        @Override
        public com.google.gson.JsonElement serialize(Color color, java.lang.reflect.Type type,
                                                     com.google.gson.JsonSerializationContext context) {
            return new com.google.gson.JsonPrimitive(color.toString());
        }
    }

    private static class ColorDeserializer implements com.google.gson.JsonDeserializer<Color> {
        @Override
        public Color deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type type,
                                 com.google.gson.JsonDeserializationContext context) throws com.google.gson.JsonParseException {
            return Color.valueOf(json.getAsString());
        }
    }
}