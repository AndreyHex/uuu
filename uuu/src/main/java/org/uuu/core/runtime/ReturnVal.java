package org.uuu.core.runtime;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ReturnVal extends RuntimeException {
    private final Object value;
}
