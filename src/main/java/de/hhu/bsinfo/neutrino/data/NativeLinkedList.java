package de.hhu.bsinfo.neutrino.data;

import de.hhu.bsinfo.neutrino.verbs.SendWorkRequest;
import java.util.ArrayList;
import java.util.List;

public class NativeLinkedList<T extends NativeObject> {

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

}
