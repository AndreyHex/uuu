package org.uuu.core.ast;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.expression.Expr;
import org.uuu.core.scanner.Token;

@Data
@RequiredArgsConstructor
public class Assign extends Expr {
    private final Token name;
    private final Expr value;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
