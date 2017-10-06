package com.simiyutin.javaii;

import java.util.function.Function;

public class LightFuture <T> {

    private final ThreadPoolImpl.LightFutureCore<T> core;

    LightFuture(ThreadPoolImpl.LightFutureCore<T> core) {
        this.core = core;
    }

    boolean isReady() {
        return core.isReady();
    }

    T get() throws LightExecutionException {
        return core.get();
    }

    <R> LightFuture<R> thenApply(Function<T, R> func) {
        return core.thenApply(func);
    }
}
