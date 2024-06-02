package org.uuu.core.ast;

public interface Visitor<T> {
    T accept(Assign assign);

    T accept(Binary binary);

    T accept(Call call);

    T accept(Literal literal);

    T accept(Unary unary);

    T accept(Ternary ternary);

    T accept(Group group);
}
