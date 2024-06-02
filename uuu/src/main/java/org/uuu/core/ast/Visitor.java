package org.uuu.core.ast;

import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.ExprStmt;
import org.uuu.core.ast.statement.Var;

public interface Visitor<T> {
    T accept(Assign assign);

    T accept(Binary binary);

    T accept(Call call);

    T accept(Literal literal);

    T accept(Unary unary);

    T accept(Ternary ternary);

    T accept(Group group);

    T accept(ExprStmt exprStmt);

    T accept(Var var);

    T accept(Variable variable);
}
