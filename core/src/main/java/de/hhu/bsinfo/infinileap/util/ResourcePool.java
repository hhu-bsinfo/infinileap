package de.hhu.bsinfo.infinileap.util;

import java.util.Stack;
import java.util.function.Supplier;

public class ResourcePool implements AutoCloseable {

    private final Stack<AutoCloseable> resources = new Stack<>();

    public <T extends AutoCloseable> T push(T resource) {
        resources.push(resource);
        return resource;
    }

    @Override
    public void close() throws CloseException {
        try {
            while (!resources.empty()) {
                resources.pop().close();
            }
        } catch (Exception e) {
            throw new CloseException(e);
        }
    }
}
