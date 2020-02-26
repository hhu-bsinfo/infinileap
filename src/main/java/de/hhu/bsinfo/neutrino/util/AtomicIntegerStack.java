package de.hhu.bsinfo.neutrino.util;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicIntegerStack {

    private static final class Node implements Poolable {
        private int value;
        private Node next;

        @Override
        public void releaseInstance() {
            NODE_POOL.get().returnInstance(this);
        }
    }

    private static final int STACK_EMPTY = -1;

    private final AtomicReference<Node> head = new AtomicReference<>();

    private static final ThreadLocal<RingBufferPool<Node>> NODE_POOL = ThreadLocal.withInitial(() -> new RingBufferPool<>(4096, Node::new));

    public int pop() {
        Node expectedHead;
        Node newHead;

        do {
            expectedHead = head.get();
            if (expectedHead == null) {
                return STACK_EMPTY;
            }

            newHead = expectedHead.next;
        } while (!head.compareAndSet(expectedHead, newHead));

        return expectedHead.value;
    }

    public void push(int value) {
        Node newHead = new Node();
        newHead.value = value;

        Node expectedHead;
        do {
            expectedHead = head.get();
            newHead.next = expectedHead;
        } while (!head.compareAndSet(expectedHead, newHead));
    }
}
