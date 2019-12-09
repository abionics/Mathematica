package Mathematica;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;

public class GrapherFunction extends Grapher {
    private double pointsCount = 3000;
    private Expression expression;

    public GrapherFunction(String _expression) {
        expression = new Expression(_expression);
    }
    public void set(String _expression) {
        expression.set(_expression);
    }

    public void graph(double left, double right, double bottom, double top) {
        points = new ArrayList<>();
        if (right <= left) return;
        double step = (right - left) / (pointsCount - 1);
        HashMap<Character, Double> values = new HashMap<>();
        for (double x = left; x <= right; x += step) {
            values.put('x', x);
            double y = expression.calculate(values);
            if (Double.isFinite(y)) points.add(new Point(x, y));
            else points.add(new Point(x, Double.NaN));
        }
    }

    public void draw(Drawer drawer, Color color, double bold) {
        if (points.isEmpty()) return;
        drawer.init(color, bold);
        boolean newPart = true;
        Point last = points.get(0);
        for (var active : points) {
            if (active.y > 1) active.y = 1;
            if (active.y < 0) active.y = 0;
            if (Double.isNaN(active.y) || Math.abs(active.y - last.y) == 1 || (last.y == 1 && active.y == 1) || (last.y == 0 && active.y == 0)) {
                newPart = true;
            } else {
                if (!Double.isNaN(last.y)) {
                    drawer.drawLine(last, active);
                } else {
                    if (!newPart) drawer.drawLine(last, active);
                    else newPart = false;
                }
            }
            last = active;
        }
    }

    public double integrate(double left, double right, double bottom, double top) {
        double integral = 0;
        double step = (right - left) / (pointsCount - 1);
        graph(left, right, bottom, top);
        for (var point : points)
            integral += point.y * step;
        return integral;
    }

    public double derivative(double x) {
        final double epsilon = 0.001;
        HashMap<Character, Double> values = new HashMap<>();
        values.put('x', x + epsilon);
        double dx = expression.calculate(values);
        values = new HashMap<>();   //because calculate() changes values
        values.put('x', x);
        x = expression.calculate(values);
        return (dx - x) / epsilon;
    }
}
