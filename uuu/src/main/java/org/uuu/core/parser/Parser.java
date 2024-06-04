package org.uuu.core.parser;

import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.*;
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
        if (end() || !peek().getType().equals(TokenType.SEMICOLON))
            throw new RuntimeException("Expected semicolon at the end of var declaration.");
        pop();
        return new Var(name, expr);
    }

    private Stmt statement() {
        if (peek().getType().equals(TokenType.LEFT_BRACE)) return block();
        if (peek().getType().equals(TokenType.IF)) return ifStmt();
        if (peek().getType().equals(TokenType.WHILE)) return whileStmt();
        if (peek().getType().equals(TokenType.FOR)) return forStmt();
        ExprStmt exprStmt = new ExprStmt(exprStmt());
        if (!peek().getType().equals(TokenType.SEMICOLON))
            throw new RuntimeException("Expected semicolon at the end of the statement.");
        pop();
        return exprStmt;
    }

    private Expr exprStmt() {
        Expr expr = expression();
        if (match(TokenType.EQUAL)) {
            pop(); //equals
            Expr value = expression();
            if (expr instanceof Variable variable) expr = new Assign(variable.getName(), value);
            else throw new RuntimeException("Invalid assignment.");
        }
        return expr;
    }

    private Stmt ifStmt() {
        pop(); // pop if
        if (!pop().getType().equals(TokenType.LEFT_PAREN)) throw new RuntimeException("Expected left paren.");
        Expr condition = expression();
        if (!pop().getType().equals(TokenType.RIGHT_PARENT)) throw new RuntimeException("Expected right paren.");
        Stmt onTrue = statement();
        if (peek().getType().equals(TokenType.ELSE)) {
            pop();
            Stmt onFalse = statement();
            return new If(condition, onTrue, onFalse);
        }
        return new If(condition, onTrue, null);
    }

    private Stmt whileStmt() {
        pop(); // pop while
        if (!pop().getType().equals(TokenType.LEFT_PAREN)) throw new RuntimeException("Expected left paren.");
        Expr condition = expression();
        if (!pop().getType().equals(TokenType.RIGHT_PARENT)) throw new RuntimeException("Expected right paren.");
        Stmt body = statement();
        return new While(condition, body);
    }

    private Stmt forStmt() {
        pop(); // pop for
        if (!pop().getType().equals(TokenType.LEFT_PAREN)) throw new RuntimeException("Expected '(' after 'for'.");

        Stmt initializer;
        Expr condition = null;
        Expr increment = null;

        if (peek().getType().equals(TokenType.SEMICOLON)) initializer = null;
        else if (peek().getType().equals(TokenType.VAR)) initializer = varDeclaration();
        else initializer = new ExprStmt(exprStmt());

        if (!peek().getType().equals(TokenType.RIGHT_PARENT)) condition = expression();
        if (!pop().getType().equals(TokenType.SEMICOLON)) throw new RuntimeException("Expected ';' after condition.");
        if (!peek().getType().equals(TokenType.RIGHT_PARENT)) increment = exprStmt();
        if (!pop().getType().equals(TokenType.RIGHT_PARENT))
            throw new RuntimeException("Expected ')' after 'for' clauses");
        Stmt body = statement();

        // transform to while loop
        if (increment != null) body = new Block(List.of(body, new ExprStmt(increment)));
        if (condition != null) body = new While(condition, body);
        else body = new While(new Literal(true), body);
        if (initializer != null) body = new Block(List.of(initializer, body));
        return body;
    }

    private Stmt block() {
        pop(); // pop open brace
        List<Stmt> statements = new ArrayList<>();
        while (!end() && !peek().getType().equals(TokenType.RIGHT_BRACE)) statements.add(declaration());
        Token pop = pop();
        if (!pop.getType().equals(TokenType.RIGHT_BRACE))
            throw new RuntimeException("Unexpected symbol at the end of the block.");
        return new Block(statements);
    }


    private Expr expression() {
        Expr expr = or();
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

    private Expr or() {
        Expr left = and();
        while (!end() && peek().getType().equals(TokenType.OR)) {
            Token operator = pop();
            Expr right = and();
            left = new Logic(operator, left, right);
        }
        return left;
    }

    private Expr and() {
        Expr left = equality();
        while (!end() && peek().getType().equals(TokenType.AND)) {
            Token operator = pop();
            Expr right = equality();
            left = new Logic(operator, left, right);
        }
        return left;
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
        throw new RuntimeException("Unexpected symbol at " + pop.getLine() + "|" + pop.getPos());
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
