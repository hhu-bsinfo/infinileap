package de.hhu.bsinfo.infinileap.util;

import jdk.incubator.foreign.MemoryAddress;

import java.util.ArrayList;

public class NativeLinkedList<T extends NativeObject & Linkable<T>> {

    private final ArrayList<T> elements = new ArrayList<>();

    private T current;

    public void add(final T element) {
        if (current != null) {
            current.linkWith(element);
        }

        elements.add(element);
        current = element;
    }

    public T get(final int index) {
        return elements.get(index);
    }

    public void clear() {
        for(T element : elements) {
            element.unlink();
        }

        elements.clear();
        current = null;
    }

    public int size() {
        return elements.size();
    }

    @SafeVarargs
    public static <T extends NativeObject & Linkable<T>> NativeLinkedList<T> from(T... elements) {
        var list = new NativeLinkedList<T>();
        for (T element : elements) {
            list.add(element);
        }
        return list;
    }

    public MemoryAddress address() {
        if (elements.isEmpty()) {
            return MemoryAddress.NULL;
        }

        return elements.get(0).address();
    }
}
