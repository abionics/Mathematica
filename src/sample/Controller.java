package sample;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Controller {
    @FXML private Canvas canvas;
    @FXML private ScrollPane scroll;

    private Model model;
    VBox root;
    ArrayList<Field> fields;

    @FXML private void initialize() {
        model = new Model(canvas);
        root = new VBox();
        fields = new ArrayList<>();

        scroll.setContent(root);
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        Field.create(this);
        graph();
    }

    void graph() {
        SimpleTimer timer = new SimpleTimer();
        model.graph(fields);
        timer.time();
    }

    @FXML private void zoom(@NotNull ZoomEvent zoomEvent) {
        model.zoom(1 / zoomEvent.getZoomFactor());
        graph();
    }
    @FXML private void scroll(@NotNull ScrollEvent scrollEvent) {
        model.scroll(scrollEvent.getDeltaX(), scrollEvent.getDeltaY());
        graph();
    }

    @FXML private void centrate() {
        model.centrate();
        graph();
    }
    @FXML private void clear() {
        fields.clear();
        Field.clear();
        initialize();
        graph();
    }
}
