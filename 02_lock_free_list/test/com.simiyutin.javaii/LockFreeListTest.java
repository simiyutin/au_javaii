package com.simiyutin.javaii;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

public class LockFreeListTest {
    @Test
    public void testSimple() {
        LockFreeList<Integer> list = new HarrisLockFreeList<>();
        assertTrue(list.isEmpty());
        list.append(1);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(1));
        assertTrue(list.remove(1));
        assertFalse(list.remove(1));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testContains() {
        LockFreeList<Integer> list = new HarrisLockFreeList<>();
        assertFalse(list.contains(1));
        list.append(1);
        assertTrue(list.contains(1));
        assertFalse(list.contains(2));
        list.append(2);
        assertTrue(list.contains(2));
    }

    @Test
    public void testRemove() {
        LockFreeList<Integer> list = new HarrisLockFreeList<>();
        list.append(1);
        assertTrue(list.remove(1));
        assertFalse(list.remove(1));
    }

    @Test
    public void testContentionSingleRemove() throws InterruptedException {
        LockFreeList<Integer> list = new HarrisLockFreeList<>();

        list.append(1);

        List<Thread> threads = new ArrayList<>();
        int nthreads = 1000;

        AtomicInteger atomicInteger = new AtomicInteger(0);

        CyclicBarrier barrier = new CyclicBarrier(nthreads);

        Runnable task = () -> {

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            boolean success = list.remove(1);
            if (success) {
                atomicInteger.incrementAndGet();
            }
        };

        for (int i = 0; i < nthreads; i++) {
            threads.add(new Thread(task));
            threads.get(i).start();
        }

        for (int i = 0; i < nthreads; i++) {
            threads.get(i).join();
        }

        assertEquals(atomicInteger.get(), 1);
    }

    @Test
    public void testContentionFillAndClear() throws InterruptedException {
        LockFreeList<Integer> list = new HarrisLockFreeList<>();
        int nthreads = 1000;
        List<Thread> threads = new ArrayList<>();
        CyclicBarrier barrier = new CyclicBarrier(nthreads);
        // adding
        for (int i = 0; i < nthreads; i++) {
            final int k = i;
            threads.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.append(k);
            }));
            threads.get(i).start();
        }
        for (int i = 0; i < nthreads; i++) {
            threads.get(i).join();
        }

        //check all values are in list
        for (int i = 0; i < nthreads; i++) {
            assertTrue(list.contains(i));
        }

        threads.clear();
        barrier.reset();
        // removing
        for (int i = 0; i < nthreads; i++) {
            final int k = i;
            threads.add(new Thread(() -> {
                try {
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                list.remove(k);
            }));
            threads.get(i).start();
        }
        for (int i = 0; i < nthreads; i++) {
            threads.get(i).join();
        }

        //check all values are not in list
        for (int i = 0; i < nthreads; i++) {
            assertFalse(list.contains(i));
        }

        assertTrue(list.isEmpty());

    }

    @Test
    public void testBootstrap() throws InterruptedException {
        int ntries = 100;
        for (int i = 0; i < ntries; i++) {
            testContentionFillAndClear();
            testContentionSingleRemove();
        }
    }
}
