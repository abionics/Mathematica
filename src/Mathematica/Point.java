package Mathematica;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Point {
    public double x;
    public double y;
    @Contract(pure = true)
    public Point(double _x, double _y) {
        x = _x;
        y = _y;
    }
    @Contract(pure = true)
    public Point(@NotNull Point point) {
        x = point.x;
        y = point.y;
    }

    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x) ^ Objects.hash(y);
    }

    @Override
    public String toString() {
        return "[" + x + ";" + y + "]";
    }
}
