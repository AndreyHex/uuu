package org.uuu.core.util;

import org.uuu.core.ast.Call;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.*;
import org.uuu.core.scanner.Token;

import java.util.stream.Collectors;

public class AstPrinter implements Visitor<String> {

    public static String print(Stmt stmt) {
        return stmt.accept(new AstPrinter());
    }

    @Override
    public String accept(Assign assign) {
        return assign.getName().getLexeme() + " = " + assign.getValue().accept(this);
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

    @Override
    public String accept(Block block) {
        return "{ " + block.getStatements().stream().map(e -> e.accept(this)).collect(Collectors.joining(" ")) + " }";
    }

    @Override
    public String accept(If anIf) {
        return "if(" + anIf.getCondition().accept(this) + ") " + anIf.getOnTrue().accept(this) + "" +
                (anIf.getOnFalse() == null ? "" : "else " + anIf.getOnFalse().accept(this));
    }

    @Override
    public String accept(Logic logic) {
        return "(" + printOperator(logic.getOperator()) + " " + logic.getLeft().accept(this) + " " +
                logic.getRight().accept(this) + ")";
    }

    @Override
    public String accept(While aWhile) {
        return "while(" + aWhile.getCondition().accept(this) + ") " + aWhile.getBody().accept(this);
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
            case OR -> "|";
            case AND -> "&";
            default -> throw new IllegalStateException("Unexpected value: " + token.getType());
        };
    }
}
