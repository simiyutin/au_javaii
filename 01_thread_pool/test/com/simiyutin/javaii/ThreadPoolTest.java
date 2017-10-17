package com.simiyutin.javaii;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.Assert.*;

public class ThreadPoolTest {

    private final Integer ONE = 1;
    private final Integer TWO = 2;
    private final Supplier<Integer> waitAndReturnOne = () -> {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1;
    };

    private static class FlagBox {

        boolean flag = false;
    }

    @Test
    public void testSimple() throws LightExecutionException {
        ThreadPool tp = new ThreadPoolImpl(3);
        Supplier<Integer> spl = () -> 30;
        LightFuture<Integer> lf = tp.feed(spl);
        Integer result = lf.get();
        assertEquals(result, new Integer(30));
    }

    @Test
    public void testNThreads() throws InterruptedException {
        final int n = 5;
        final ThreadPool tp = new ThreadPoolImpl(n);
        final CyclicBarrier barrier = new CyclicBarrier(n);
        final FlagBox flag = new FlagBox();

        //blocks thread on invocation
        Supplier<Integer> tsk = () -> {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                return 0;
            }
            synchronized (flag) {
                flag.flag = true;
                flag.notify();
            }

            return 1;
        };
        for (int j = 0; j < n; j++) {
            tp.feed(tsk);
        }
        synchronized (flag) {
            while (!flag.flag) {
                flag.wait();
            }
        }
        //if finishes than OK
    }

    @Test
    public void testExceptionInTask() throws Exception {
        try {
            ThreadPool tp = new ThreadPoolImpl(3);
            Supplier<Integer> spl = () -> {
                throw new RuntimeException("hello");
            };
            LightFuture<Integer> lf = tp.feed(spl);
            lf.get();
            fail("Exception must be thrown");
        } catch (LightExecutionException ex) {
            if(ex.getSuppressed().length == 0) {
                fail("Exceptioon must contain suppressed one");
            }
            assertEquals(ex.getSuppressed()[0].getClass(), RuntimeException.class);
        }

    }

    @Test
    public void testGet() throws Exception {
        ThreadPool tp = new ThreadPoolImpl(3);
        LightFuture<Integer> lf = tp.feed(waitAndReturnOne);
        assertEquals(lf.get(), ONE);
    }

    @Test
    public void testIsReady() throws Exception {
        ThreadPool tp = new ThreadPoolImpl(3);
        LightFuture<Integer> lf = tp.feed(waitAndReturnOne);
        assertFalse(lf.isReady());
        TimeUnit.SECONDS.sleep(1);
        assertFalse(lf.isReady());
        assertEquals(lf.get(), ONE);
    }

    @Test
    public void testThenApply() throws LightExecutionException {
        ThreadPool tp = new ThreadPoolImpl(3);
        Supplier<Integer> spl = () -> 30;
        LightFuture<Integer> lf = tp.feed(spl);
        Function<Integer, Integer> fn = (i) -> i * 2;
        LightFuture<Integer> lf2 = lf.thenApply(fn);
        assertEquals(lf2.get(), new Integer(60));
    }

    @Test
    public void testThenApplyOrder() throws Exception {
        ThreadPool tp = new ThreadPoolImpl(2);
        Function<Integer, Integer> fun = (i) -> i + 1;
        LightFuture<Integer> fut = tp.feed(waitAndReturnOne).thenApply(fun);
        assertEquals(fut.get(), TWO);
    }

    @Test
    public void testThenApplyMultiple() throws Exception {
        int n = 100;
        ThreadPool tp = new ThreadPoolImpl(n);
        Supplier<Integer> spl = () -> 1;
        CyclicBarrier barrier = new CyclicBarrier(n);
        Function<Integer, Integer> fun = (i) -> {
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            return i + 1;

        };
        LightFuture<Integer> fut = tp.feed(spl);
        List<LightFuture<Integer>> futs = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            futs.add(fut.thenApply(fun));
        }
        for (int i = 0; i < n; i++) {
            assertEquals(futs.get(i).get(), TWO);
        }
    }

    @Test
    public void testGenerics() throws Exception {
        ThreadPool tp = new ThreadPoolImpl(3);
        Supplier<Integer> spl = () -> 30;
        LightFuture<Integer> lf = tp.feed(spl);
        Number result = lf.get();

        Function<Number, Number> fun = (i) -> i.intValue() + 1;
        LightFuture<Number> fut = tp.feed(waitAndReturnOne).thenApply(fun);

        Function<Number, Integer> fun2 = (i) -> i.intValue() + 1;
        LightFuture<Number> fut2 = tp.feed(waitAndReturnOne).thenApply(fun2);
    }
}
