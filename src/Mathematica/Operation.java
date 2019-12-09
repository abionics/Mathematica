package Mathematica;

import org.jetbrains.annotations.Contract;

class Operation {
    char symbol;
    int priority;
    int count;
    Function func;
    OperationType type;

    @Contract(pure = true)
    Operation(char _symbol, int _priority, int _count, Function _func, OperationType _type) {
        symbol = _symbol;
        priority = _priority;
        count = _count;
        func = _func;
        type = _type;
        if (type == OperationType.function_prefix || type == OperationType.function_postfix)
            priority = Integer.MAX_VALUE;
    }
}
