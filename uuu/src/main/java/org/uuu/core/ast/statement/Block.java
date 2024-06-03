package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Block extends Stmt {
    private final List<Stmt> statements;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
