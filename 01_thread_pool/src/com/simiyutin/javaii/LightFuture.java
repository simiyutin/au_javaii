package com.simiyutin.javaii;

import java.util.function.Function;

public class LightFuture <T> {

    private final ThreadPoolImpl.LightFutureCore core;

    LightFuture(ThreadPoolImpl.LightFutureCore core) {
        this.core = core;
    }

    boolean isReady() {
        return core.isReady();
    };

    T get() throws LightExecutionException {
        return (T) core.get();
    };

    <R> LightFuture<R> thenApply(Function<T, R> func) {
        return core.thenApply(func);
    };
}
