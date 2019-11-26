package de.hhu.bsinfo.neutrino.util;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerStack {

    private static final int STACK_EMPTY = -1;

    private final int[] stack;
    private final AtomicInteger index;

    public AtomicIntegerStack(int size) {
        stack = new int[size];
        index = new AtomicInteger(0);
    }

    public int pop() {
        var newIndex = index.decrementAndGet();
        if (newIndex < 0) {
            index.set(0);
            return STACK_EMPTY;
        }

        return stack[newIndex];
    }

    public boolean push(int value) {
        var oldIndex = index.getAndIncrement();
        if (oldIndex >= stack.length) {
            index.set(stack.length);
            return false;
        }

        stack[oldIndex] = value;
        return true;
    }
}
