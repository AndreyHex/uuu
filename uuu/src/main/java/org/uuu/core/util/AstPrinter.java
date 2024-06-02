package org.uuu.core.util;

import org.uuu.core.ast.Assign;
import org.uuu.core.ast.Call;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.ExprStmt;
import org.uuu.core.ast.statement.Stmt;
import org.uuu.core.ast.statement.Var;
import org.uuu.core.scanner.Token;

public class AstPrinter implements Visitor<String> {

    public static String print(Stmt stmt) {
        return stmt.accept(new AstPrinter());
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

    @Override
    public String accept(ExprStmt exprStmt) {
        return exprStmt.getExpression().accept(this) + ";\n";
    }

    @Override
    public String accept(Var var) {
        return "var " + var.getName().getLexeme() + " = " +
                (var.getInitializer() != null ? var.getInitializer().accept(this) : "") + ";";
    }

    @Override
    public String accept(Variable variable) {
        return variable.getName().getLexeme();
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
