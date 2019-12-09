package com.abionics.matematica;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class SimpleTimer {
    private long start = System.nanoTime();
    private static DecimalFormatSymbols separator = new DecimalFormatSymbols(Locale.getDefault());
    static {
        separator.setDecimalSeparator('.');
        separator.setGroupingSeparator(',');
    }
    private static DecimalFormat format = new DecimalFormat("#.##", separator);

    public long get() {
        return System.nanoTime() - start;
    }
    public void time() {
        System.out.println("Time: " + Double.valueOf(format.format(get() / 1000000.)) + " ms");
    }
    public void _time() {
        System.out.println("Time: " + get() + " ns");
    }
    public void set() {
        start = System.nanoTime();
    }
    public void round() {
        time();
        set();
    }
    public double getset() {
        double result = get();
        set();
        return result;
    }
}
