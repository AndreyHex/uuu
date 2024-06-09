package org.uuu.core.parser;

import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.*;
import org.uuu.core.scanner.Token;
import org.uuu.core.scanner.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static org.uuu.core.scanner.TokenType.*;

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
        if (match(VAR)) return varDeclaration();
        if (match(FN)) return fnDeclaration();
        if (match(CLASS)) return classDeclaration();
        else return statement();
    }

    private Stmt classDeclaration() {
        pop(CLASS, "");
        Token name = pop(IDENTIFIER, "Expected class name.");
        Token superclass = null;
        if (match(LESS)) {
            pop();
            superclass = pop(IDENTIFIER, "Expected super class identifier after '<'.");
        }
        pop(LEFT_BRACE, "Expected '{' at the beginning of class body.");

        List<Fn> methods = new ArrayList<>();
        while (!end() && !peek().getType().equals(RIGHT_BRACE)) methods.add(fnDeclaration());

        pop(RIGHT_BRACE, "Expected '}' at the end of class body.");
        return new ClassStmt(name, superclass == null ? null : new Variable(superclass), methods);
    }

    private Fn fnDeclaration() {
        pop(); // pop 'fn'
        Token name = pop(IDENTIFIER, "Expected identifier after 'fn'.");
        pop(LEFT_PAREN, "Expected '(' after '" + name.getLexeme() + "' name.");
        List<Token> parameters = new ArrayList<>();
        if (!match(RIGHT_PARENT)) parameters = parameters(new ArrayList<>());
        pop(RIGHT_PARENT, "Expected ')' after function parameters.");
        pop(LEFT_BRACE, "Expected '{' before function body.");
        List<Stmt> body = stmts();
        pop(RIGHT_BRACE, "Expected '}' after function body.");
        return new Fn(name, parameters, body);
    }

    private List<Token> parameters(List<Token> params) {
        if (params.size() > 254) throw new RuntimeException("Exceeded limit of parameters (254).");
        params.add(pop());
        if (match(COMMA)) {
            pop();
            return parameters(params);
        }
        return params;
    }


    private Stmt varDeclaration() {
        pop(); // pop 'var'
        Token name = pop(IDENTIFIER, "Expected identifier after var.");
        Expr expr = null;
        if (match(EQUAL)) {
            pop();
            expr = expression();
        }
        pop(SEMICOLON, "Expected semicolon at the end of var declaration.");
        return new Var(name, expr);
    }

    private Stmt statement() {
        if (match(LEFT_BRACE)) return block();
        if (match(IF)) return ifStmt();
        if (match(WHILE)) return whileStmt();
        if (match(FOR)) return forStmt();
        if (match(RETURN)) return returnStmt();

        Stmt stmt;
        if (match(BREAK)) stmt = new BreakStmt(pop());
        else if (match(CONTINUE)) stmt = new ContinueStmt(pop());
        else stmt = new ExprStmt(exprStmt());
        pop(SEMICOLON, "Expected semicolon at the end of the statement.");
        return stmt;
    }

    private Stmt returnStmt() {
        pop(RETURN, "");
        Expr val = null;
        if (!match(SEMICOLON)) val = expression();
        pop(SEMICOLON, "Expecting ';' at the end of return statement.");
        return new Return(val);
    }

    private Expr exprStmt() {
        Expr expr = expression();
        if (match(TokenType.EQUAL)) {
            pop(); //equals
            Expr value = expression();
            if (expr instanceof Variable variable) expr = new Assign(variable.getName(), value);
            else if (expr instanceof Get get) expr = new Set(get.getName(), get.getObject(), value);
            else throw new RuntimeException("Invalid assignment.");
        }
        return expr;
    }

    private Stmt ifStmt() {
        pop(); // pop if
        pop(LEFT_PAREN, "Expected left paren.");
        Expr condition = expression();
        pop(RIGHT_PARENT, "Expected right paren.");

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
        pop(LEFT_PAREN, "Expected left paren.");
        Expr condition = expression();
        pop(RIGHT_PARENT, "Expected right paren.");
        Stmt body = statement();
        return new While(condition, body);
    }

    private Stmt forStmt() {
        pop(); // pop for
        pop(LEFT_PAREN, "Expected '(' after 'for'.");

        Stmt initializer;
        Expr condition = null;
        Expr increment = null;

        if (peek().getType().equals(TokenType.SEMICOLON)) initializer = null;
        else if (peek().getType().equals(TokenType.VAR)) initializer = varDeclaration();
        else initializer = new ExprStmt(exprStmt());

        if (!peek().getType().equals(RIGHT_PARENT)) condition = expression();
        pop(SEMICOLON, "Expected ';' after condition.");
        if (!peek().getType().equals(RIGHT_PARENT)) increment = exprStmt();
        pop(RIGHT_PARENT, "Expected ')' after 'for' clauses");
        Stmt body = statement();

        return new For(initializer, condition, increment, body);
    }

    private Stmt block() {
        pop(); // pop open brace
        List<Stmt> statements = stmts();
        pop(RIGHT_BRACE, "Unexpected symbol at the end of the block.");
        return new Block(statements);
    }

    private List<Stmt> stmts() {
        List<Stmt> statements = new ArrayList<>();
        while (!end() && !peek().getType().equals(TokenType.RIGHT_BRACE)) statements.add(declaration());
        return statements;
    }

    private Expr expression() {
        Expr expr = or();
        if (match(TokenType.QUESTION)) {
            pop();
            Expr onTrue = expression();
            pop(COLON, "Expected colon.");
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
        } else return call();
    }

    private Expr call() {
        Expr callee = primary();
        return call(callee);
    }

    private Expr call(Expr expr) {
        if (match(LEFT_PAREN)) {
            pop();
            List<Expr> arguments = new ArrayList<>();
            if (!match(RIGHT_PARENT)) arguments = arguments(arguments);
            Token paren = pop(RIGHT_PARENT, "Expected ')' after arguments.");
            return call(new Call(paren, expr, arguments));
        } else if (match(DOT)) {
            pop(DOT, "");
            Token name = pop(IDENTIFIER, "Expected identifier after '.'.");
            return call(new Get(name, expr));
        }
        return expr;
    }

    private List<Expr> arguments(List<Expr> args) {
        if (args.size() > 254) throw new RuntimeException("Exceeded limit of arguments (254).");
        args.add(expression());
        if (match(COMMA)) {
            pop();
            return arguments(args);
        }
        return args;
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

        if (pop.getType().equals(SELF)) return new Self(pop);
        if (pop.getType().equals(SUPER)) return superr(pop);
        if (pop.getType().equals(IDENTIFIER)) return new Variable(pop);

        if (pop.getType().equals(LEFT_PAREN)) {
            Expr grouped = expression();
            pop(RIGHT_PARENT, "Expected ')'");
            return new Group(grouped);
        }
        throw new RuntimeException("Unexpected symbol at " + pop.getLine() + "|" + pop.getPos());
    }

    private Expr superr(Token pop) {
        pop(DOT, "Expected '.' after 'super'.");
        Token method = pop(IDENTIFIER, "Expecting superclass method name.");
        return new Super(pop, method);
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

    private Token pop(TokenType type, String error) {
        if (!match(type)) throw new RuntimeException(error + " %d|%d".formatted(peek().getLine(), peek().getPos()));
        return pop();
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
