package de.hhu.bsinfo.neutrino.api.util.service.resolve;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Node<T> {

    private final T identifier;

    private final Set<Node<T>> edges;

    public Node(final @NotNull T content) {
        identifier = content;
        edges = new HashSet<>();
    }

    public void addEdge(final @NotNull Node<T> node) {
        edges.add(node);
    }

    public T getIdentifier() {
        return identifier;
    }

    public Set<Node<T>> getEdges() {
        return Collections.unmodifiableSet(edges);
    }

    public int edgeCount() {
        return edges.size();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Node<?> node = (Node<?>) other;
        return identifier.equals(node.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }
}
