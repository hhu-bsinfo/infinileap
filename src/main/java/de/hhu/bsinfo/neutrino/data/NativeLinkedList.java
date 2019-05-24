package de.hhu.bsinfo.neutrino.data;

import java.util.ArrayList;
import java.util.List;

public class NativeLinkedList<T extends NativeObject> implements NativeObject{

    @FunctionalInterface
    public interface Linker<T extends NativeObject> {
        void onLink(final T current, final T next);
    }

    private final List<T> elements = new ArrayList<>();
    private final Linker<T> linker;

    private T current;

    public NativeLinkedList(final Linker<T> linker) {
        this.linker = linker;
    }

    public void add(final T element) {
        if (current != null) {
            linker.onLink(current, element);
        }

        elements.add(element);
        current = element;
    }

    public T get(final int index) {
        return elements.get(index);
    }

    @Override
    public long getHandle() {
        return elements.isEmpty() ? 0 : elements.get(0).getHandle();
    }
}
