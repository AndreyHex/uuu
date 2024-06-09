package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.Expr;

@Data
@RequiredArgsConstructor
public class For extends Stmt {
    private final Stmt initializer;
    private final Expr condition;
    private final Expr increment;
    private final Stmt body;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
