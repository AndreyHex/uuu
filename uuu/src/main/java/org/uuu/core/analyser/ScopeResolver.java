package org.uuu.core.analyser;

import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.*;
import org.uuu.core.interpreter.Interpreter;
import org.uuu.core.scanner.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ScopeResolver implements Visitor<Void> {

    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    private final Interpreter interpreter;

    public ScopeResolver(Interpreter interpreter) {
        this.interpreter = interpreter;
        beginScope();
    }

    @Override
    public Void accept(Block block) {
        beginScope();
        block.getStatements().forEach(e -> e.accept(this));
        endScope();
        return null;
    }

    @Override
    public Void accept(Var var) {
        declare(var.getName());
        if (var.getInitializer() != null) var.getInitializer().accept(this);
        define(var.getName());
        return null;
    }

    @Override
    public Void accept(Variable variable) {
        if (!scopes.isEmpty() && scopes.peek().get(variable.getName().getLexeme()) == Boolean.FALSE)
            throw new RuntimeException("Cannot read local variable in its own initializer.");
        resolve(variable, variable.getName());
        return null;
    }

    private void resolve(Expr expr, Token name) {
        for (int i = scopes.size() - 1; i >= 0; i--)
            if (scopes.get(i).containsKey(name.getLexeme())) {
                interpreter.resolve(expr, scopes.size() - 1 - i);
                return;
            }
    }

    private void resolve(Fn fn) {
        beginScope();
        fn.getParams().forEach(e -> {
            declare(e);
            define(e);
        });
        fn.getBody().forEach(e -> e.accept(this));
        endScope();
    }

    private void declare(Token name) {
        if (scopes.peek().containsKey(name.getLexeme()))
            throw new RuntimeException("Variable with name '%s' already defined in the scope.".formatted(name.getLexeme()));
        scopes.peek().put(name.getLexeme(), false);
    }

    private void define(Token name) {
        scopes.peek().put(name.getLexeme(), true);
    }


    private void endScope() {
        scopes.pop();
    }

    private void beginScope() {
        scopes.push(new HashMap<>());
    }


    @Override
    public Void accept(Assign assign) {
        assign.getValue().accept(this);
        resolve(assign, assign.getName());
        return null;
    }

    @Override
    public Void accept(Binary binary) {
        binary.getLeft().accept(this);
        binary.getRight().accept(this);
        return null;
    }

    @Override
    public Void accept(Call call) {
        call.getCallee().accept(this);
        call.getArgs().forEach(e -> e.accept(this));
        return null;
    }

    @Override
    public Void accept(Literal literal) {
        return null;
    }

    @Override
    public Void accept(Unary unary) {
        unary.getRight().accept(this);
        return null;
    }

    @Override
    public Void accept(Ternary ternary) {
        ternary.getCondition().accept(this);
        ternary.getOnTrue().accept(this);
        ternary.getOnFalse().accept(this);
        return null;
    }

    @Override
    public Void accept(Group group) {
        group.getExpression().accept(this);
        return null;
    }

    @Override
    public Void accept(ExprStmt exprStmt) {
        exprStmt.getExpression().accept(this);
        return null;
    }

    @Override
    public Void accept(If anIf) {
        anIf.getCondition().accept(this);
        anIf.getOnTrue().accept(this);
        if (anIf.getOnFalse() != null) anIf.getOnFalse().accept(this);
        return null;
    }

    @Override
    public Void accept(Logic logic) {
        logic.getLeft().accept(this);
        logic.getRight().accept(this);
        return null;
    }

    @Override
    public Void accept(While aWhile) {
        aWhile.getCondition().accept(this);
        aWhile.getBody().accept(this);
        return null;
    }

    @Override
    public Void accept(Fn fn) {
        declare(fn.getName());
        define(fn.getName());
        resolve(fn);
        return null;
    }

    @Override
    public Void accept(Return aReturn) {
        if (aReturn.getValue() != null) aReturn.getValue().accept(this);
        return null;
    }

    @Override
    public Void accept(ClassStmt aClass) {
        declare(aClass.getName());
        define(aClass.getName());
        if (aClass.getSuperclass() != null &&
            aClass.getSuperclass().getName().getLexeme().equals(aClass.getName().getLexeme()))
            throw new RuntimeException("Cannot inherit from itself.");
        if (aClass.getSuperclass() != null) {
            aClass.getSuperclass().accept(this);
            beginScope();
            scopes.peek().put("super", true);
        }
        beginScope();
        scopes.peek().put("self", true);
        aClass.getMethods().forEach(this::resolve);
        endScope();
        if (aClass.getSuperclass() != null) endScope();
        return null;
    }

    @Override
    public Void accept(Get get) {
        get.getObject().accept(this);
        return null;
    }

    @Override
    public Void accept(Set set) {
        set.getValue().accept(this);
        set.getObject().accept(this);
        return null;
    }

    @Override
    public Void accept(Self self) {
        resolve(self, self.getKeyword());
        return null;
    }

    @Override
    public Void accept(Super aSuper) {
        resolve(aSuper, aSuper.getKeyword());
        return null;
    }
}
