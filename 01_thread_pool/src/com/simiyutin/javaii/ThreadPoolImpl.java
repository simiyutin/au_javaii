package com.simiyutin.javaii;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPoolImpl implements ThreadPool {

    private final Queue<LightFutureCore> taskQueue;
    private final List<Thread> threads;

    public ThreadPoolImpl(int nThreads) {
        this.taskQueue = new ArrayDeque<>();
        this.threads = new ArrayList<>();
        for (int i = 0; i < nThreads; i++) {
            threads.add(new Thread(new WorkerRoutine()));
            threads.get(threads.size() - 1).start();
        }
    }

    @Override
    public <T> LightFuture<T> feed(Supplier<? extends T> supplier) {

        LightFutureCore core = new LightFutureCore(supplier);

        synchronized (taskQueue) {
            taskQueue.add(core);
            taskQueue.notify();
        }

        return new LightFuture<>(core);
    }

    @Override
    public void shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private class WorkerRoutine implements Runnable {
        @Override
        public void run() {
            while (true) {
                LightFutureCore core;
                synchronized (taskQueue) {
                    while (taskQueue.size() == 0) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    core = taskQueue.poll();
                }

                synchronized (core) {
                    try {
                        core.result = core.supplier.get();
                    } catch (Exception ex) {
                        core.caughtException = true;
                    }
                    core.ready = true;
                    core.notifyAll();
                }

            }
        }
    }

    class LightFutureCore {

        final Supplier supplier;
        volatile boolean ready;
        boolean caughtException;
        Object result;

        private LightFutureCore(Supplier supplier) {
            this.supplier = supplier;
            this.result = null;
            this.ready = false;
            this.caughtException = false;
        }

        boolean isReady() {
            return ready;
        }

        synchronized Object get() throws LightExecutionException {
            while (!isReady()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    throw new LightExecutionException();
                }
            }
            if (caughtException) {
                throw new LightExecutionException();
            }
            return result;
        }

        <T, R> LightFuture<R> thenApply(Function<T, R> func) {

            Supplier<R> spl = () -> {
                T result;
                synchronized (this) {
                    while (!isReady()) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            return null;
                        }
                    }
                    result = (T) this.result;
                }

                return func.apply(result);
            };

            return feed(spl);
        };
    }
}
