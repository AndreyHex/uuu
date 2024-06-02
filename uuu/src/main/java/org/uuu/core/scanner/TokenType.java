package org.uuu.core.scanner;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TokenType {
    LEFT_PAREN,
    RIGHT_PARENT,

    LEFT_BRACE,
    RIGHT_BRACE,

    COMMA, DOT,
    MINUS, PLUS,
    SEMICOLON, SLASH, STAR,

    QUESTION, COLON,
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    IDENTIFIER, STRING, NUMBER,

    TRUE, FALSE,
    AND, OR,
    IF, ELSE,
    FOR, WHILE,
    VAR, FN,
    CLASS, SUPER, SELF,

    RETURN,
    CONTINUE,
    BREAK,
    SWITCH,

    PRINT,

    NULL, EOF,

}
