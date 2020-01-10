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

        var value = expectedHead.value;
        expectedHead.releaseInstance();
        return value;
    }

    public void push(int value) {
        var pool = NODE_POOL.get();
        Node expectedHead;
        Node newHead = pool.getInstance();
        newHead.value = value;

        do {
            expectedHead = head.get();
            newHead.next = expectedHead;
        } while (!head.compareAndSet(expectedHead, newHead));
    }
}
