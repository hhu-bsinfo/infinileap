/*
 * Copyright (C) 2019 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.neutrino.api.module.resolve;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyGraph<T> {

    private final Map<T, Node<T>> nodes = new HashMap<>();

    @SafeVarargs
    public final void add(final @NotNull T dependent, final T... dependencies) {
        Node<T> from = putIfAbsent(dependent);
        Arrays.stream(dependencies)
                .filter(Objects::nonNull)
                .forEach(to -> addEdge(from, to));
    }

    private void addEdge(final @NotNull Node<T> from, final @NotNull T to) {
        from.addEdge(putIfAbsent(to));
    }

    private Node<T> putIfAbsent(final @NotNull T element) {
        if (!nodes.containsKey(element)) {
            Node<T> node = new Node<>(element);
            nodes.put(element, node);
            return node;
        }

        return nodes.get(element);
    }

    public List<T> values() {
        return nodes.values().stream()
                .map(Node::getIdentifier)
                .collect(Collectors.toList());
    }

    /**
     * Determines the order in which dependencies must be loaded.
     *
     * @param root The root node.
     * @return An ordered list containing the dependencies to load.
     * @throws CircularDependencyException If a circular dependency was detected.
     */
    public List<T> resolve(final @NotNull T root) {
        Node<T> rootNode = nodes.get(root);
        if (rootNode == null) {
            return Collections.emptyList();
        }

        List<Node<T>> resolved = new ArrayList<>();
        Set<Node<T>> seen = new HashSet<>();

        resolve(rootNode, resolved, seen);

        return resolved.stream()
                .map(Node::getIdentifier)
                .collect(Collectors.toList());
    }

    private static <T> void resolve(final @NotNull Node<T> root, final @NotNull List<Node<T>> resolved, final @NotNull Set<Node<T>> seen) {
        seen.add(root);
        for (Node<T> node : root.getEdges()) {
            // Check if this node was already resolved
            if (resolved.contains(node)) {
                continue;
            }

            // Check if this node was already seen to prevent circular dependencies
            if (seen.contains(node)) {
                throw new CircularDependencyException("Circular dependency between {} and {} detected",
                        root.getIdentifier().getClass().getName(),
                        node.getIdentifier().getClass().getName());
            }

            resolve(node, resolved, seen);
        }
        resolved.add(root);
    }
}
