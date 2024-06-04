package org.uuu.core.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.uuu.core.ast.expression.Assign;
import org.uuu.core.ast.statement.*;
import org.uuu.core.scanner.Scanner;
import org.uuu.core.util.AstPrinter;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    private static final TestCase[] TEST_CASES = new TestCase[]{
            new TestCase("true+!true;", "(+ true (! true));"),
            new TestCase("!true?-!true:1-1;", "((! true) ? (- (! true)) : (- 1.0 1.0));"),
            new TestCase("!true?1>2?2:false:1-1;", "((! true) ? ((> 1.0 2.0) ? 2.0 : false) : (- 1.0 1.0));"),
            new TestCase("(2+3)/3;", "(/ (group (+ 2.0 3.0)) 3.0);"),
            new TestCase("(-2-2-2)==3;", "(== (group (- (- (- 2.0) 2.0) 2.0)) 3.0);"),
            new TestCase("var test = 4;", "var test = 4.0;"),
            new TestCase("var d_d = 4 + 2;", "var d_d = (+ 4.0 2.0);"),
            new TestCase("var d = true | false;", "var d = (| true false);"),
            new TestCase("var d = true & false;", "var d = (& true false);"),
            new TestCase("var d = true & ( false | false );", "var d = (& true (group (| false false)));"),
    };

    @ParameterizedTest(name = "{index}:{0}")
    @ArgumentsSource(TestCasesSource.class)
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
    public void testIfElse() {
        List<Stmt> parse = Parser.parse(Scanner.scan("if(true) a = 2; else a = 3;"));
        assertInstanceOf(If.class, parse.get(0));
    }

    @Test
    public void testBlock() {
        List<Stmt> parse = Parser.parse(Scanner.scan("a = 2 + 2; { var a = 23; }"));
        assertInstanceOf(ExprStmt.class, parse.get(0));
        assertInstanceOf(Block.class, parse.get(1));
    }

    @Test
    public void testNestedBlocks() {
        List<Stmt> parse = Parser.parse(Scanner.scan("a = 2 + 2; { var a = 23; {} a = b; { var g; { var j; }}}"));
        assertInstanceOf(ExprStmt.class, parse.get(0));
        assertInstanceOf(Block.class, parse.get(1));
        List<Stmt> statements = ((Block) parse.get(1)).getStatements();
        assertInstanceOf(Var.class, statements.get(0));
        assertInstanceOf(Block.class, statements.get(1));
        assertInstanceOf(ExprStmt.class, statements.get(2));
        assertInstanceOf(Block.class, statements.get(3));
        List<Stmt> inner = ((Block) statements.get(3)).getStatements();
        assertInstanceOf(Var.class, inner.get(0));
        assertInstanceOf(Block.class, inner.get(1));
    }

    @Test
    public void testBlockError() {
        assertDoesNotThrow(() -> Parser.parse(Scanner.scan("{var a=2;{a=4;}{}{{}} }")));
        assertThrows(RuntimeException.class, () -> Parser.parse(Scanner.scan("{var a=2;{a=4;}{}{{}} } }")));
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

    private static class TestCasesSource implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
            return Stream.of(TEST_CASES).map(TestCase::toArguments);
        }
    }

    private record TestCase(String code, String expected) {
        public Arguments toArguments() {
            return Arguments.of(code, expected);
        }
    }
}