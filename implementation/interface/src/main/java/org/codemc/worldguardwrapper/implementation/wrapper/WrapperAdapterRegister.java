package org.codemc.worldguardwrapper.implementation.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WrapperAdapterRegister<W, U> {

    private Map<Class<?>, WrapperAdapter<W, U>> wrappedToAdapter = new HashMap<>();
    private Map<Class<?>, WrapperAdapter<W, U>> unwrappedToAdapter = new HashMap<>();

    public void register(WrapperAdapter<W, U> adapter) {
        wrappedToAdapter.put(adapter.getWrappedFlag(), adapter);
        unwrappedToAdapter.put(adapter.getUnwrappedFlag(), adapter);
    }

    @SuppressWarnings("unchecked")
    public Optional<WrapperAdapter<W, U>> fromWrapped(W wrapped) {
        return Optional.ofNullable(wrappedToAdapter.get(wrapped.getClass()));
    }

    @SuppressWarnings("unchecked")
    public Optional<WrapperAdapter<W, U>> fromUnwrapped(U unwrapped) {
        return Optional.ofNullable(unwrappedToAdapter.get(unwrapped.getClass()));
    }

    public Optional<U> unwrap(W wrapped) {
        return fromWrapped(wrapped).map(adapter -> adapter.unwrap(wrapped));
    }

    public Optional<W> wrap(U unwrapped) {
        return fromUnwrapped(unwrapped).map(adapter -> adapter.wrap(unwrapped));
    }
}
