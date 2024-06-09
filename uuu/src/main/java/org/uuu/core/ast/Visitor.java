package org.uuu.core.ast;

import org.uuu.core.ast.expression.*;
import org.uuu.core.ast.statement.*;

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

    T accept(Block block);

    T accept(If anIf);

    T accept(Logic logic);

    T accept(While aWhile);

    T accept(Fn fn);

    T accept(Return aReturn);

    T accept(ClassStmt aClass);

    T accept(Get get);

    T accept(Set set);

    T accept(Self self);

    T accept(Super aSuper);

    T accept(BreakStmt breakStmt);

    T accept(ContinueStmt continueStmt);

    T accept(For aFor);
}
