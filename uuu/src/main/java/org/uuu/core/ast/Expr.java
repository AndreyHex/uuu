package org.uuu.core.ast;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Expr {
    public abstract <T> T accept(Visitor<T> visitor);
}
