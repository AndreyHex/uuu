package org.uuu.core.interpreter;

import org.junit.jupiter.api.Test;
import org.uuu.core.parser.Parser;
import org.uuu.core.scanner.Scanner;

import static org.junit.jupiter.api.Assertions.assertThrows;

class InterpreterTest {

    @Test
    public void testUndefinedVariable() {
        assertThrows(RuntimeException.class, () -> Interpreter.interpret(Parser.parse(Scanner.scan("a = g + 2;"))));
        assertThrows(RuntimeException.class, () -> Interpreter.interpret(Parser.parse(Scanner.scan("a = 3 + 2;"))));
    }

}