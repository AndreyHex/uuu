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

    public static Token ofType(TokenType type, int line) {
        return new Token(type, "", null, line);
    }

    public static Token ofNumber(double value, int line) {
        return new Token(TokenType.NUMBER, "", value, line);
    }

    public static Token ofStr(String value, int line) {
        return new Token(TokenType.STRING, "", value, line);
    }

    public static Token ofIdent(String lexeme, int line) {
        return new Token(TokenType.IDENTIFIER, lexeme, null, line);
    }

    @Override
    public String toString() {
        return "[type:%s|lexeme:%s|literal:%s|line:%d]".formatted(type, lexeme, literal, line);
    }
}
