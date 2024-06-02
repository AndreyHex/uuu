package org.uuu.core.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.uuu.core.ast.Assign;
import org.uuu.core.ast.statement.ExprStmt;
import org.uuu.core.ast.statement.Stmt;
import org.uuu.core.scanner.Scanner;
import org.uuu.core.util.AstPrinter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static final Expression[] EXPRESSION_TESTS = new Expression[]{
            new Expression("true+!true;", "(+ true (! true));"),
            new Expression("!true?-!true:1-1;", "((! true) ? (- (! true)) : (- 1.0 1.0));"),
            new Expression("!true?1>2?2:false:1-1;", "((! true) ? ((> 1.0 2.0) ? 2.0 : false) : (- 1.0 1.0));"),
            new Expression("(2+3)/3;", "(/ (group (+ 2.0 3.0)) 3.0);"),
            new Expression("(-2-2-2)==3;", "(== (group (- (- (- 2.0) 2.0) 2.0)) 3.0);"),
            new Expression("var test = 4;", "var test = 4.0;"),
            new Expression("var d_d = 4 + 2;", "var d_d = (+ 4.0 2.0);"),
    };

    @ParameterizedTest(name = "{index}:{0}")
    @ArgumentsSource(ExpressionsSource.class)
    public void singleExpressionTest(String code, String expected) {
        List<Stmt> parse = Parser.parse(new Scanner(code).scan());
        assertEquals(1, parse.size());
        assertEquals(expected, AstPrinter.print(parse.get(0)).trim());
    }

    @Test
    public void testAssigment() {
        List<Stmt> parse = Parser.parse(Scanner.scan("a = 2 + 2;"));
        assertInstanceOf(ExprStmt.class, parse.get(0));
        ExprStmt stmt = (ExprStmt) parse.get(0);
        assertInstanceOf(Assign.class, stmt.getExpression());
    }

    @Test
    public void testAssigmentError() {
        assertThrows(RuntimeException.class, () -> Parser.parse(Scanner.scan("a + 2 = 2 + 2;")));
    }

    @Test
    public void testNoSemicolonException() {
        assertThrows(RuntimeException.class, () -> Parser.parse(new Scanner("2+2;\n4*4").scan()));
        assertThrows(RuntimeException.class, () -> Parser.parse(new Scanner("var test").scan()));
        assertThrows(RuntimeException.class, () -> Parser.parse(new Scanner("var test 2 + 2").scan()));
        assertThrows(RuntimeException.class, () -> Parser.parse(new Scanner("var test = 23").scan()));
    }

    private static class ExpressionsSource implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(EXPRESSION_TESTS).map(Expression::toArguments);
        }
    }

    private record Expression(String code, String expected) {
        public Arguments toArguments() {
            return Arguments.of(code, expected);
        }
    }
}