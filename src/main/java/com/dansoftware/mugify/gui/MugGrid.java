package com.dansoftware.mugify.gui;

import com.dansoftware.mugify.mug.Mug;
import com.dansoftware.mugify.mug.MugTuple;
import com.pixelduke.transit.TransitStyleClass;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignV;

import java.util.HashMap;
import java.util.Map;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MugGrid extends GridPane {

    public enum Viewport {
        SIDE_SCENE("viewport_side", MaterialDesignC.COFFEE),
        TOP_SCENE("viewport_top", MaterialDesignC.CIRCLE_OUTLINE),
        BOTTOM_SCENE("viewport_bottom", MaterialDesignC.CIRCLE),
        SCENE_3D("viewport_3d", MaterialDesignC.CUBE),
        ALL_SCENES("viewport_all", MaterialDesignV.VIEW_GRID);

        private final String id;
        private final Ikon icon;

        Viewport(String id, Ikon icon) {
            this.id = id;
            this.icon = icon;
        }

        public String getId() {
            return this.id;
        }

        public Ikon getIcon() {
            return this.icon;
        }
    }

    private static final String STYLE_CLASS = "mug-grid";
    private static final Viewport DEFAULT_VIEWPORT = Viewport.ALL_SCENES;

    private final MugTuple mugTuple;

    private final Map<Viewport, Mug> mugs = Map.of(
            Viewport.SIDE_SCENE, new Mug(),
            Viewport.TOP_SCENE, new Mug(),
            Viewport.BOTTOM_SCENE, new Mug(),
            Viewport.SCENE_3D, new Mug()
    );

    private final Map<Viewport, SubScene> subScenes;

    private final ObjectProperty<Viewport> viewport;

    public MugGrid() {
        this.mugTuple = new MugTuple(mugs.values());
        this.viewport = new SimpleObjectProperty<>();
        this.subScenes = new HashMap<>();
        this.init();
        this.getStyleClass().add(TransitStyleClass.BACKGROUND);
        this.getStyleClass().add(STYLE_CLASS);
    }

    private void init() {
        initGridConstraints();
        initViewportBehaviours();
        generateScenes();
        fitMugs();
        this.viewport.set(DEFAULT_VIEWPORT);
    }

    private void initGridConstraints() {
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.getRowConstraints().add(new RowConstraints());
        this.getRowConstraints().add(new RowConstraints());
    }

    private void initViewportBehaviours() {
        this.viewport.addListener((_, _, newValue) -> {
            subScenes.forEach((_, scene) -> {
                var vBox = (VBox) scene.getParent();
                vBox.prefWidthProperty().unbind();
                vBox.prefHeightProperty().unbind();
            });

            if (newValue == Viewport.ALL_SCENES) {
                showAllScenes();
                return;
            }

            showSubScene(newValue);
        });
    }

    private void generateScenes() {
        for (Viewport vp : mugs.keySet()) {
            Mug mug = mugs.get(vp);
            boolean rotatable = (vp == Viewport.SCENE_3D);  // only the last scene will be rotatable
            SubScene scene = createMugSubScene(mug, rotatable);

            var topHeader = new BorderPane();
            topHeader.getStyleClass().add("viewport-top-header");

            var label = new Label();
            label.setGraphic(new FontIcon(vp.icon));
            label.textProperty().bind(val(vp.id));
            label.getStyleClass().add("viewport-label");

            var fullScreenButton = generateFullScreenBtn(vp);

            topHeader.setLeft(label);
            topHeader.setRight(fullScreenButton);

            VBox vBox = new VBox(topHeader, scene);
            vBox.getStyleClass().add("viewport-box");
            vBox.setMinSize(0, 0);

            scene.widthProperty().bind(vBox.widthProperty());
            scene.heightProperty().bind(vBox.heightProperty().subtract(label.heightProperty()));

            VBox.setVgrow(scene, Priority.ALWAYS);
            VBox.setMargin(topHeader, new Insets(5, 5, 0, 5));

            getChildren().add(vBox);
            this.subScenes.put(vp, scene);
        }
    }

    private Label generateFullScreenBtn(Viewport vp) {
        var fullScreenButton = new Label();
        fullScreenButton.getStyleClass().add("viewport-label");
        fullScreenButton.setCursor(Cursor.HAND);
        fullScreenButton.setGraphic(new FontIcon(MaterialDesignF.FULLSCREEN));
        fullScreenButton.setOnMouseClicked(_ -> {
            if (viewport.get() == Viewport.ALL_SCENES) {
                viewport.set(vp);
                fullScreenButton.setGraphic(new FontIcon(MaterialDesignF.FULLSCREEN_EXIT));
            } else {
                viewport.set(Viewport.ALL_SCENES);
                fullScreenButton.setGraphic(new FontIcon(MaterialDesignF.FULLSCREEN));
            }
        });
        return fullScreenButton;
    }

    private void fitMugs() {
        // mugs[0] is side-view by default
        setMugRotation(mugs.get(Viewport.BOTTOM_SCENE), 272.5, 0.5); // bottom-view
        setMugRotation(mugs.get(Viewport.TOP_SCENE), 90.5, 1.5); // top-view
        setMugRotation(mugs.get(Viewport.SCENE_3D), 37.5, 20.5); // 3D View default position

        // we don't want the height to have effect on the appearance of the top and bottom scene
        mugs.get(Viewport.TOP_SCENE).heightProperty().unbindBidirectional(this.mugTuple.heightProperty());
        mugs.get(Viewport.BOTTOM_SCENE).heightProperty().unbindBidirectional(this.mugTuple.heightProperty());
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
                subScene.setCursor(Cursor.CLOSED_HAND);
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

            subScene.setOnMouseReleased(_ -> subScene.setCursor(Cursor.OPEN_HAND));
            subScene.setCursor(Cursor.OPEN_HAND);
        }

        subScene.setFill(Color.TRANSPARENT);

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
        var positions = Map.of(
                Viewport.SIDE_SCENE, new int[] {0, 0},
                Viewport.TOP_SCENE, new int[] {0, 1},
                Viewport.BOTTOM_SCENE, new int[] {1, 0},
                Viewport.SCENE_3D, new int[] {1, 1}
        );

        for (Viewport vp : subScenes.keySet()) {
            SubScene scene = subScenes.get(vp);
            var vBox = (VBox) scene.getParent();
            vBox.setVisible(true);
            vBox.setManaged(true);
            vBox.prefWidthProperty().bind(this.widthProperty().divide(2));
            vBox.prefHeightProperty().bind(this.heightProperty().divide(2));
            GridPane.setRowSpan(vBox, 1);
            GridPane.setColumnSpan(vBox, 1);
            GridPane.setConstraints(vBox, positions.get(vp)[0], positions.get(vp)[1]);
        }
    }

    private void showSubScene(Viewport viewport) {
        for (Viewport vp : subScenes.keySet()) {
            subScenes.get(vp).getParent().setVisible(vp == viewport);
            subScenes.get(vp).getParent().setManaged(vp == viewport);
        }

        var subScene = subScenes.get(viewport);
        var vBox = (VBox) subScene.getParent();

        GridPane.setConstraints(vBox, 0, 0);
        GridPane.setColumnSpan(vBox, 2);
        GridPane.setRowSpan(vBox, 2);
        vBox.prefWidthProperty().bind(this.widthProperty());
        vBox.prefHeightProperty().bind(this.heightProperty());
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
