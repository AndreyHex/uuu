package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.Expr;

@Data
@RequiredArgsConstructor
public class If extends Stmt {
    private final Expr condition;
    private final Stmt onTrue;
    private final Stmt onFalse;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
