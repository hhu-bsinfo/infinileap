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

package de.hhu.bsinfo.neutrino.api.module;

import de.hhu.bsinfo.neutrino.api.module.inject.DependencyInjector;
import de.hhu.bsinfo.neutrino.api.module.resolve.DependencyManager;
import de.hhu.bsinfo.neutrino.api.module.resolve.ModuleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModuleManager implements ModuleProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleManager.class);

    private final Map<Class<? extends Module<?>>, ModuleContainer> modules = new HashMap<>();
    private final DependencyManager<Module<?>> dependencyManager = new DependencyManager<>();
    private final DependencyInjector injector;

    public ModuleManager() {
        injector = new DependencyInjector(this);
    }

    public void register(final Class<? extends Module<?>> module) {
        Class<? extends Module<?>> moduleClass = findModuleClass(module);
        modules.put(moduleClass, new ModuleContainer(module, findOptionsClass(moduleClass)));
        dependencyManager.register(moduleClass);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Module<?>> findModuleClass(final Class<?> target) {
        var current = target;
        while (current.getSuperclass() != null) {
            if (current.getSuperclass().equals(Module.class)) {
                return (Class<? extends Module<?>>) current;
            }

            current = target.getSuperclass();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends ModuleOptions> findOptionsClass(final Class<? extends Module<?>> target) {
        ParameterizedType parameterizedType = (ParameterizedType) target.getGenericSuperclass();
        return (Class<? extends ModuleOptions>) parameterizedType.getActualTypeArguments()[0];
    }

    public void initialize() {
        for (var module : dependencyManager.getOrderedDependencies()) {
            ModuleContainer container = modules.get(module);
            ModuleOptions config = container.newOptionsInstance();
            var instance = container.newInstance(config);
            injector.inject(instance);
            instance.onInit();
        }
    }

    public List<Module<? extends ModuleOptions>> getModules() {
        return dependencyManager.getOrderedDependencies().stream()
                .map(modules::get)
                .map(ModuleContainer::getInstance)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends Module<?>> T get(Class<T> module) {
        return module.cast(modules.get(module).getInstance());
    }

    public static class ModuleContainer {
        private final String name;
        private final Class<? extends Module<? extends ModuleOptions>> moduleClass;
        private final Class<? extends ModuleOptions> optionsClass;

        private Module<? extends ModuleOptions> instance;

        ModuleContainer(final Class<? extends Module<?>> moduleClass, final Class<? extends ModuleOptions> optionsClass) {
            name = moduleClass.getSimpleName();
            this.moduleClass = moduleClass;
            this.optionsClass = optionsClass;
        }

        ModuleOptions newOptionsInstance() {
            try {
                return optionsClass.getConstructor().newInstance();
            } catch (final Exception e) {
                throw new ModuleInstantiationException("Creating options for {} failed", e, moduleClass.getSimpleName());
            }
        }

        Module<? extends ModuleOptions> newInstance(final ModuleOptions options) {
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

        public String getName() {
            return name;
        }

        public Class<? extends Module<?>> getModuleClass() {
            return moduleClass;
        }

        public Class<? extends ModuleOptions> getOptionsClass() {
            return optionsClass;
        }

        public Module<?> getInstance() {
            return instance;
        }
    }
}
