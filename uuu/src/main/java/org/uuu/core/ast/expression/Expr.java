package org.uuu.core.ast.expression;

import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;

@RequiredArgsConstructor
public abstract class Expr {
    public abstract <T> T accept(Visitor<T> visitor);
}
