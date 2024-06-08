package org.uuu.core.scanner;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Token {
    private final TokenType type;
    private final String lexeme; // name?
    private final Object literal; // value of literal?
    private final int line;
    private final int pos;

    public static Token ofType(TokenType type, int line, int pos) {
        return new Token(type, type.name().toLowerCase(), null, line, pos);
    }

    public static Token ofNumber(double value, int line, int pos) {
        return new Token(TokenType.NUMBER, "", value, line, pos);
    }

    public static Token ofStr(String value, int line, int pos) {
        return new Token(TokenType.STRING, "", value, line, pos);
    }

    public static Token ofIdent(String lexeme, int line, int pos) {
        return new Token(TokenType.IDENTIFIER, lexeme, null, line, pos);
    }

    @Override
    public String toString() {
        return "[type:%s|lexeme:%s|literal:%s|line:%d]".formatted(type, lexeme, literal, line);
    }
}
