package org.uuu.core.ast.expression;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.scanner.Token;

@Data
@RequiredArgsConstructor
public class Get extends Expr {
    private final Token name;
    private final Expr object;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
