package org.uuu.core.ast;

import lombok.Data;
import lombok.RequiredArgsConstructor;

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
