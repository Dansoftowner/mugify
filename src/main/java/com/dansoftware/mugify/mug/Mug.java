package com.dansoftware.mugify.mug;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

/**
 * The mug body
 * @author Daniel Gyoerffy
 */
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

    private final DoubleProperty borderThickness;
    private final DoubleProperty radius;
    private final DoubleProperty height;
    private final ObjectProperty<Color> outerColor;
    private final ObjectProperty<Color> innerColor;
    private final ObjectProperty<Color> bottomColor;
    private final DoubleProperty handleRadius;
    private final ObjectProperty<Color> handleColor;
    private final DoubleProperty handleWidth;

    private final DoubleProperty realHandleRadius;

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
        realHandleRadius = new SimpleDoubleProperty(DEFAULT_HANDLE_RADIUS);
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
                handleRadius -= 1;
            }

            this.realHandleRadius.set(handleRadius);
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

        // if the outer and inner colors are the same we make sure that there is still a color-difference
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

        int numSegments = 1000;
        double arcAngle = 180.0;

        for (int i = 0; i < numSegments; i++) {
            double angle = Math.toRadians(arcAngle / (numSegments - 1) * i - 90);

            Box segment = new Box();

            segment.depthProperty().bind(this.handleWidth);
            segment.heightProperty().bind(this.handleWidth);
            segment.widthProperty().bind(this.handleWidth);

            segment.setMaterial(handleMaterial);

            segment.translateXProperty().bind(this.realHandleRadius.multiply(Math.cos(angle)).add(this.radius));
            segment.translateYProperty().bind(this.realHandleRadius.multiply(Math.sin(angle)));

            segment.setRotationAxis(Rotate.Z_AXIS);
            segment.setRotate(Math.toDegrees(angle) + 90);
            handle.getChildren().add(segment);
        }

        /*int numSegments = 1000;
        double arcAngle = 180.0;
        double radius = 25.0;
        double handleWidth = 10.0;

        for (int i = 0; i < numSegments; i++) {
            double angle = Math.toRadians(arcAngle / (numSegments - 1) * i - 90);
            double x = 50 + radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            Box segment = new Box(handleWidth, handleWidth, handleWidth);
            segment.setMaterial(handleMaterial);
            segment.setTranslateX(x);
            segment.setTranslateY(y);
            segment.setRotationAxis(Rotate.Z_AXIS);
            segment.setRotate(Math.toDegrees(angle) + 90);
            handle.getChildren().add(segment);
        }*/
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

    public void setHandleWidth(double handleWidth) {
        this.handleWidth.set(handleWidth);
    }
}
