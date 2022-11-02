package de.hhu.bsinfo.infinileap.engine.message;

import de.hhu.bsinfo.infinileap.binding.*;
import de.hhu.bsinfo.infinileap.engine.channel.Channel;
import de.hhu.bsinfo.infinileap.engine.event.loop.EventLoopContext;
import de.hhu.bsinfo.infinileap.engine.util.ChannelResolver;

import java.lang.foreign.MemorySegment;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.hhu.bsinfo.infinileap.binding.HandlerParameters.Flag.WHOLE_MESSAGE;

public class CallManager {

    /**
     * The service's instance used for callbacks.
     */
    private final Object serviceInstance;

    /**
     * A list of adapters associated with one or more UCX active message handlers.
     */
    private final List<HandlerAdapter> handlerAdapters;

    private CallManager(List<HandlerAdapter> handlerAdapters, Object serviceInstance) {
        this.handlerAdapters = handlerAdapters;
        this.serviceInstance = serviceInstance;
    }

    public static CallManager forServiceInstance(final Object serviceInstance, final ChannelResolver resolver) {

        // Collect user-defined RPC handlers
        var handlers = extractHandlers(serviceInstance.getClass());

        // Create adapter for each handler
        var handlerAdapters = handlers.stream()
                .map(method -> createAdapter(serviceInstance, method, resolver))
                .toList();

        return new CallManager(handlerAdapters, serviceInstance);
    }

    private static List<Method> extractHandlers(Class<?> serviceClass) {
        return Arrays.stream(serviceClass.getDeclaredMethods())
                .filter(field -> field.isAnnotationPresent(Handler.class))
                .collect(Collectors.toList());
    }

    private static HandlerAdapter createAdapter(Object serviceInstance, Method method, ChannelResolver resolver) {
        try {
            var caller = MethodHandles.lookup();
            var methodHandle = caller.unreflect(method);
            var factoryType = MethodType.methodType(MessageHandler.class, serviceInstance.getClass());
            var interfaceType = MethodType.methodType(void.class, MemorySegment.class, MemorySegment.class, Channel.class);
            var dynamicType = MethodType.methodType(void.class, method.getParameterTypes()[0], method.getParameterTypes()[1], Channel.class);
            var site = LambdaMetafactory.metafactory(
                    caller,
                    "onMessage",
                    factoryType,
                    interfaceType,
                    methodHandle,
                    dynamicType
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

    public void registerOn(Worker worker) throws ControlException {
        var handlerParameters = new HandlerParameters();
        for (var adapter : handlerAdapters) {
            handlerParameters.setId(Identifier.of(adapter.getIdentifier()))
                    .setFlags(WHOLE_MESSAGE)
                    .setCallback(adapter);

            worker.setHandler(handlerParameters);
        }
    }
}
