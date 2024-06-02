package org.uuu.core.ast.statement;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.uuu.core.ast.Visitor;
import org.uuu.core.ast.expression.Expr;
import org.uuu.core.scanner.Token;

@Data
@AllArgsConstructor
public class Var extends Stmt {
    private final Token name;
    private final Expr initializer;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
