package Mathematica;

import javafx.scene.paint.Color;

import java.util.ArrayList;

public abstract class Grapher {
//    static final private double MAX_ABS_Y = 1e9;
    protected ArrayList<Point> points = new ArrayList<>();

    public abstract void set(String _expression);
    public abstract void graph(double left, double right, double bottom, double top);
    public void normalize(double left, double right, double bottom, double top) {
        final double epsilon = 0.0001;
        double width = right - left + epsilon;
        double height = top - bottom + epsilon;

        for (var point : points) {
            double x = (point.x - left) / width;
            double y = (point.y - bottom) / height;
            point.x = x;
            if (x >= 0 && x <= 1 && Double.isFinite(y)) {
                point.y = y;
            } else {
                point.y = Double.NaN;
            }
        }
    }
    public abstract void draw(Drawer drawer, Color color, double bold);

    public abstract double integrate(double left, double right, double bottom, double top);

    public static boolean isEquation(String string) {
        return GrapherEquation.isFit(string);
    }

//    public static ArrayList<Point> normalize(@NotNull ArrayList<Point> points, double left, double right) {
//        double bottom = -Double.MAX_VALUE;
//        double top = Double.MAX_VALUE;
//        for (var points : graphs) {
//            double y = points.y;
//            if (y < bottom) bottom = y;
//            if (y > top) top = y;
//        }
//        bottom = Math.max(bottom, -MAX_ABS_Y);
//        top = Math.max(top, MAX_ABS_Y);
//
//        return normalize(graphs, left, right, top, bottom);
//    }

//    public static ArrayList<Point> normalize(@NotNull ArrayList<Point> points, double left, double right, double bottom, double top) {
//        final double epsilon = 0.0001;
//        double width = right - left + epsilon;
//        double height = top - bottom + epsilon;
//
//        ArrayList<Point> normalized = new ArrayList<>();
//        for (var point : points) {
//            double x = (point.x - left) / width;
//            double y = (point.y - bottom) / height;
//            if (x >= 0 && x <= 1 && y >= 0 && y <= 1) {
//                normalized.add(new Point(x, y));
//            } else {
//                normalized.add(new Point(x, Double.NaN));
//            }
//        }
//        return normalized;
//    }
}
