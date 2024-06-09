package org.uuu.core.interpreter;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.statement.Fn;
import org.uuu.core.runtime.Environment;
import org.uuu.core.runtime.ReturnVal;
import org.uuu.core.scanner.Token;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Function implements Callable {
    private final Fn declaration;
    private final Environment closure;

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Environment env = new Environment(closure);
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

    public Function bind(Instance instance) {
        Environment env = new Environment(closure);
        env.define(Token.ofIdent("self", 0, 0), instance);
        return new Function(declaration, env);
    }
}
