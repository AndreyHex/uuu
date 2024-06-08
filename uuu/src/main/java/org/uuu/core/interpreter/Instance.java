package org.uuu.core.interpreter;

import lombok.RequiredArgsConstructor;
import org.uuu.core.scanner.Token;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class Instance {
    private final Class aClass;

    private final Map<String, Object> fields = new HashMap<>();

    public Object get(Token field) {
        if (fields.containsKey(field.getLexeme())) return fields.get(field.getLexeme());

        Function method = aClass.findMethod(field);
        if (method != null) return method.bind(this);

        throw new RuntimeException("Undefined property '%s'.".formatted(field.getLexeme()));
    }

    @Override
    public String toString() {
        return "<instance of: %s>".formatted(aClass.getName().getLexeme());
    }

    public void set(Token name, Object val) {
        fields.put(name.getLexeme(), val);
    }
}
