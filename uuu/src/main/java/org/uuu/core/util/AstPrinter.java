package org.uuu.core.util;

import org.uuu.core.ast.*;
import org.uuu.core.scanner.Token;

public class AstPrinter implements Visitor<String> {

    public static String print(Expr expr) {
        return expr.accept(new AstPrinter());
    }

    @Override
    public String accept(Assign assign) {
        return "";
    }

    @Override
    public String accept(Binary binary) {
        return "(" + printOperator(binary.getOperator()) + " " +
                binary.getLeft().accept(this) + " " +
                binary.getRight().accept(this) + ")";
    }

    @Override
    public String accept(Call call) {
        return "";
    }

    @Override
    public String accept(Literal literal) {
        return "" + literal.getValue().toString() + "";
    }

    @Override
    public String accept(Unary unary) {
        return "(" + printOperator(unary.getOperator()) + " " + unary.getRight().accept(this) + ")";
    }

    @Override
    public String accept(Ternary ternary) {
        return "(" + ternary.getCondition().accept(this) + " ? " +
                ternary.getOnTrue().accept(this) + " : " + ternary.getOnFalse().accept(this) + ")";
    }

    @Override
    public String accept(Group group) {
        return "(group " + group.getExpression().accept(this) + ")";
    }

    private String printOperator(Token token) {
        return switch (token.getType()) {
            case BANG -> "!";
            case STAR -> "*";
            case SLASH -> "/";
            case MINUS -> "-";
            case PLUS -> "+";
            case TRUE -> "true";
            case FALSE -> "false";
            case GREATER -> ">";
            case LESS -> "<";
            case EQUAL_EQUAL -> "==";
            default -> throw new IllegalStateException("Unexpected value: " + token.getType());
        };
    }
}
