package org.uuu.core.ast.expression;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.scanner.Token;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Call extends Expr {
    private final Token paren;
    private final Expr callee;
    private final List<Expr> args;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
