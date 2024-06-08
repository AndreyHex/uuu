package org.uuu.core.interpreter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.scanner.Token;

import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class Class implements Callable {
    private final Token name;
    private final Map<String, Function> methods;

    public String toString() {
        return "<class: %s>".formatted(name.getLexeme());
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Instance instance = new Instance(this);
        Function init = findMethod("init");
        if (init != null) init.bind(instance).call(interpreter, args);
        return instance;
    }

    @Override
    public int arity() {
        Function init = findMethod("init");
        return init == null ? 0 : init.arity();
    }

    public Function findMethod(String name) {
        return methods.get(name);
    }

    public Function findMethod(Token field) {
        return findMethod(field.getLexeme());
    }
}
