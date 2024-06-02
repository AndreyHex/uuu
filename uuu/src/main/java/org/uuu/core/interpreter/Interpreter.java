package org.uuu.core.interpreter;

import org.uuu.core.ast.*;

import java.util.Objects;

public class Interpreter implements Visitor<Object> {

    public static Object interpret(Expr expr) {
        return expr.accept(new Interpreter());
    }

    @Override
    public Object accept(Assign assign) {
        return null;
    }

    @Override
    public Object accept(Binary binary) {
        return switch (binary.getOperator().getType()) {
            case PLUS -> plus(evaluate(binary.getLeft()), evaluate(binary.getRight()));
            case STAR -> (double) evaluate(binary.getLeft()) * (double) evaluate(binary.getRight());
            case SLASH -> (double) evaluate(binary.getLeft()) / (double) evaluate(binary.getRight());
            case MINUS -> (double) evaluate(binary.getLeft()) - (double) evaluate(binary.getRight());
            case GREATER -> (double) evaluate(binary.getLeft()) > (double) evaluate(binary.getRight());
            case GREATER_EQUAL -> (double) evaluate(binary.getLeft()) >= (double) evaluate(binary.getRight());
            case LESS -> (double) evaluate(binary.getLeft()) < (double) evaluate(binary.getRight());
            case LESS_EQUAL -> (double) evaluate(binary.getLeft()) <= (double) evaluate(binary.getRight());
            case EQUAL_EQUAL -> Objects.equals(evaluate(binary.getLeft()), evaluate(binary.getRight()));
            case BANG_EQUAL -> !Objects.equals(evaluate(binary.getLeft()), evaluate(binary.getRight()));
            default -> null;
        };
    }

    private Object plus(Object a, Object b) {
        if (a instanceof Double ad && b instanceof Double bd) return ad + bd;
        if (a instanceof String as && b instanceof String bs) return as + bs;
        return null;
    }

    @Override
    public Object accept(Call call) {
        return null;
    }

    @Override
    public Object accept(Literal literal) {
        return literal.getValue();
    }

    @Override
    public Object accept(Unary unary) {
        return switch (unary.getOperator().getType()) {
            case MINUS -> -(double) evaluate(unary.getRight());
            case BANG -> !(boolean) evaluate(unary.getRight());
            default -> null;
        };
    }

    @Override
    public Object accept(Ternary ternary) {
        boolean evaluate = (boolean) evaluate(ternary.getCondition());
        if (evaluate) return evaluate(ternary.getOnTrue());
        else return evaluate(ternary.getOnFalse());
    }

    @Override
    public Object accept(Group group) {
        return evaluate(group.getExpression());
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
