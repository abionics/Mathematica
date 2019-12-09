package sample;

import Mathematica.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.function.DoubleFunction;

class Model {
    private final Drawer drawer;

    //Dont use static! Model is running in several threads
    final private double horizontal = 10;
    final private double vertical = 10;
    final private double horizontalSubscribes = 12;
    final private double verticalSubscribes = 9;

    private Point center = new Point(0, 0);
    private Point scale = new Point(8, 6);

    @Contract(pure = true) private double left() {
        return center.x - scale.x;
    }
    @Contract(pure = true) private double right() {
        return center.x + scale.x;
    }
    @Contract(pure = true) private double bottom() {
        return center.y - scale.y;
    }
    @Contract(pure = true) private double top() {
        return center.y + scale.y;
    }

    private DoubleFunction<Double> normalizeX = (x) -> (x - left()) / (right() - left());
    private DoubleFunction<Double> normalizeY = (y) -> (y - bottom()) / (top() - bottom());

    Model(@NotNull Canvas canvas) {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        double activeWidth = width - 2 * horizontal;
        double activeHeight = height - 2 * vertical;
        drawer = new Drawer(canvas.getGraphicsContext2D(), activeWidth, activeHeight, horizontal, vertical);
    }

    void zoom(double value) {
        scale.x *= value;
        scale.y *= value;
    }
    void scroll(double valueX, double valueY) {
        center.x += scale.x * valueX / 100 / 2;
        center.y += scale.y * valueY / 100 / 2;
    }
    void centrate() {
        center = new Point(0, 0);
    }

    void graph(@NotNull ArrayList<Field> fields) {
        drawer.clear();
        Grapher grapher;
        Grapher equation = new GrapherEquation("");
        Grapher function = new GrapherFunction("");
        for (var field : fields) {
            var expression = field.getExpression();
            var color = field.getColor();
            if (expression.isEmpty()) continue;
            if (Grapher.isEquation(expression)) {
                equation.set(expression);
                grapher = equation;
            } else {
                function.set(expression);
                grapher = function;
            }
            grapher.graph(left(), right(), bottom(), top());
            grapher.normalize(left(), right(), bottom(), top());
            grapher.draw(drawer, color, 2);
        }
        layout();
//        System.out.println("Integral: " + grapher.integrate(0,100));
//        System.out.println("Derivative in " + 100 + " : " + grapher.derivative(100));
    }

    private void layout() {
        double middleX = normalizeX.apply(0);
        double middleY = normalizeY.apply(0);

        drawer.init(Color.BLACK, 1);
        drawer.drawLine(new Point(0, middleY), new Point(1, middleY));
        drawer.drawLine(new Point(middleX, 0), new Point(middleX, 1));

        //horizontal subscribes
        double step = roundToFirstDigit((right() - left()) / horizontalSubscribes);
        for (double i = Math.floor(left() / step) * step; i <= right(); i += step) {
            String text = format(i);
            if (text.equals("0") || text.equals("-0")) continue;
            double x = normalizeX.apply(i);
            drawer.text(text, new Point(x, middleY - 0.02), true);
            drawer.drawLine(new Point(x, middleY - 0.004), new Point(x, middleY + 0.004));
        }
        //vertical subscribes
        step = roundToFirstDigit((top() - bottom()) / verticalSubscribes);
        for (double i = Math.floor(bottom() / step) * step; i <= top(); i += step) {
            String text = format(i);
            if (text.equals("0") || text.equals("-0")) continue;
            double y = normalizeY.apply(i);
            drawer.text(text, new Point(middleX + 0.005, y - 0.005), false);
            drawer.drawLine(new Point(middleX - 0.003, y), new Point(middleX + 0.003, y));
        }
        //zero
        drawer.text("0", new Point(middleX + 0.005, middleY - 0.02), false);
    }

    private double roundToFirstDigit(double value) {
        double signum = Math.signum(value);
        value = Math.abs(value);
        double exponent = Math.pow(10, Math.floor(Math.log10(value)));
        double times = Math.floor(value / exponent);
        if (times > 1) {
            if (times < 5) times = 2;
            else if (times < 9) times = 5;
            else times = 10;
        }
        return signum * times * exponent;
    }
    private String format(double value) {
        final DecimalFormat format = new DecimalFormat("##########.##########");
        return format.format(value);
    }
}
