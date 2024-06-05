package org.uuu.core.interpreter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.statement.Fn;
import org.uuu.core.runtime.Environment;
import org.uuu.core.runtime.ReturnVal;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Function implements Callable {
    private final Fn declaration;

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Environment env = new Environment(interpreter.env);
        for (int i = 0; i < args.size(); i++) env.define(declaration.getParams().get(i), args.get(i));
        try {
            interpreter.executeBlock(declaration.getBody(), env);
        } catch (ReturnVal e) {
            return e.getValue();
        }
        return null;
    }

    @Override
    public int arity() {
        return declaration.getParams().size();
    }

    public String toString() {
        return "<fn:" + declaration.getName().getLexeme() + ">";
    }
}
