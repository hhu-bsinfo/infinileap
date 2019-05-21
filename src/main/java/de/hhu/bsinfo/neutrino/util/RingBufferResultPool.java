package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.struct.Result;
import java.util.Objects;

public class RingBufferResultPool implements ObjectPool<Result> {

    private final RingBuffer<Result> buffer;

    public RingBufferResultPool(int size) {
        buffer = new RingBuffer<>(size, Result.class);

        for(int i = 0; i < buffer.size(); i++) {
            buffer.push(new Result());
        }
    }

    @Override
    public Result getInstance() {
        return Objects.requireNonNullElseGet(buffer.pop(), Result::new);
    }

    @Override
    public void returnInstance(Result result) {
        buffer.push(result);
    }
}
