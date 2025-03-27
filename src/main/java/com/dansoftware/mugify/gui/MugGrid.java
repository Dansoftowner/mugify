package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.Mug;
import com.dansoftware.mugify.mug.MugTuple;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MugGrid extends GridPane {

    public enum Viewport {
        SIDE_SCENE,
        TOP_SCENE,
        BOTTOM_SCENE,
        SCENE_3D,
        ALL_SCENES,
    }

    private static final Viewport DEFAULT_VIEWPORT = Viewport.ALL_SCENES;

    private final MugTuple mugTuple;

    private final List<Mug> mugs = List.of(
            new Mug(),
            new Mug(),
            new Mug(),
            new Mug()
    );

    private final List<SubScene> subScenes;

    private final ObjectProperty<Viewport> viewport;

    public MugGrid() {
        this.mugTuple = new MugTuple(mugs);
        this.viewport = new SimpleObjectProperty<>();
        this.subScenes = new LinkedList<>();
        this.init();
    }

    private void init() {
        initGridConstraints();
        initViewportBehaviours();
        generateScenes();
        rotateMugs();
        this.viewport.set(DEFAULT_VIEWPORT);
    }

    private void initGridConstraints() {
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getRowConstraints().add(new RowConstraints());
        this.getRowConstraints().add(new RowConstraints());
    }

    private void initViewportBehaviours() {
        this.viewport.addListener((obs, oldValue, newValue) -> {
            subScenes.forEach(scene -> {
                scene.widthProperty().unbind();
                scene.heightProperty().unbind();
            });

            if (newValue == Viewport.ALL_SCENES) {
                showAllScenes();
                return;
            }

            var dict = Map.of(
                    Viewport.SIDE_SCENE, 0,
                    Viewport.TOP_SCENE, 1,
                    Viewport.BOTTOM_SCENE, 2,
                    Viewport.SCENE_3D, 3
            );
            showSubScene(dict.get(newValue));
        });
    }

    private void generateScenes() {
        for (int i = 0; i < mugs.size(); i++) {
            Mug mug = mugs.get(i);
            boolean rotatable = (i == mugs.size() - 1);  // only the last scene will be rotatable
            SubScene scene = createMugSubScene(mug, rotatable);
            getChildren().add(scene);
            this.subScenes.add(scene);
        }
    }

    private void rotateMugs() {
        // mugs[0] is side-view by default
        setMugRotation(mugs.get(2), 272.5, 0.5); // bottom-view
        setMugRotation(mugs.get(1), 90.5, 1.5); // top-view
        // mugs[3] is in a rotatable scene
    }

    private SubScene createMugSubScene(Mug mug, boolean rotatable) {

        PointLight pointLight = new PointLight(Color.GRAY);
        pointLight.setTranslateX(-100);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-100);

        if (rotatable)
            mug.getChildren().add(pointLight);

        mug.getChildren().add(new AmbientLight(Color.WHITE));

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);

        SubScene subScene = new SubScene(mug, 0, 0, true, SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);

        if (rotatable) {
            Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
            Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
            mug.getTransforms().addAll(rotateX, rotateY);

            double[] startPos = new double[2];
            subScene.setOnMousePressed(event -> {
                startPos[0] = event.getSceneX();
                startPos[1] = event.getSceneY();
            });

            subScene.setOnMouseDragged(event -> {
                double deltaX = event.getSceneX() - startPos[0];
                double deltaY = event.getSceneY() - startPos[1];
                rotateY.setAngle(rotateY.getAngle() + deltaX / 2);
                rotateX.setAngle(rotateX.getAngle() - deltaY / 2);

                //System.out.println("y: " + (rotateY.getAngle() + deltaX / 2));
                //System.out.println("x: " + (rotateX.getAngle() - deltaY / 2));

                startPos[0] = event.getSceneX();
                startPos[1] = event.getSceneY();
            });
        }

        return subScene;
    }

    private void setMugRotation(Mug mug, double xAngle, double yAngle) {
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        mug.getTransforms().addAll(rotateX, rotateY);
        rotateX.setAngle(xAngle);
        rotateY.setAngle(yAngle);
    }

    private void showAllScenes() {
        // the four position of the mugs in the grid
        var positions = new int[][] {
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}
        };

        for (int i = 0; i < subScenes.size(); i++) {
            SubScene scene = subScenes.get(i);
            scene.widthProperty().bind(this.widthProperty().divide(2));
            scene.heightProperty().bind(this.heightProperty().divide(2));
            GridPane.setConstraints(scene, positions[i][0], positions[i][1]);
        }
    }

    private void showSubScene(int subSceneIndex) {
        for (int i = 0; i < subScenes.size(); i++) {
            subScenes.get(i).setVisible(i == subSceneIndex);
        }

        var subScene = subScenes.get(subSceneIndex);

        GridPane.setColumnSpan(subScene, 2);
        GridPane.setRowSpan(subScene, 2);
        subScene.widthProperty().bind(this.widthProperty());
        subScene.heightProperty().bind(this.heightProperty());
    }

    public Viewport getViewport() {
        return viewport.get();
    }

    public ObjectProperty<Viewport> viewportProperty() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport.set(viewport);
    }

    public MugTuple getMugTuple() {
        return mugTuple;
    }
}
