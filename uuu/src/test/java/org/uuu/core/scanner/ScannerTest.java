package org.uuu.core.scanner;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.uuu.core.scanner.TokenType.*;

public class ScannerTest {

    private static final TestCase[] TESTS = new TestCase[]{
            TestCase.code("tru true true true  ").expected(IDENTIFIER, TRUE, TRUE, TRUE),
            TestCase.code("!false").expected(BANG, FALSE),
            TestCase.code("tru false1 ! != true  ").expected(IDENTIFIER, IDENTIFIER, BANG, BANG_EQUAL, TRUE),
            TestCase.code("  \n\n&\n == = \ttrue  ").expected(AND, EQUAL_EQUAL, EQUAL, TRUE),
            TestCase.code("tru ((( !! ").expected(IDENTIFIER, LEFT_PAREN, LEFT_PAREN, LEFT_PAREN, BANG, BANG),
            TestCase.code("t\nr\tu ").expected(IDENTIFIER, IDENTIFIER, IDENTIFIER),
            TestCase.code("//t\nr\tu ").expected(IDENTIFIER, IDENTIFIER),
            TestCase.code(" !switch  \"switch\\\"\" ").expected(BANG, SWITCH, STRING),
            TestCase.code(" \"strin//tri\" return").expected(STRING, RETURN),
            TestCase.code("===+").expected(EQUAL_EQUAL, EQUAL, PLUS),
            TestCase.code("returning !switching").expected(IDENTIFIER, BANG, IDENTIFIER),
            TestCase.code("while(true){}").expected(WHILE, LEFT_PAREN, TRUE, RIGHT_PARENT, LEFT_BRACE, RIGHT_BRACE),
            TestCase.code("if(!variable)\nreturn;").expected(IF, LEFT_PAREN, BANG, IDENTIFIER, RIGHT_PARENT, RETURN, SEMICOLON),
            TestCase.code("var r = 231.23").expected(VAR, IDENTIFIER, EQUAL, NUMBER),
            TestCase.code("<==>=> =<").expected(LESS_EQUAL, EQUAL, GREATER_EQUAL, GREATER, EQUAL, LESS),
            TestCase.code("cla\nss").expected(IDENTIFIER, IDENTIFIER),
            TestCase.code("sup\tper").expected(IDENTIFIER, IDENTIFIER),
            TestCase.code("var num_314").expected(VAR, IDENTIFIER),
            TestCase.code("var some_var_ = 1232.23").expected(VAR, IDENTIFIER, EQUAL, NUMBER),
            TestCase.code("fn f_666() return null;").expected(FN, IDENTIFIER, LEFT_PAREN, RIGHT_PARENT, RETURN, NULL, SEMICOLON),
            TestCase.code("self continue").expected(SELF, CONTINUE),
            TestCase.code(" break;").expected(BREAK, SEMICOLON),
            TestCase.code("super.get class").expected(SUPER, DOT, IDENTIFIER, CLASS),
            TestCase.code(" 3+4\n -2 \n*8,").expected(NUMBER, PLUS, NUMBER, MINUS, NUMBER, STAR, NUMBER, COMMA),
            TestCase.code(" i == null ? true : -1").expected(IDENTIFIER, EQUAL_EQUAL, NULL, QUESTION, TRUE, COLON, MINUS, NUMBER),
            TestCase.code("(2+3)/43").expected(LEFT_PAREN, NUMBER, PLUS, NUMBER, RIGHT_PARENT, SLASH, NUMBER),


//            TestCase.code("var r=.233").expected(VAR, IDENTIFIER, EQUAL, NUMBER), //TODO make it valid number ?
//            TestCase.code("var r = 231.").expected(VAR, IDENTIFIER, EQUAL, NUMBER),

    };

    @ParameterizedTest(name = "{index}:{0}")
    @ArgumentsSource(Source.class)
    public void singleTokenTest(String code, List<TokenType> expected) {
        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scan();
        assertEquals(expected, tokens.stream().map(Token::getType).toList());
    }


    static class Source implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(TESTS).map(TestCase::toArgs);
        }
    }

    private record TestCase(String code, TokenType... expected) {
        public static TestCase code(String code) {
            return new TestCase(code);
        }

        public TestCase expected(TokenType... expected) {
            return new TestCase(code, expected);
        }

        public Arguments toArgs() {
            return Arguments.of(code, List.of(expected));
        }
    }

}