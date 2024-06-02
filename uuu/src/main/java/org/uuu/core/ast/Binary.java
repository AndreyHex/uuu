package org.uuu.core.ast;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.scanner.Token;

@Data
@RequiredArgsConstructor
public class Binary extends Expr {
    private final Token operator;
    private final Expr left;
    private final Expr right;

    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
