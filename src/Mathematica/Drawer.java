package Mathematica;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class Drawer {
    private GraphicsContext gc;
    private double activeWidth;
    private double activeHeight;
    private double horizontal;
    private double vertical;

    @Contract(pure = true)
    public Drawer(GraphicsContext gc, double activeWidth, double activeHeight, double horizontal, double vertical) {
        this.gc = gc;
        this.activeWidth = activeWidth;
        this.activeHeight = activeHeight;
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public void init(Color color, double bold) {
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(bold);
    }

    public void drawLine(@NotNull Point from, @NotNull Point to) {
        gc.beginPath();
        gc.moveTo(from.x * activeWidth + horizontal, (1 - from.y) * activeHeight + vertical);
        gc.lineTo(to.x * activeWidth + horizontal, (1 - to.y) * activeHeight + vertical);
        gc.stroke();
        gc.closePath();
    }

    void drawPoint(@NotNull Point point, double bold) {
        gc.fillOval(point.x * activeWidth + horizontal, (1 - point.y) * activeHeight + vertical, bold, bold);
    }

    public void text(String text, @NotNull Point point, boolean center) {
        TextAlignment alignment = center ? TextAlignment.CENTER : TextAlignment.LEFT;
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.7);
        gc.setFont(new Font("Verdana", 9));
        gc.setTextAlign(alignment);
        gc.strokeText(text, point.x * activeWidth + horizontal, (1 - point.y) * activeHeight + vertical);
    }

    public void clear() {
        //clear
        gc.clearRect(0, 0, activeWidth + 2 * horizontal, activeHeight + 2 * vertical);
        //border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);
        gc.strokeRect(0, 0, activeWidth + 2 * horizontal, activeHeight + 2 * vertical);
    }
}
