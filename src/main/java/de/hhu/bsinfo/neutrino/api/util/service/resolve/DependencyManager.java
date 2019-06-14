package de.hhu.bsinfo.neutrino.api.util.service.resolve;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DependencyManager<T> {

    private final DependencyGraph<Class<? extends T>> graph = new DependencyGraph<>();
    private final Predicate<Class<? extends T>> defaultPredicate = in -> true;

    @SuppressWarnings("unchecked")
    public void register(final Class<? extends T> target) {
        var dependencies = getDependencies(target).toArray(new Class[0]);
        graph.add(target, dependencies);
    }

    public List<Class<? extends T>> getOrderedDependencies() {
        return getOrderedDependencies(defaultPredicate);
    }

    public List<Class<? extends T>> getOrderedDependencies(Class<? extends T> superClass) {
        return getOrderedDependencies(new ClassPredicate<>(superClass));
    }

    public List<Class<? extends T>> getOrderedDependencies(Predicate<Class<? extends T>> filter) {
        return graph.values().stream()
                .flatMap(dependency -> graph.resolve(dependency).stream())
                .filter(filter)
                .distinct()
                .collect(Collectors.toList());
    }

    static List<Class<?>> getDependencies(final Class<?> target) {
        return Arrays.stream(target.getDeclaredFields())
                .filter(field -> field.getAnnotation(Inject.class) != null)
                .map(Field::getType)
                .collect(Collectors.toList());
    }

    private static class ClassPredicate<T> implements Predicate<Class<? extends T>> {

        private final Class<?> superClass;

        ClassPredicate(Class<?> superClass) {
            this.superClass = superClass;
        }

        @Override
        public boolean test(Class<? extends T> target) {
            return superClass.isAssignableFrom(target);
        }
    }
}
