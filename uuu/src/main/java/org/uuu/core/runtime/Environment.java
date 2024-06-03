package org.uuu.core.runtime;

import lombok.RequiredArgsConstructor;
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

    public void assign(Token name, Object val) {
        if (variables.containsKey(name.getLexeme())) {
            variables.put(name.getLexeme(), val);
            return;
        } else if (enclosing != null) {
            enclosing.assign(name, val);
            return;
        }
        throw new RuntimeException("Undefined variable '" + name.getLexeme() + "'.");
    }

    public void define(Token name, Object val) {
        variables.put(name.getLexeme(), val);
    }

    public Object get(Token token) {
        if (variables.containsKey(token.getLexeme())) return variables.get(token.getLexeme());
        if (enclosing != null) return enclosing.get(token);
        throw new RuntimeException("Undefined variable '" + token.getLexeme() + "'.");
    }
}
