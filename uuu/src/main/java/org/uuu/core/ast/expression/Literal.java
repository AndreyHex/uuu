package org.uuu.core.ast.expression;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;

@Data
@RequiredArgsConstructor
public class Literal extends Expr {
    private final Object value;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
