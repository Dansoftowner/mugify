package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.Mug;
import com.dansoftware.mugify.mug.MugTuple;
import javafx.scene.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.List;

public class MugGrid extends GridPane {

    private final MugTuple mugTuple;
    private final List<Mug> mugs = List.of(
            new Mug(),
            new Mug(),
            new Mug(),
            new Mug()
    );

    public MugGrid() {
        this.mugTuple = new MugTuple(mugs);
        this.init();
    }

    private void init() {
        generateScenes();
        setMugPositions();
    }

    private void generateScenes() {
        // the four position of the mugs in the grid
        var positions = new int[][] {
                {0, 0},
                {0, 1},
                {1, 0},
                {1, 1}
        };

        for (int i = 0; i < mugs.size(); i++) {
            Mug mug = mugs.get(i);
            boolean rotatable = (i == mugs.size() - 1);  // only the last scene will be rotatable
            SubScene scene = createMugSubScene(mug, rotatable);
            GridPane.setConstraints(scene, positions[i][0], positions[i][1]);
            getChildren().add(scene);
        }
    }

    private void setMugPositions() {
        // mugs[0] is side-view by default
        setMugPosition(mugs.get(1), 272.5, 0.5); // bottom-view
        setMugPosition(mugs.get(2), 90.5, 1.5); // top-view
        // mugs[3] is in a rotatable scene
    }

    private SubScene createMugSubScene(Mug mug, boolean rotatable) {

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(-100);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-100);
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        mug.getChildren().addAll(/*pointLight,*/ ambientLight);

        // Kamera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-500);
        camera.setNearClip(0.1);
        camera.setFarClip(1000.0);


        // SubScene létrehozása
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

        subScene.widthProperty().bind(this.widthProperty().divide(2));
        subScene.heightProperty().bind(this.heightProperty().divide(2));
        return subScene;
    }

    private void setMugPosition(Mug mug, double xAngle, double yAngle) {
        Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        mug.getTransforms().addAll(rotateX, rotateY);
        rotateX.setAngle(xAngle);
        rotateY.setAngle(yAngle);
    }

    public MugTuple getMugTuple() {
        return mugTuple;
    }
}
