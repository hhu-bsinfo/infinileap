package de.hhu.bsinfo.infinileap.util;

import de.hhu.bsinfo.infinileap.binding.NativeLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Stack;

public class ResourcePool implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NativeLogger.class);

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
