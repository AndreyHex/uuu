package org.uuu.core.ast.statement;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.uuu.core.ast.Visitor;
import org.uuu.core.scanner.Token;

import java.util.List;

@Data
@RequiredArgsConstructor
public class Fn extends Stmt {
    private final Token name;
    private final List<Token> params;
    private final List<Stmt> body;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.accept(this);
    }
}
