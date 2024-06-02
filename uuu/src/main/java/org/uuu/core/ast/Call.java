package org.uuu.core.ast;

import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.expression.Expr;
import org.uuu.core.scanner.Token;

import java.util.List;

@RequiredArgsConstructor
public class Call extends Expr {
    private final Token parent;
    private final Expr callee;
    private final List<Expr> args;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
