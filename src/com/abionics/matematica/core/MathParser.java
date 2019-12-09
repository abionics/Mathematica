package com.abionics.matematica.core;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

class MathParser<T> {
    private HashMap<Character, Operation> operations = new HashMap<>();
    private HashSet<Integer> vars = new HashSet<>();
    private HashMap<String, Character> wides = new HashMap<>();
    private HashMap<Character, Character> unaries = new HashMap<>();

    void addOperation(char symbol, int priority, int count, Function func, OperationType type) {
        operations.put(symbol, new Operation(symbol, priority, count, func, type));
    }
    void addOperation(char symbol, int priority, int count, Function func) {
        addOperation(symbol, priority, count, func, OperationType.left);
    }
    void addVar(int symbol) {
        vars.add(symbol);
    }
    void addWide(String from, char to) {
        if (operations.containsKey(to)) {
            wides.put(from, to);
        } else {
            error(21, to);
        }
    }
    void addUnary(char from, char to) {
        if (operations.containsKey(to)) {
            unaries.put(from, to);
        } else {
            error(21, to);
        }
    }

    void removeOperation(char symbol) {
        operations.remove(symbol);
    }
    void removeVar(int symbol) {
        vars.remove(symbol);
    }
    void removeWide(String wide) {
        wides.remove(wide);
    }
    void removeUnary(char unary) {
        unaries.remove(unary);
    }

    HashSet<Character> usedVars(String expression) {
        HashSet<Character> result = new HashSet<>();
        String compressed = compress(expression);
        for (char token : compressed.toCharArray())
            if (vars.contains((int) token))
                result.add(token);
        return result;
    }
    String compress(String expression) {
        StringBuilder result = new StringBuilder(expression);
        for (var line : wides.entrySet()) {
            replaceAll(result, line.getKey(), Character.toString(line.getValue()));
        }
        if (!result.toString().isEmpty()) {
            char ch = result.charAt(0);
            if (unaries.containsKey(ch))
                result.setCharAt(0, unaries.get(ch));
        }
        for (int i = 1; i < result.length(); i++) {
            char ch = result.charAt(i);
            if (unaries.containsKey(result.charAt(i))) {
                Operation prev = operations.get(result.charAt(i - 1));
                if ((prev != null && prev.type != OperationType.function_postfix) ||
                        (result.charAt(i - 1) == '('))
                    result.setCharAt(i, unaries.get(ch));
            }
        }
        return result.toString();
    }
    String translate(@NotNull String expression) {
        AdvanceList<Character> stack = new AdvanceList<>();
        StringBuilder result = new StringBuilder();

        for (char token : expression.toCharArray()) {
            if (vars.contains((int) token)) {
                result.append(token);
                continue;
            }
            if (token == '(') {
                stack.add(token);
                continue;
            }
            Operation operation = operations.get(token);
            if (operation != null) {
                int prior = operation.priority;
                switch (operation.type) {
                    case left:
                    case right: {
                        while (!stack.isEmpty() && stack.back() != '(') {
                            Operation last = operations.get(stack.back());
                            if (last.priority > prior || (last.priority == prior && last.type == OperationType.left)) {
                                result.append(stack.back());
                                stack.remove();
                            } else break;
                        }
                        stack.add(token);
                        break;
                    }
                    case function_prefix: {
                        stack.add(token);
                        break;
                    }
                    case function_postfix: {
                        result.append(token);
                        break;
                    }
                }
                continue;
            }
            if (token == ')') {
                while (!stack.isEmpty() && (stack.back() != '(')) {
                    result.append(stack.back());
                    stack.remove();
                }
                if (stack.isEmpty()) {
                    error(1, token);
                    continue;
                }
                stack.remove();
                if (!stack.isEmpty()) {
                    Operation last = operations.get(stack.back());
                    if (last != null && last.type == OperationType.function_prefix) {
                        result.append(stack.back());
                        stack.remove();
                    }
                }
                continue;
            }
            if (token == ' ') continue;
            error(2, token);
        }

        while (!stack.isEmpty()) {
            if (stack.back() == '(') {
                error(3, '(');
            }
            result.append(stack.back());
            stack.remove();
        }

        return result.toString();
    }
    T calculate(@NotNull String expression, HashMap<Character, T> values) {
        if (expression.isEmpty()) {
            error(11, ' ');
            return null;
        }

        AdvanceList<T> stack = new AdvanceList<>();
        for (char token : expression.toCharArray()) {
            T var = values.get(token);
            if (var != null) {
                stack.add(var);
                continue;
            }
            Operation operation = operations.get(token);
            if (operation != null) {
                int count = operation.count;
                if (stack.size() >= count) {
                    ArrayList<T> inputs = new ArrayList<>(Collections.nCopies(count, null));
                    for (int i = 0; i < count; i++) {
                        inputs.set(count - i - 1, stack.back());
                        stack.remove();
                    }
                    stack.add((T) operation.func.function(inputs));
                } else {
                    error(12, token);
                }
            } else {
                error(13, token);
            }
        }

        if (stack.size() != 1) {
            char token;
            if (stack.size() > 9) token = '%';
            else token = (char) (stack.size() + 48);
            error(14, token);
            return null;
        }

        return stack.back();
    }
    T parse(String expression, HashMap<Character, T> values) {
        return calculate(translate(compress(expression)), values);
    }

    private void error(int error, char token) {
        switch (error) {
            case 1: {
                System.out.println("Error 1 (translate): Mistake in \"(\" or \")\", or incorrect order of operations in token = " + token);
                break;
            }
            case 2: {
                System.out.println("Error 2 (translate): Undefined token = " + token);
                break;
            }
            case 3: {
                System.out.println("Error 3 (translate): Missing \")\" or extra \"(\"");
                break;
            }
            case 11: {
                System.out.println("Error 11 (calculate): Empty expression \"\"");
                break;
            }
            case 12: {
                System.out.println("Error 12 (calculate): Operation (token = " + token + ") dont have enough arguments");
                break;
            }
            case 13: {
                System.out.println("Error 13 (calculate): Undefined operator, token = " + token);
                break;
            }
            case 14: {
                System.out.println("Error 14 (calculate): Wrong count of elements in stack = " + token);
                break;
            }
            case 21: {
                System.out.println("Error 21 (addWide / addUnary): Undefined operation, token = " + token);
                break;
            }
            case 31: {
                System.out.println("Error 31 (compress): Undefined operator, token = " + token);
                break;
            }
            default: {
                System.out.println("Error UND: Unknown error, token = " + token);
            }
        }
    }

    private static void replaceAll(@NotNull StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    class AdvanceList<X> extends ArrayList<X> {
        X back() {
            return get(size() - 1);
        }
        X remove() {
            return remove(size() - 1);
        }
    }
}
