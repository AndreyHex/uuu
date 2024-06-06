package org.uuu.core.runtime;

import lombok.RequiredArgsConstructor;
import org.uuu.core.interpreter.Function;
import org.uuu.core.scanner.Token;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Environment {

    private final Environment enclosing;
    private final Map<String, Object> variables = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public void define(Function fn) {
        define(fn.getDeclaration().getName(), fn);
    }

    public void define(String name, Object val) {
        variables.put(name, val);
    }

    public void define(Token name, Object val) {
        variables.put(name.getLexeme(), val);
    }

    public Object get(Token token) {
        if (variables.containsKey(token.getLexeme())) return variables.get(token.getLexeme());
        if (enclosing != null) return enclosing.get(token);
        throw new RuntimeException("Undefined variable '" + token.getLexeme() + "'.");
    }

    public Object get(Token token, int d) {
        if (d == 0 && !variables.containsKey(token.getLexeme())) return null;
        else if (d == 0) return variables.get(token.getLexeme());
        if (enclosing == null) return null;
        return enclosing.get(token, d - 1);
    }

    public void assign(Token name, Object value, Integer d) {
        if (d == 0 && !variables.containsKey(name.getLexeme()))
            throw new RuntimeException("Undefined variable '" + name.getLexeme() + "'.");
        else if (d == 0) variables.put(name.getLexeme(), value);
        else if (enclosing == null) throw new RuntimeException("idk");
        else enclosing.assign(name, value, d - 1);
    }
}
