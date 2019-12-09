package Mathematica;

import javafx.scene.paint.Color;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class GrapherEquation extends Grapher {
    private double pointsCountX = 420;
    private double pointsCountY = 315;
    private Expression expression;
    private Comparator comparator;

    public GrapherEquation(String expression) {
        this.expression = new Expression(define(expression));
    }
    public void set(String expression) {
        this.expression.set(define(expression));
    }
    @NotNull
    private String define(@NotNull String expression) {
        if (expression.isEmpty()) return "";
        int index = -1;
        for (var c : Comparator.values()) {
            index = expression.indexOf(c.symbol);
            if (index != -1) {
                comparator = c;
                break;
            }
        }
        //left = right  ->  left - (right) = 0
        return expression.substring(0, index) + "-(" + expression.substring(index + 1) + ")";
    }

    public void graph(double left, double right, double bottom, double top) {
        points = new ArrayList<>();
        if (right <= left || top <= bottom) return;
        double stepX = (right - left) / (pointsCountX - 1);
        double stepY = (top - bottom) / (pointsCountY - 1);
        HashMap<Character, Double> values = new HashMap<>();
        if (comparator == Comparator.EQUALS) {
            HashMap<Double, Double> leftValues = new HashMap<>();
            for (double y = bottom; y <= top; y += stepY) leftValues.put(y, Double.NaN);
            for (double x = left; x <= right; x += stepX) {
                values.put('x', x);
                double bottomValue = Double.NaN;
                for (double y = bottom; y <= top; y += stepY) {
                    values.put('y', y);
                    double z = expression.calculate(values);
                    if (Double.isFinite(z)) {
                        if (z * bottomValue <= 0) {
                            points.add(new Point(x, y));
                            points.add(new Point(x, y - stepY));
                        }
                        if (z * leftValues.get(y) <= 0) {
                            points.add(new Point(x, y));
                            points.add(new Point(x - stepX, y));
                        }
                    }
                    leftValues.put(y, z);
                    bottomValue = z;
                }
            }
        } else {
            for (double x = left; x <= right; x += stepX) {
                values.put('x', x);
                for (double y = bottom; y <= top; y += stepY) {
                    values.put('y', y);
                    double z = expression.calculate(values);
                    if (Double.isFinite(z))
                        switch (comparator) {
                            case MORE:
                                if (z > 0) points.add(new Point(x, y));
                                break;
                            case LESS:
                                if (z < 0) points.add(new Point(x, y));
                                break;
                        }
                }
            }
        }
    }

    public void draw(Drawer drawer, Color color, double bold) {
        if (points.isEmpty()) return;
        drawer.init(color, bold);
        points.forEach(point -> drawer.drawPoint(point, bold));
    }

    public double integrate(double left, double right, double bottom, double top) {
        double stepX = (right - left) / (pointsCountX - 1);
        double stepY = (top - bottom) / (pointsCountY - 1);
        graph(left, right, bottom, top);
        return points.size() * stepX * stepY;
    }

//    public double derivative(double x) {
//        final double EPSILON = 0.001;
//        HashMap<Character, Double> values = new HashMap<>();
//        values.put('x', x + EPSILON);
//        double dx = expression.calculate(values);
//        values.put('x', x);
//        x = expression.calculate(values);
//        return (dx - x) / EPSILON;
//    }

    static boolean isFit(String _expression) {
        for (var c : Comparator.values())
            if (_expression.contains(c.symbol)) return true;
        return false;
    }

    private enum Comparator {
        EQUALS("="), MORE(">"), LESS("<");

        final String symbol;

        @Contract(pure = true)
        Comparator(String _symbol) {
            symbol = _symbol;
        }
    }
}
