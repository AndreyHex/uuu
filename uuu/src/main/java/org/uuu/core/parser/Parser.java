package org.uuu.core.parser;

import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Assign;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.ExprStmt;
import org.uuu.core.ast.statement.Stmt;
import org.uuu.core.ast.statement.Var;
import org.uuu.core.scanner.Token;
import org.uuu.core.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class Parser {

    private final List<Token> tokens;
    private int i = 0;

    public List<Stmt> run() {
        List<Stmt> res = new ArrayList<>();
        while (!end()) res.add(declaration());
        return res;
    }

    public static List<Stmt> parse(List<Token> tokens) {
        return new Parser(tokens).run();
    }

    private Stmt declaration() {
        if (peek().getType().equals(TokenType.VAR)) return varDeclaration();
        else return statement();
    }

    private Stmt varDeclaration() {
        pop(); // pop 'var'
        Token name = pop();
        if (!name.getType().equals(TokenType.IDENTIFIER)) throw new RuntimeException("Expected identifier after var.");
        Expr expr = null;
        if (peek().getType().equals(TokenType.EQUAL)) {
            pop();
            expr = expression();
        }
        if (!peek().getType().equals(TokenType.SEMICOLON))
            throw new RuntimeException("Expected semicolon at the end of var declaration.");
        pop();
        return new Var(name, expr);
    }

    private Stmt statement() {
        Expr expr = expression();
        if (match(TokenType.EQUAL)) {
            pop(); //equals
            Expr value = expression();
            if (expr instanceof Variable variable) expr = new Assign(variable.getName(), value);
            else throw new RuntimeException("Invalid assignment.");
        }
        if (!peek().getType().equals(TokenType.SEMICOLON))
            throw new RuntimeException("Expected semicolon at the end of the statement.");
        pop();
        return new ExprStmt(expr);
    }


    private Expr expression() {
        Expr expr = equality();
        if (match(TokenType.QUESTION)) {
            pop();
            Expr onTrue = expression();
            if (!match(TokenType.COLON)) throw new RuntimeException("Expected colon");
            pop();
            Expr onFalse = expression();
            expr = new Ternary(expr, onTrue, onFalse);
        }
        return expr;
    }

    private Expr equality() {
        return recurs(this::comparison, TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL);
    }

    private Expr comparison() {
        return recurs(this::term, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);
    }

    private Expr term() {
        return recurs(this::factor, TokenType.PLUS, TokenType.MINUS);
    }

    private Expr factor() {
        return recurs(this::unary, TokenType.SLASH, TokenType.STAR);
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = pop();
            Expr right = unary();
            return new Unary(operator, right);
        } else return primary();
    }

    private Expr primary() {
        Token pop = pop();
        switch (pop.getType()) {
            case FALSE: return new Literal(false);
            case TRUE: return new Literal(true);
            case STRING, NUMBER: return new Literal(pop.getLiteral());
            case NULL: return new Literal(null);
            default: break;
        }

        if (pop.getType().equals(TokenType.IDENTIFIER)) return new Variable(pop);

        if (pop.getType().equals(TokenType.LEFT_PAREN)) {
            Expr grouped = expression();
            if (!pop().getType().equals(TokenType.RIGHT_PARENT)) throw new RuntimeException("Expected ')'");
            return new Group(grouped);
        }
        throw new RuntimeException("Unexpected symbol");
    }

    private Expr recurs(Supplier<Expr> sup, TokenType... types) {
        Expr left = sup.get();
        while (match(types)) {
            Token operator = pop();
            Expr right = sup.get();
            left = new Binary(operator, left, right);
        }
        return left;
    }

    private Token pop() {
        return tokens.get(i++);
    }

    private boolean match(TokenType... types) {
        if (end()) return false;
        for (TokenType type : types) if (type.equals(peek().getType())) return true;
        return false;
    }

    private Token peek() {
        return tokens.get(i);
    }

    private boolean end() {
        return i >= tokens.size() || peek().getType().equals(TokenType.EOF);
    }

}
