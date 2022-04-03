package de.hhu.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.infinileap.engine.util.EndpointResolver;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static de.hhu.bsinfo.infinileap.binding.HandlerParameters.Flag.WHOLE_MESSAGE;

public class HandlerList implements Iterable<HandlerAdapter> {

    private final List<HandlerAdapter> handlerAdapters;

    private HandlerList(List<HandlerAdapter> handlerAdapters) {
        this.handlerAdapters = handlerAdapters;
    }

    public static HandlerList forServiceInstance(final Object serviceInstance, final EndpointResolver resolver) {
        // Collect user-defined RPC handlers
        var handlerAdapters = extractHandlers(serviceInstance.getClass()).stream()
                .map(method -> createAdapter(serviceInstance, method, resolver))
                .collect(Collectors.toList());

        return new HandlerList(handlerAdapters);
    }

    private static List<Method> extractHandlers(Class<?> serviceClass) {
        return Arrays.stream(serviceClass.getDeclaredMethods())
                .filter(field -> field.getAnnotation(Handler.class) != null)
                .collect(Collectors.toList());
    }

    private static HandlerAdapter createAdapter(Object serviceInstance, Method method, EndpointResolver resolver) {
        try {
            var caller = MethodHandles.lookup();
            var methodHandle = caller.unreflect(method);
            var type = MethodType.methodType(void.class, MemorySegment.class, MemorySegment.class, Endpoint.class);
            var site = LambdaMetafactory.metafactory(
                    caller,
                    "onMessage",
                    MethodType.methodType(MessageHandler.class, serviceInstance.getClass()),
                    type,
                    methodHandle,
                    type
            );

            MethodHandle factory = site.getTarget();
            MessageHandler messageHandler = (MessageHandler) factory.invoke(serviceInstance);
            return new HandlerAdapter(
                    method.getAnnotation(Handler.class).identifier(),
                    messageHandler,
                    resolver
            );
        } catch (Throwable e) {
            throw new RuntimeException("Creating adapter for service method failed", e);
        }
    }

    public void registerWith(Worker worker) throws ControlException {
        for (var adapter : handlerAdapters) {
            var handlerParameters = new HandlerParameters()
                    .setId(Identifier.of(adapter.getIdentifier()))
                    .setFlags(WHOLE_MESSAGE)
                    .setCallback(adapter);

            worker.setHandler(handlerParameters);
        }
    }

    @Override
    public Iterator<HandlerAdapter> iterator() {
        return handlerAdapters.iterator();
    }
}
