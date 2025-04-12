package com.dansoftware.mugify.mug;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;

public class MugChangeObserver {

    private final BooleanProperty changed;

    public MugChangeObserver(MugLike mug) {
        changed = new SimpleBooleanProperty(true);
        ChangeListener<Object> changeListener = (_, _, _) -> changed.set(true);

        mug.borderThicknessProperty().addListener(changeListener);
        mug.radiusProperty().addListener(changeListener);
        mug.heightProperty().addListener(changeListener);
        mug.outerColorProperty().addListener(changeListener);
        mug.innerColorProperty().addListener(changeListener);
        mug.bottomColorProperty().addListener(changeListener);
        mug.handleRadiusProperty().addListener(changeListener);
        mug.handleColorProperty().addListener(changeListener);
        mug.handleWidthProperty().addListener(changeListener);
        mug.handleRoundedProperty().addListener(changeListener);
        mug.nameProperty().addListener(changeListener);
    }

    public void commit() {
        changed.set(false);
    }

    public boolean isChanged() {
        return changed.get();
    }

    public ReadOnlyBooleanProperty changedProperty() {
        return changed;
    }
}