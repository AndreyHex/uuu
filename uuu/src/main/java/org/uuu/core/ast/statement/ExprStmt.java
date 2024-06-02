package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.Expr;

@Data
@RequiredArgsConstructor
public class ExprStmt extends Stmt {
    private final Expr expression;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
