package com.simiyutin.javaii;

import java.util.function.Supplier;

public interface ThreadPool {
    <T> LightFuture<T> feed(Supplier<? extends T> task);
    void shutdown();
}
