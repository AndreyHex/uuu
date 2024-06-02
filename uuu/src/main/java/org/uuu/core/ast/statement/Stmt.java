package org.uuu.core.ast.statement;

import org.uuu.core.ast.Visitor;

public abstract class Stmt {
    public abstract <T> T accept(Visitor<T> visitor);
}
