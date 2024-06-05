package org.uuu.core.interpreter;

import java.util.List;

public interface Callable {
    Object call(Interpreter interpreter, List<Object> args);

    int arity();
}
