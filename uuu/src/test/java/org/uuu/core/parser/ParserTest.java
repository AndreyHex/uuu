package org.uuu.core.parser;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.uuu.core.ast.Expr;
import org.uuu.core.scanner.Scanner;
import org.uuu.core.util.AstPrinter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

    private static final Expression[] EXPRESSION_TESTS = new Expression[]{
            new Expression("true+!true", "(+ true (! true))"),
            new Expression("!true?-!true:1-1", "((! true) ? (- (! true)) : (- 1.0 1.0))"),
            new Expression("!true?1>2?2:false:1-1", "((! true) ? ((> 1.0 2.0) ? 2.0 : false) : (- 1.0 1.0))"),
            new Expression("(2+3)/3", "(/ (group (+ 2.0 3.0)) 3.0)"),
            new Expression("(-2-2-2)==3", "(== (group (- (- (- 2.0) 2.0) 2.0)) 3.0)"),
            new Expression("!-true ==4/(3-2)", "(== (! (- true)) (/ 4.0 (group (- 3.0 2.0))))"),
    };

    @ParameterizedTest(name = "{index}:{0}")
    @ArgumentsSource(ExpressionsSource.class)
    public void expressions(String code, String expected) {
        Expr res = new Parser(new Scanner(code).scan()).test();
        assertEquals(expected, AstPrinter.print(res));
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