package org.uuu.core.interpreter;

import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Call;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.*;
import org.uuu.core.runtime.Environment;
import org.uuu.core.scanner.TokenType;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class Interpreter implements Visitor<Object> {

    private final List<Stmt> statements;

    Environment env = new Environment();

    public static void interpret(List<Stmt> statements) {
        new Interpreter(statements).interpret();
    }

    public Interpreter interpret() {
        statements.forEach(e -> e.accept(this));
        return this;
    }

    @Override
    public Object accept(Assign assign) {
        Object value = evaluate(assign.getValue());
        env.assign(assign.getName(), value);
        return value;
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

    @Override
    public Object accept(ExprStmt exprStmt) {
        evaluate(exprStmt.getExpression());
        return null;
    }

    @Override
    public Object accept(Var var) {
        if (var.getInitializer() != null) env.define(var.getName(), evaluate(var.getInitializer()));
        else env.define(var.getName(), null);
        return null;
    }

    @Override
    public Object accept(Variable variable) {
        return env.get(variable.getName());
    }

    @Override
    public Object accept(Block block) {
        executeBlock(block.getStatements(), new Environment(env));
        return null;
    }

    @Override
    public Object accept(If anIf) {
        boolean res = (boolean) anIf.getCondition().accept(this);
        if (res) return anIf.getOnTrue().accept(this);
        else return anIf.getOnFalse().accept(this);
    }

    @Override
    public Object accept(Logic logic) {
        Object res = logic.getLeft().accept(this);
        if (logic.getOperator().getType().equals(TokenType.OR)) {
            if ((boolean) res) return true;
        } else if (!(boolean) res) return false;
        return logic.getRight().accept(this);
    }

    @Override
    public Object accept(While aWhile) {
        while ((boolean) aWhile.getCondition().accept(this)) aWhile.getBody().accept(this);
        return null;
    }

    private void executeBlock(List<Stmt> statements, Environment environment) {
        Environment prev = env;
        try {
            env = environment;
            statements.forEach(e -> e.accept(this));
        } finally {
            env = prev;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }
}
