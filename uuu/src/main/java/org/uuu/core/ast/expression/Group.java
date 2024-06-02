package org.uuu.core.ast.expression;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;

@Data
@RequiredArgsConstructor
public class Group extends Expr {
    private final Expr expression;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
