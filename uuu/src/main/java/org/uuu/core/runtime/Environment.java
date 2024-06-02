package org.uuu.core.runtime;

import org.uuu.core.scanner.Token;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    private final Map<String, Object> variables = new HashMap<>();

    public void assign(Token name, Object val) {
        if (!variables.containsKey(name.getLexeme()))
            throw new RuntimeException("Undefined variable '" + name.getLexeme() + "'.");
        variables.put(name.getLexeme(), val);
    }

    public void define(Token name, Object val) {
        variables.put(name.getLexeme(), val);
    }

    public Object get(Token token) {
        if (variables.containsKey(token.getLexeme())) return variables.get(token.getLexeme());
        throw new RuntimeException("Undefined variable '" + token.getLexeme() + "'.");
    }
}
