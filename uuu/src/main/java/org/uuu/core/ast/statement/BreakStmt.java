package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.scanner.Token;

@Data
@RequiredArgsConstructor
public class BreakStmt extends Stmt {
    private final Token keyword;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
