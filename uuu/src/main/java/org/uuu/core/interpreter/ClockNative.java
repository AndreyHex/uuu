package org.uuu.core.interpreter;

import java.util.List;

public class ClockNative implements Callable {
    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        return System.currentTimeMillis();
    }

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public String toString() {
        return "<native fn: clock>";
    }
}
