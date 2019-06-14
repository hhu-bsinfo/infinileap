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

package de.hhu.bsinfo.neutrino.api.util.service;

import de.hhu.bsinfo.neutrino.api.util.service.inject.DependencyInjector;
import de.hhu.bsinfo.neutrino.api.util.service.resolve.DependencyManager;
import de.hhu.bsinfo.neutrino.api.util.service.resolve.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

public class ServiceManager implements ServiceProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceManager.class);

    private final Map<Class<? extends Service<?>>, ServiceContainer> services = new HashMap<>();
    private final DependencyManager<Service<?>> dependencyManager = new DependencyManager<>();
    private final DependencyInjector injector;

    public ServiceManager() {
        injector = new DependencyInjector(this);
    }

    private static void loadServices() {
        ServiceLoader.load(Service.class);
    }

    public void register(final Class<? extends Service<?>> module) {
        Class<? extends Service<?>> moduleClass = findModuleClass(module);
        services.put(moduleClass, new ServiceContainer(module, findOptionsClass(moduleClass)));
        dependencyManager.register(moduleClass);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Service<?>> findModuleClass(final Class<?> target) {
        var current = target;
        while (current.getSuperclass() != null) {
            if (current.getSuperclass().equals(Service.class)) {
                return (Class<? extends Service<?>>) current;
            }

            current = target.getSuperclass();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ServiceOptions> findOptionsClass(final Class<? extends Service<?>> target) {
        ParameterizedType parameterizedType = (ParameterizedType) target.getGenericSuperclass();
        return (Class<? extends ServiceOptions>) parameterizedType.getActualTypeArguments()[0];
    }

    public void initialize() {
        for (var module : dependencyManager.getOrderedDependencies()) {
            ServiceContainer container = services.get(module);
            ServiceOptions config = container.newOptionsInstance();
            var instance = container.newInstance(config);
            injector.inject(instance);
            instance.onInit();
        }
    }

    public List<Service<? extends ServiceOptions>> getServices() {
        return dependencyManager.getOrderedDependencies().stream()
                .map(services::get)
                .map(ServiceContainer::getInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends Service<?>> T get(Class<T> module) {
        return module.cast(services.get(module).getInstance());
    }

    public static class ServiceContainer {
        private final Class<? extends Service<? extends ServiceOptions>> moduleClass;
        private final Class<? extends ServiceOptions> optionsClass;

        private Service<? extends ServiceOptions> instance;

        ServiceContainer(final Class<? extends Service<?>> moduleClass, final Class<? extends ServiceOptions> optionsClass) {
            this.moduleClass = moduleClass;
            this.optionsClass = optionsClass;
        }

        ServiceOptions newOptionsInstance() {
            try {
                return optionsClass.getConstructor().newInstance();
            } catch (final Exception e) {
                throw new ModuleInstantiationException("Creating options for {} failed", e, moduleClass.getSimpleName());
            }
        }

        Service<? extends ServiceOptions> newInstance(final ServiceOptions options) {
            if (instance != null) {
                throw new ModuleInstantiationException("An instance of {} was already created: ", instance.getClass().getSimpleName());
            }

            try {
                instance = moduleClass.getConstructor().newInstance();
            } catch (final Exception e) {
                throw new ModuleInstantiationException("Creating instance of {} failed", e, moduleClass.getSimpleName());
            }

            instance.setOptions(options);
            return instance;
        }

        public Class<? extends Service<?>> getModuleClass() {
            return moduleClass;
        }

        public Class<? extends ServiceOptions> getOptionsClass() {
            return optionsClass;
        }

        public Service<?> getInstance() {
            return instance;
        }
    }
}
