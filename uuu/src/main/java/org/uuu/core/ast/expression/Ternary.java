package org.uuu.core.ast.expression;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;

@Data
@RequiredArgsConstructor
public class Ternary extends Expr {
    private final Expr condition;
    private final Expr onTrue;
    private final Expr onFalse;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
