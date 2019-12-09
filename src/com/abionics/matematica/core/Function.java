package com.abionics.matematica.core;

import java.util.ArrayList;

public interface Function<T> {
    T function(ArrayList<T> vars);
}
