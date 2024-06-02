package org.uuu.core.ast;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Literal extends Expr {
    private final Object value;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
