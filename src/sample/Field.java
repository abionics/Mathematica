package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

class Field {
    static final private Color[] colors = new Color[]{Color.BLUE, Color.GREEN, Color.RED, Color.PURPLE, Color.ORANGE};
    static private int id = 0;
    static void clear() {
        id = 0;
    }

    private TextField field;
    private ColorPicker picker;

    private Field(Controller controller) {
        InvalidationListener listener = new InvalidationListener() {
            @Override
            public void invalidated(@NotNull Observable observable) {
                create(controller);
                observable.removeListener(this);
            }
        };
        field = new TextField();
        field.setPrefWidth(175);
        field.setPromptText("Input function...");
        field.setOnAction(actionEvent -> controller.graph());
        field.textProperty().addListener(listener);
        picker = new ColorPicker(colors[id++ % colors.length]);
        picker.setPrefWidth(50);
        picker.setOnAction(actionEvent -> controller.graph());
    }

    @NotNull
    @Contract(" -> new")
    private HBox boxing() {
        return new HBox(10, field, picker);
    }

    static void create(Controller controller) {
        var line = new Field(controller);
        controller.fields.add(line);
        controller.root.getChildren().add(line.boxing());
    }

    String getExpression() {
        return field.getText();
    }
    Color getColor() {
        return picker.getValue();
    }
}
