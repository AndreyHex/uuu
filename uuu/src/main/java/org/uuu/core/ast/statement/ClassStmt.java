package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.scanner.Token;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ClassStmt extends Stmt {
    private final Token name;
    private final List<Fn> methods;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
