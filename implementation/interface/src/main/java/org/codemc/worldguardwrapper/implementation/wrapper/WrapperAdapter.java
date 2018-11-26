package org.codemc.worldguardwrapper.implementation.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

@AllArgsConstructor
public class WrapperAdapter<W, U> {

    @Getter
    private Class<W> wrappedFlag;
    @Getter
    private Class<U> unwrappedFlag;
    private Function<U, W> wrapper;
    private Function<W, U> unwrapper;

    public W wrap(U flag) {
        return wrapper.apply(flag);
    }

    public U unwrap(W flag) {
        return unwrapper.apply(flag);
    }
}
