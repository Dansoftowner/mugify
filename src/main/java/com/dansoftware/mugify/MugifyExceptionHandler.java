package com.dansoftware.mugify;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.dansoftware.mugify.i18n.I18NUtils.val;

public class MugifyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Window ownerWindow;

    public MugifyExceptionHandler(Window ownerWindow) {
        this.ownerWindow = ownerWindow;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(ownerWindow);
            alert.setTitle(val("error.dialog.title").get());
            alert.setHeaderText(val("error.dialog.header").get());
            alert.setContentText(e.getMessage() != null ? e.getMessage() : val("error.dialog.unknown").get());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            TextArea textArea = new TextArea(sw.toString());
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setMaxWidth(Double.MAX_VALUE);
            textArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(textArea, Priority.ALWAYS);
            GridPane.setHgrow(textArea, Priority.ALWAYS);

            GridPane expandableContent = new GridPane();
            expandableContent.setMaxWidth(Double.MAX_VALUE);
            expandableContent.add(new Label(val("error.dialog.stacktrace").get()), 0, 0);
            expandableContent.add(textArea, 0, 1);

            alert.getDialogPane().setExpandableContent(expandableContent);
            alert.showAndWait();
        });
    }
}
