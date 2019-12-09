package Mathematica;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

class Expression {
    private static final int NUMBERS_RESERVED = 32768;
    private static final char DECIMAL_SEPARATOR = '.';
    private static final MathParser<Double> defaultParser = getDefaultParser();
    private MathParser<Double> parser;
    private String expression;
    private HashMap<Character, Double> numbersTable = new HashMap<>();

    private Expression(String expression, MathParser<Double> parser) {
        this.parser = parser;
        set(expression);
    }
    Expression(String _expression) {
        this(_expression, defaultParser);
    }
    MathParser<Double> getParser() {
        return parser;
    }

    @NotNull
    private String numberize(@NotNull String expression) {
        numbersTable.clear();
        StringBuilder compressed = new StringBuilder(expression.length());
        StringBuilder number = new StringBuilder();
        expression = expression + " ";
        for (char ch : expression.toCharArray()) {
            if ((ch >= '0' && ch <= '9') || ch == DECIMAL_SEPARATOR) number.append(ch);
            else {
                if (!number.toString().isEmpty()) {
                    double value = Double.parseDouble(number.toString());
                    char code = (char) (NUMBERS_RESERVED + numbersTable.size());
                    numbersTable.put(code, value);
                    number = new StringBuilder();
                    compressed.append(code);
                }
                if (ch != ' ') compressed.append(ch);
            }
        }
        return compressed.toString();
    }
    private void addNumbersAsVars() {
        for (var line : numbersTable.entrySet())
            parser.addVar(line.getKey());
    }
    private void removeNumbersAsVars() {
        for (var line : numbersTable.entrySet())
            parser.removeVar(line.getKey());
    }

    void set(String expression) {
        removeNumbersAsVars();
        expression = numberize(expression);
        addNumbersAsVars();
        this.expression = parser.translate(parser.compress(expression));
    }

    //method changes values
    double calculate(@NotNull HashMap<Character, Double> values) {
//        HashMap<Character, Double> values = new HashMap<>(_values);
        values.putAll(numbersTable);
        return parser.calculate(expression, values);
    }

    private static MathParser<Double> getDefaultParser() {
        final MathParser<Double> instance = new MathParser<>();
        instance.addOperation('+', 3, 2, vars -> (double) vars.get(0) + (double) vars.get(1));
        instance.addOperation('-', 3, 2, vars -> (double) vars.get(0) - (double) vars.get(1));
        instance.addOperation('*', 4, 2, vars -> (double) vars.get(0) * (double) vars.get(1));
        instance.addOperation('/', 4, 2, vars -> (double) vars.get(0) / (double) vars.get(1));
        instance.addOperation('^', 5, 2, vars -> Math.pow((double) vars.get(0), (double) vars.get(1)), OperationType.right);
        instance.addOperation('~', 0, 1, vars -> -(double) vars.get(0), OperationType.function_prefix);
        instance.addOperation('√', 0, 1, vars -> Math.sqrt((double) vars.get(0)), OperationType.function_prefix);
        instance.addOperation('&', 0, 1, vars -> Math.sin((double) vars.get(0)), OperationType.function_prefix);
        instance.addOperation('|', 0, 1, vars -> Math.cos((double) vars.get(0)), OperationType.function_prefix);
        instance.addOperation('@', 0, 1, vars -> Math.abs((double) vars.get(0)), OperationType.function_prefix);
        instance.addOperation('$', 0, 1, vars -> Math.log((double) vars.get(0)), OperationType.function_prefix);
        instance.addOperation('!', 0, 1, vars -> {
            double val = (double) vars.get(0);
            if (val < 0) return Double.NaN;
            double factorial = 1;
            for (int i = 2; i <= (int) val; i++)
                factorial *= i;
            return factorial;
        }, OperationType.function_postfix);

        instance.addWide("sqrt", '√');
        instance.addWide("sin", '&');
        instance.addWide("cos", '|');
        instance.addWide("abs", '@');
        instance.addWide("log", '$');
        instance.addWide("ln", '$');

        instance.addUnary('-', '~');

        for (char i = 'a'; i <= 'z'; i++)
            instance.addVar(i);
        for (char i = 'A'; i <= 'Z'; i++)
            instance.addVar(i);

        return instance;
    }
}
