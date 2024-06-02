package org.uuu.core.ast.expression;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.uuu.core.ast.Visitor;
import org.uuu.core.scanner.Token;

@Data
@AllArgsConstructor
public class Variable extends Expr {
    private final Token name;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
