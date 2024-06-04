package org.uuu.core.interpreter;

import org.junit.jupiter.api.Test;
import org.uuu.core.parser.Parser;
import org.uuu.core.runtime.Environment;
import org.uuu.core.scanner.Scanner;
import org.uuu.core.scanner.Token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterTest {

    @Test
    public void testUndefinedVariable() {
        assertThrows(RuntimeException.class, () -> Interpreter.interpret(Parser.parse(Scanner.scan("a = g + 2;"))));
        assertThrows(RuntimeException.class, () -> Interpreter.interpret(Parser.parse(Scanner.scan("a = 3 + 2;"))));
    }

    @Test
    public void testAssigment() {
        Environment env = run("var a = 21;");
        assertEquals(21d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testReAssigment() {
        Environment env = run("var a = 2123; a = 69;");
        assertEquals(69d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testVarDeclarationWithoutInitialValue() {
        Environment env = run("var a; a = 69;");
        assertEquals(69d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testMathPrecedence() {
        Environment env = run("var a = 2 + 2 * 2;");
        assertEquals(6d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testIf() {
        Environment env = run("var a; if( 2 > 1) a = 32; else a = 99;");
        assertEquals(32d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testElse() {
        Environment env = run("var a; if( 2 == 1) a = 32; else a = 99;");
        assertEquals(99d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testOr() {
        Environment env = run("var a = false | !false;");
        assertEquals(true, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testAnd() {
        Environment env = run("var a = true & 2 > 1 & !false;");
        assertEquals(true, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testLogicPrecedence() {
        Environment env = run("var a = true | false & false;");
        assertEquals(true, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testWhile() {
        Environment env = run("var a = 1; while(a<5) a = a + 1;");
        assertEquals(5d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testFor() {
        Environment env = run("var a = 0; for(var i = 0; i<5;i=i+1) a = a + 1;");
        assertEquals(5d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testFibonacci() {
        String code = """
                var a = 0;
                var b = 1;
                for(var i = 10; i > 0;i = i - 1) {
                    var tmp = a + b;
                    a = b;
                    b = tmp;
                }
                """;
        Environment env = run(code);
        assertEquals(55d, env.get(Token.ofIdent("a", 1, 1)));
    }

    private static Environment run(String code) {
        return new Interpreter(Parser.parse(Scanner.scan(code))).interpret().env;
    }
}