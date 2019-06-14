package de.hhu.bsinfo.neutrino.api.util.service.inject;

import de.hhu.bsinfo.neutrino.api.util.service.Service;
import de.hhu.bsinfo.neutrino.api.util.service.resolve.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DependencyInjector implements Injector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DependencyInjector.class);

    private final ServiceProvider provider;

    public DependencyInjector(final ServiceProvider provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inject(final Object target) {
        for (Field injectableField : getDependencyFields(target.getClass())) {
            if (Modifier.isFinal(injectableField.getModifiers())){
                throw new InjectionException("Injecting final fields is not supported");
            }

            if (!Service.class.isAssignableFrom(injectableField.getType())) {
                throw new InjectionException("Injection is only supported for modules");
            }

            var type = (Class<? extends Service<?>>) injectableField.getType();
            Object object = provider.get(type);

            if (object == null) {
                LOGGER.warn("Could not find dependency {}", type.getName());
                continue;
            }

            if (!type.isAssignableFrom(object.getClass())) {
                LOGGER.warn("dependency {} is not compatible with field type {}",
                        object.getClass().getName(), type.getName());
                continue;
            }

            try {
                injectableField.setAccessible(true);
                injectableField.set(target, object);
            } catch (IllegalAccessException e) {
                throw new InjectionException("Could not inject {} within {}", e,
                        injectableField.getName(), object.getClass().getName());
            }
        }
    }

    private static List<Field> getDependencyFields(final Class<?> target) {
        return Arrays.stream(target.getDeclaredFields())
                .filter(field -> field.getAnnotation(Inject.class) != null)
                .collect(Collectors.toList());
    }
}
