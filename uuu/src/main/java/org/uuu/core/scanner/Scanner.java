package org.uuu.core.scanner;

import lombok.RequiredArgsConstructor;

import java.util.*;

@RequiredArgsConstructor
public class Scanner {

    private static final Map<String, TokenType> keyWords = new HashMap<>();

    static {
        keyWords.put("var", TokenType.VAR);
        keyWords.put("fn", TokenType.FN);

        keyWords.put("while", TokenType.WHILE);
        keyWords.put("for", TokenType.FOR);

        keyWords.put("if", TokenType.IF);
        keyWords.put("else", TokenType.ELSE);

        keyWords.put("true", TokenType.TRUE);
        keyWords.put("false", TokenType.FALSE);

        keyWords.put("class", TokenType.CLASS);
        keyWords.put("super", TokenType.SUPER);
        keyWords.put("self", TokenType.SELF);

        keyWords.put("return", TokenType.RETURN);
        keyWords.put("continue", TokenType.CONTINUE);
        keyWords.put("break", TokenType.BREAK);
        keyWords.put("switch", TokenType.SWITCH);

        keyWords.put("null", TokenType.NULL);
    }


    private final String code;
    private int c = 0;
    private int line = 0;

    public List<Token> scan() {
        if (code == null || code.isEmpty()) return List.of();
        List<Token> r = new ArrayList<>();
        while (!over())
            if (current() == '/' && peek() == '/') while (!newLine()) c++;
            else if (newLine()) {
                c++;
                line++;
            } else if (isSpace()) c++;
            else {
                Optional<Token> token = trySimple()
                        .or(this::tryDigit)
                        .or(this::tryString)
                        .or(this::tryIdentifier);
                if (token.isEmpty()) System.out.println("Unexpected character %d | %c".formatted(line, c));
                else r.add(token.get());
                c++;
            }
        return r;
    }

    private Optional<Token> tryIdentifier() {
        if (!isAlpha()) return Optional.empty();
        StringBuilder identifier = new StringBuilder();
        while (!over() && (isAlpha() || isDigit())) {
            identifier.append(current());
            c++;
        }
        c--;
        String str = identifier.toString();
        if (keyWords.containsKey(str)) return Optional.of(Token.ofType(keyWords.get(str), line));
        else return Optional.of(Token.ofIdent(str, line));
    }

    private Optional<Token> trySimple() {
        return Optional.ofNullable(parse());
    }

    private Token parse() {
        return switch (current()) {
            case '(' -> Token.ofType(TokenType.LEFT_PAREN, line);
            case ')' -> Token.ofType(TokenType.RIGHT_PARENT, line);
            case '{' -> Token.ofType(TokenType.LEFT_BRACE, line);
            case '}' -> Token.ofType(TokenType.RIGHT_BRACE, line);
            case ',' -> Token.ofType(TokenType.COMMA, line);
            case '.' -> Token.ofType(TokenType.DOT, line);
            case '-' -> Token.ofType(TokenType.MINUS, line);
            case '+' -> Token.ofType(TokenType.PLUS, line);
            case ';' -> Token.ofType(TokenType.SEMICOLON, line);
            case '*' -> Token.ofType(TokenType.STAR, line);
            case '/' -> Token.ofType(TokenType.SLASH, line);
            case '&' -> Token.ofType(TokenType.AND, line);
            case '|' -> Token.ofType(TokenType.OR, line);
            case '?' -> Token.ofType(TokenType.QUESTION, line);
            case ':' -> Token.ofType(TokenType.COLON, line);
            case '=' -> {
                if (peek() == '=') {
                    c++;
                    yield Token.ofType(TokenType.EQUAL_EQUAL, line);
                } else yield Token.ofType(TokenType.EQUAL, line);
            }
            case '<' -> {
                if (peek() == '=') {
                    c++;
                    yield Token.ofType(TokenType.LESS_EQUAL, line);
                } else yield Token.ofType(TokenType.LESS, line);
            }
            case '>' -> {
                if (peek() == '=') {
                    c++;
                    yield Token.ofType(TokenType.GREATER_EQUAL, line);
                } else yield Token.ofType(TokenType.GREATER, line);
            }
            case '!' -> {
                if (peek() == '=') {
                    c++;
                    yield Token.ofType(TokenType.BANG_EQUAL, line);
                } else yield Token.ofType(TokenType.BANG, line);
            }
            default -> null;
        };
    }

    private boolean isAlpha() {
        char cur = current();
        return (cur >= 'a' && cur <= 'z') ||
                (cur >= 'A' && cur <= 'Z') ||
                cur == '_';
    }

    private Optional<Token> tryDigit() {
        if (isDigit()) return Optional.of(parseDigit());
        else return Optional.empty();

    }

    private Token parseDigit() {
        StringBuilder digit = new StringBuilder();
        while (!over() && (isDigit() || (current() == '.' && isDigit(peek())))) {
            digit.append(current());
            c++;
        }
        c--;
        return Token.ofNumber(Double.parseDouble(digit.toString()), line);

    }

    private Optional<Token> tryString() {
        if (current() == '"') return Optional.of(parseStr());
        else return Optional.empty();
    }

    private Token parseStr() {
        StringBuilder string = new StringBuilder();
        if (current() == '"') c++; //
        char p = current();
        while (current() != '"' || p == '\\') {
            p = current();
            string.append(current());
            c++;
        }
        return Token.ofStr(string.toString(), line);
    }

    private boolean isDigit() {
        return isDigit(current());
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean newLine() {
        return current() == '\n';
    }

    private boolean isSpace() {
        char cur = current();
        return cur == ' ' || cur == '\t' || cur == '\r';
    }

    private boolean over() {
        return c >= code.length();
    }

    private char peek() {
        if (!hasNext()) return '\0';
        return code.charAt(c + 1);
    }

    private char current() {
        return code.charAt(c);
    }

    private boolean hasNext() {
        return c < code.length() - 1;
    }

}
