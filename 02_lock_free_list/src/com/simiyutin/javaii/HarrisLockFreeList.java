package com.simiyutin.javaii;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class HarrisLockFreeList<T> implements LockFreeList<T>{
    private final Node<T> head;
    private final Node<T> tail;

    public HarrisLockFreeList() {
        head = new Node<>();
        tail = new Node<>();
        head.nextRef = new AtomicMarkableReference<>(tail, false);
    }

    @Override
    public boolean isEmpty() {
        return next(head) == tail;
    }

    // node is marked <==> its .next field is marked
    @Override
    public void append(T value) {
        Node<T> newNode = new Node<>(value);
        while (true) {
            Node<T> headNext = head.nextRef.getReference();
            newNode.nextRef = new AtomicMarkableReference<>(headNext, false);
            if (head.nextRef.compareAndSet(headNext, newNode, false, false)) {
                break;
            }
        }
    }

    @Override
    public boolean remove(T value) {
        Pair<T> p;
        Node<T> rightNodeNext;
        boolean rightNodeNextMarked;
        boolean[] holder = {false};

        while (true) {
            p = search(value);
            if (p.rightNode == tail || !p.rightNode.key.equals(value)) {
                return false;
            }
            rightNodeNext = p.rightNode.nextRef.get(holder);
            rightNodeNextMarked = holder[0];
            if (!rightNodeNextMarked) {
                if (p.rightNode.nextRef.compareAndSet(rightNodeNext, rightNodeNext, false, true)) {
                    break;
                }
            }
        }

        if (!p.leftNode.nextRef.compareAndSet(p.rightNode, rightNodeNext, false, false)) {
            search(p.rightNode.key);
        }

        return true;
    }

    @Override
    public boolean contains(T value) {
        Pair<T> neighbors = search(value);
        return neighbors.rightNode != tail && neighbors.rightNode.key.equals(value);
    }

    private Node<T> next(Node<T> node) {
        boolean[] holder = {false};
        Node<T> next = node.nextRef.getReference();
        while (next != tail) {
            Node<T> nextOfNext = next.nextRef.get(holder);
            if (!holder[0]) {
                break;
            }
            next = nextOfNext;
        }
        return next;
    }

    private Pair<T> search(T key) {
        Pair<T> p = new Pair<>();
        Node<T> leftNodeNext = null;
        boolean[] holder = {false};

        while (true) {
            Node<T> t = head;
            Node<T> tNext = t.nextRef.get(holder);
            boolean tNextMarked = holder[0];

            do {
                if (!tNextMarked) { //head.next will never be marked
                    p.leftNode = t;
                    leftNodeNext = tNext;
                }

                t = tNext;
                if (t == tail) {
                    break;
                }
                tNext = t.nextRef.get(holder);
                tNextMarked = holder[0];
            } while (tNextMarked || !t.key.equals(key));
            p.rightNode = t;

            if (leftNodeNext == p.rightNode) { //never null
                if (p.rightNode != tail && p.rightNode.nextRef.isMarked()) {
                    continue;
                } else {
                    return p;
                }
            }

            if (p.leftNode.nextRef.compareAndSet(leftNodeNext, p.rightNode, false, false)) { // leftNodeNext was not marked on assignment
                if (p.rightNode == tail || !p.rightNode.nextRef.isMarked()) {
                    return p;
                }
            }
        }
    }

    private static class Pair<T> {
        Node<T> leftNode;
        Node<T> rightNode;
    }

    private static class Node<T> {
        T key;
        AtomicMarkableReference<Node<T>> nextRef;

        Node(T key) {
            this.key = key;
        }

        Node() {

        }
    }
}
