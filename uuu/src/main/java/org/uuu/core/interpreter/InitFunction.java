package org.uuu.core.interpreter;

import org.uuu.core.ast.statement.Fn;
import org.uuu.core.runtime.Environment;

import java.util.List;

public class InitFunction extends Function {
    public InitFunction(Fn declaration, Environment closure) {
        super(declaration, closure);
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        super.call(interpreter, args);
        return getClosure().get("self", 0);
    }
}
