package org.uuu.core.interpreter;

import java.util.List;

public class PrintNative implements Callable {

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        System.out.println(args.get(0));
        return null;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public String toString() {
        return "<native fn: print>";
    }
}
