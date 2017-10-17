package com.simiyutin.javaii;

public interface LockFreeList<T> {
    boolean isEmpty();

    /**
     * Appends value to the end of list
     */
    void append(T value);

    boolean remove(T value);

    boolean contains(T value);
}
