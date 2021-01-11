package de.hhu.bsinfo.infinileap.binding;

import jdk.incubator.foreign.CLinker;
import jdk.incubator.foreign.FunctionDescriptor;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

@FunctionalInterface
public interface ConnectionHandler {

    /* static final */ FunctionDescriptor DESCRIPTOR = FunctionDescriptor.ofVoid(
            CLinker.C_POINTER,
            CLinker.C_POINTER
    );

    MethodType METHOD_TYPE = MethodType.methodType(void.class, MemoryAddress.class, MemoryAddress.class);

    void onConnection(MemoryAddress request, MemoryAddress data);

    private void callback(MemoryAddress request, MemoryAddress data) {
        onConnection(request, data);
    }

    default MemorySegment upcallStub() {
        var linker = CLinker.getInstance();

        try {
            var methodHandle = MethodHandles.lookup()
                    .findVirtual(ConnectionHandler.class, "callback", METHOD_TYPE)
                    .bindTo(this);

            return linker.upcallStub(methodHandle, DESCRIPTOR);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }
}
