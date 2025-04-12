package com.dansoftware.mugify.mug;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class Mug extends Group implements MugLike {

    public static final double DEFAULT_BORDER_THICKNESS = 10;
    public static final double DEFAULT_RADIUS = 50;
    public static final double DEFAULT_HEIGHT = 100;

    public static final Color DEFAULT_OUTER_COLOR = new Color(0.2196, 0.7961, 0.6078, 1.0);
    public static final Color DEFAULT_INNER_COLOR = new Color(0.0706, 0.3216, 0.2275, 1.0);
    public static final Color DEFAULT_BOTTOM_COLOR = Color.GRAY;

    public static final Color DEFAULT_HANDLE_COLOR = new Color(0.3922, 0.2196, 0.2353, 1.0);
    public static final double DEFAULT_HANDLE_RADIUS = 25;
    public static final double DEFAULT_HANDLE_WIDTH = 10;
    public static final boolean DEFAULT_HANDLE_ROUNDED = true;

    public static final String DEFAULT_NAME = "Mug";

    private final DoubleProperty borderThickness;
    private final DoubleProperty radius;
    private final DoubleProperty height;
    private final ObjectProperty<Color> outerColor;
    private final ObjectProperty<Color> innerColor;
    private final ObjectProperty<Color> bottomColor;
    private final DoubleProperty handleRadius;
    private final ObjectProperty<Color> handleColor;
    private final DoubleProperty handleWidth;
    private final DoubleProperty maxHandleRadius;
    private final BooleanProperty handleRounded;
    private final StringProperty name;

    public Mug() {
        borderThickness = new SimpleDoubleProperty(DEFAULT_BORDER_THICKNESS);
        radius = new SimpleDoubleProperty(DEFAULT_RADIUS);
        height = new SimpleDoubleProperty(DEFAULT_HEIGHT);
        outerColor = new SimpleObjectProperty<>(DEFAULT_OUTER_COLOR);
        innerColor = new SimpleObjectProperty<>(DEFAULT_INNER_COLOR);
        bottomColor = new SimpleObjectProperty<>(DEFAULT_BOTTOM_COLOR);
        handleRadius = new SimpleDoubleProperty(DEFAULT_HANDLE_RADIUS);
        handleColor = new SimpleObjectProperty<>(DEFAULT_HANDLE_COLOR);
        handleWidth = new SimpleDoubleProperty(DEFAULT_HANDLE_WIDTH);
        handleRounded = new SimpleBooleanProperty(DEFAULT_HANDLE_ROUNDED);
        maxHandleRadius = new SimpleDoubleProperty(MugBoundaries.MAX_HANDLE_RADIUS);
        name = new SimpleStringProperty(DEFAULT_NAME);
        init();
    }

    private void init() {
        var outerBody = buildOuterBody();
        var innerBody = buildInnerBody();
        var bottom = buildBottom();
        var mugHandle = buildHandle();

        var mugBody = new Group(outerBody, innerBody, bottom);

        this.getChildren().addAll(mugBody, mugHandle);

        this.handleRadius.addListener((_, _, newValue) -> {
            double handleRadius = (double) newValue;
            while (MugBoundaries.HEIGHT_HANDLE_RADIUS_RATIO > this.height.get() / handleRadius) {
                handleRadius -= 0.1;
            }
            this.handleRadius.set(handleRadius);
            this.maxHandleRadius.set(handleRadius);
        });

        this.height.addListener((_, _, newValue) -> {
            double height = (double) newValue;
            double handleRadius = this.handleRadius.get();
            while (MugBoundaries.HEIGHT_HANDLE_RADIUS_RATIO > height / handleRadius) {
                handleRadius -= 0.1;
            }
            this.handleRadius.set(handleRadius);
            this.maxHandleRadius.set(handleRadius);
        });
    }

    private Node buildOuterBody() {
        Cylinder outerBody = new Cylinder();
        outerBody.heightProperty().bind(this.height);
        outerBody.radiusProperty().bind(this.radius);

        PhongMaterial outerMaterial = new PhongMaterial();
        outerMaterial.diffuseColorProperty().bind(this.outerColor);
        outerBody.setMaterial(outerMaterial);
        return outerBody;
    }

    private Node buildInnerBody() {
        Cylinder innerBody = new Cylinder();
        innerBody.radiusProperty().bind(this.radius.subtract(this.borderThickness));
        innerBody.heightProperty().bind(this.height.add(0.5));

        PhongMaterial innerMaterial = new PhongMaterial();
        var innerColor = Bindings.createObjectBinding(() -> {
            if (this.innerColor.get().equals(this.outerColor.get())) {
                Color color = this.innerColor.get();
                return color.deriveColor(10, 1, 1, 1);
            }
            return this.innerColor.get();
        }, this.innerColor, this.outerColor);

        innerMaterial.diffuseColorProperty().bind(innerColor);
        innerBody.setMaterial(innerMaterial);
        return innerBody;
    }

    private Node buildBottom() {
        Cylinder bottom = new Cylinder(0, 0.1);
        bottom.radiusProperty().bind(this.radius);

        bottom.translateYProperty().bind(this.height.divide(100).multiply(51));
        PhongMaterial bottomMaterial = new PhongMaterial();
        bottomMaterial.diffuseColorProperty().bind(this.bottomColor);
        bottom.setMaterial(bottomMaterial);
        return bottom;
    }

    private Node buildHandle() {
        Group handle = new Group();

        var handleMaterial = new PhongMaterial();
        handleMaterial.diffuseColorProperty().bind(this.handleColor);

        int numSegments = 100;
        double arcAngle = 180.0;

        for (int i = 0; i < numSegments; i++) {
            double angle = Math.toRadians(arcAngle / (numSegments - 1) * i - 90);

            Box boxSegment = new Box();
            Cylinder cylinderSegment = new Cylinder();

            boxSegment.visibleProperty().bind(this.handleRounded.not());
            boxSegment.managedProperty().bind(this.handleRounded.not());

            cylinderSegment.visibleProperty().bind(this.handleRounded);
            cylinderSegment.managedProperty().bind(this.handleRounded);

            cylinderSegment.radiusProperty().bind(this.handleWidth.divide(2));
            cylinderSegment.heightProperty().bind(this.handleWidth);

            boxSegment.widthProperty().bind(this.handleWidth);
            boxSegment.heightProperty().bind(this.handleWidth);
            boxSegment.depthProperty().bind(this.handleWidth);

            boxSegment.setMaterial(handleMaterial);
            cylinderSegment.setMaterial(handleMaterial);

            boxSegment.translateXProperty().bind(this.handleRadius.multiply(Math.cos(angle)).add(this.radius));
            cylinderSegment.translateXProperty().bind(this.handleRadius.multiply(Math.cos(angle)).add(this.radius));

            boxSegment.translateYProperty().bind(this.handleRadius.multiply(Math.sin(angle)));
            cylinderSegment.translateYProperty().bind(this.handleRadius.multiply(Math.sin(angle)));

            boxSegment.setRotationAxis(Rotate.Z_AXIS);
            cylinderSegment.setRotationAxis(Rotate.Z_AXIS);

            cylinderSegment.setRotate(Math.toDegrees(angle));
            boxSegment.setRotate(Math.toDegrees(angle));

            handle.getChildren().add(boxSegment);
            handle.getChildren().add(cylinderSegment);
        }
        return handle;
    }

    public double getBorderThickness() {
        return borderThickness.get();
    }

    public DoubleProperty borderThicknessProperty() {
        return borderThickness;
    }

    public double getRadius() {
        return radius.get();
    }

    public DoubleProperty radiusProperty() {
        return radius;
    }

    public double getHeight() {
        return height.get();
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public Color getOuterColor() {
        return outerColor.get();
    }

    public ObjectProperty<Color> outerColorProperty() {
        return outerColor;
    }

    public Color getInnerColor() {
        return innerColor.get();
    }

    public ObjectProperty<Color> innerColorProperty() {
        return innerColor;
    }

    public Color getBottomColor() {
        return bottomColor.get();
    }

    public ObjectProperty<Color> bottomColorProperty() {
        return bottomColor;
    }

    public double getHandleRadius() {
        return handleRadius.get();
    }

    public DoubleProperty handleRadiusProperty() {
        return handleRadius;
    }

    public ObservableValue<? extends Number> maxHandleRadiusProperty() {
        return maxHandleRadius;
    }

    public void setBorderThickness(double borderThickness) {
        this.borderThickness.set(borderThickness);
    }

    public void setRadius(double radius) {
        this.radius.set(radius);
    }

    public void setHeight(double height) {
        this.height.set(height);
    }

    public void setOuterColor(Color outerColor) {
        this.outerColor.set(outerColor);
    }

    public void setInnerColor(Color innerColor) {
        this.innerColor.set(innerColor);
    }

    public void setBottomColor(Color bottomColor) {
        this.bottomColor.set(bottomColor);
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

    public double getHandleWidth() {
        return handleWidth.get();
    }

    public DoubleProperty handleWidthProperty() {
        return handleWidth;
    }

    @Override
    public boolean isHandleRounded() {
        return this.handleRounded.get();
    }

    @Override
    public void setHandleRounded(boolean handleRounded) {
        this.handleRounded.set(handleRounded);
    }

    @Override
    public BooleanProperty handleRoundedProperty() {
        return this.handleRounded;
    }

    public void setHandleWidth(double handleWidth) {
        this.handleWidth.set(handleWidth);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}