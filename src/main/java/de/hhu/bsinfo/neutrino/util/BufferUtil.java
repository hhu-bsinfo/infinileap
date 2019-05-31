package de.hhu.bsinfo.neutrino.util;

import de.hhu.bsinfo.neutrino.buffer.LocalBuffer;
import de.hhu.bsinfo.neutrino.buffer.RegisteredBuffer;
import de.hhu.bsinfo.neutrino.data.NativeArray;
import de.hhu.bsinfo.neutrino.verbs.ScatterGatherElement;

public class BufferUtil {

    public static ScatterGatherElement.Array split(final RegisteredBuffer buffer) {
        int slots = (int) (buffer.capacity() / Integer.MAX_VALUE) + 1;
        int remainder = (int) (buffer.capacity() & Integer.MAX_VALUE);

        var array = new ScatterGatherElement.Array(slots + 1);
        return array.forEachIndexed((index, element) -> {
            element.setLocalKey(buffer.getLocalKey());
            element.setLength(index == slots ? Integer.MAX_VALUE : remainder);
            element.setAddress(buffer.getHandle() + (long) index * Integer.MAX_VALUE);
        });
    }

}
