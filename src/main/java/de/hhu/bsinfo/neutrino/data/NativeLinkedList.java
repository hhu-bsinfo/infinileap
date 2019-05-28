package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.util.Linkable;
import java.util.ArrayList;
import java.util.List;

public class NativeLinkedList<T extends NativeObject & Linkable<T>> implements NativeObject {

    private final List<T> elements = new ArrayList<>();

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

    @Override
    public long getHandle() {
        return elements.isEmpty() ? 0 : elements.get(0).getHandle();
    }
}
