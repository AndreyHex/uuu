package org.uuu.core.interpreter;

import org.uuu.core.ast.statement.Fn;
import org.uuu.core.runtime.Environment;

import java.util.List;

public interface Callable {
    Object call(Interpreter interpreter, List<Object> args);

    int arity();

    public static Function function(Fn declaration, Environment closure) {
        if (declaration.getLexeme().equals("init")) return new InitFunction(declaration, closure);
        else return new Function(declaration, closure);
    }
}
