package com.simiyutin.javaii;

import java.util.function.Supplier;

public interface ThreadPool {
    <T> LightFuture<T> feed(Supplier<T> task);
    void shutdown();
}
