package org.uuu.core.interpreter;

import org.junit.jupiter.api.Disabled;
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
        assertThrows(RuntimeException.class, () -> run("a = g + 2;"));
        assertThrows(RuntimeException.class, () -> run("a = 3 + 2;"));
    }

    @Test
    public void testDoubleDeclaration() {
        assertThrows(RuntimeException.class, () -> run("var a = 1; var a = 69;"));
    }

    @Test
    public void testInvalidCode() {
        assertThrows(RuntimeException.class, () -> run("var a = 2; return a;"));
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

    @Test
    public void testLoopContinue() {
        String code = """
                      var a = 0;
                      for(var i = 10; i > 0;i = i - 1) {
                        if(i == 2) continue;
                        if(i == 6) continue;
                        a = a + 1;
                      }
                      """;
        Environment env = run(code);
        assertEquals(8d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testLoopBreak() {
        String code = """
                      var a = 0;
                      for(var i = 10; i > 0;i = i - 1) {
                        if(i == 5) break;
                        a = a + 1;
                      }
                      """;
        Environment env = run(code);
        assertEquals(5d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testNestingLoops() {
        String code = """
                      var a = 0;
                      for(var i = 10; i > 0;i = i - 1) {
                        var b = 0;
                        while(b < 10) {
                          if(b == 5) break;
                          a = a + 2;
                          b = b + 1;
                        }
                        a = a + 1;
                      }
                      """;
        Environment env = run(code);
        assertEquals(110d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testFunction() {
        String code = """
                      var a = 0;
                      fn increment(b) {
                        a = a + b;
                      }
                      increment(1);
                      increment(-2);
                      increment(10);
                      increment(10);
                      """;
        Environment env = run(code);
        assertEquals(19d, env.get(Token.ofIdent("a", 1, 1)));
    }

    @Test
    public void testFunctionWithReturn() {
        String code = """
                      var a = 9;
                      var b = 60;
                      fn sum(a, b) {
                        return a + b;
                      }
                      var x = sum(a,b);
                      """;
        Environment env = run(code);
        assertEquals(69d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Disabled("not supported")
    @Test
    public void testFunctionCallBeforeDeclaration() {
        String code = """
                      var x = test();
                      fn test() {
                        return 69;
                      }
                      """;
        Environment env = run(code);
        assertEquals(69d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testNestedFunction() {
        String code = """
                      fn test() {
                        var i = 0;
                        fn add() {
                          i = i + 1;
                          return i;
                        }
                        return add;
                      }
                      var f = test();
                      f();
                      f();
                      var x = f();
                      """;
        Environment env = run(code);
        assertEquals(3d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testRecursiveFibonacci() {
        String code = """
                      fn fib(n) {
                        if(n <= 1) return n;
                        return fib(n-1) + fib(n-2);
                      }
                      var x = fib(10);
                      """;
        Environment env = run(code);
        assertEquals(55d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testCurrying() {
        String code = """
                      fn sum(a,b) { return a + b; }
                      fn curry(a) {
                          fn s(b) { return sum(a,b); }
                          return s;
                      }
                      var a = 36;
                      var b = 33;
                      var x = curry(a)(b);
                      """;
        Environment env = run(code);
        assertEquals(69d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testScopes() {
        String code = """
                      var a = 1;
                      var x;
                      {
                        fn test() {
                          return a;
                        }
                        var a = 69;
                        x = test();
                      }
                      """;
        Environment env = run(code);
        assertEquals(1d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testClasses() {
        String code = """
                      class Test {
                        fn test() {
                          return self.test_two();
                        }
                        fn test_two() {
                          return 33;
                        }
                      }
                      var t = Test();
                      var x = t.test();
                      """;
        Environment env = run(code);
        assertEquals(33d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testClassInitArgs() {
        String code = """
                      class Test {
                        fn init(a,b) {
                          self.a = a;
                          self.b = b;
                        }
                        fn getA() {
                          return self.a;
                        }
                        fn getB() {
                          return self.b;
                        }
                      }
                      var t = Test(33, 69);
                      var x = t.getA();
                      var xx = t.getB();
                      """;
        Environment env = run(code);
        assertEquals(33d, env.get(Token.ofIdent("x", 1, 1)));
        assertEquals(69d, env.get(Token.ofIdent("xx", 1, 1)));
    }

    @Test
    public void testClassInit() {
        String code = """
                      class Test {
                        fn init() {
                          self.a = 69;
                        }
                        fn get() {
                          return self.a;
                        }
                      }
                      var t = Test();
                      var x = t.get();
                      var xx = t.a;
                      """;
        Environment env = run(code);
        assertEquals(69d, env.get(Token.ofIdent("x", 1, 1)));
        assertEquals(69d, env.get(Token.ofIdent("xx", 1, 1)));
    }

    @Test
    public void testClassInheritance() {
        String code = """
                      class A {
                        fn test() {
                          return 69;
                        }
                      }
                      class B < A {}
                      var t = A();
                      var x = t.test();
                      """;
        Environment env = run(code);
        assertEquals(69d, env.get(Token.ofIdent("x", 1, 1)));
    }

    @Test
    public void testClassSuper() {
        String code = """
                      class A {
                        fn test() {
                          return 69;
                        }
                      }
                      class B < A {
                       fn test() {
                         return 42;
                       }
                       fn get() {
                         return super.test();
                       }
                      }
                      var a = A();
                      var b = B();
                      var x = b.get();
                      var xx = b.test();
                      """;
        Environment env = run(code);
        assertEquals(69d, env.get(Token.ofIdent("x", 1, 1)));
        assertEquals(42d, env.get(Token.ofIdent("xx", 1, 1)));
    }

    private static Environment run(String code) {
        return new Interpreter(Parser.parse(Scanner.scan(code))).interpret().env;
    }
}