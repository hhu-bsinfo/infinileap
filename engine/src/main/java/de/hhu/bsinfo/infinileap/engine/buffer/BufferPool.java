package de.hhu.bsinfo.infinileap.engine.buffer;

public interface BufferPool {

    PooledBuffer claim();

    PooledBuffer claim(int timeout);

    void release(int identifier);

    PooledBuffer get(int identifier);
}
