package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.struct.field.NativeInteger;
import de.hhu.bsinfo.neutrino.struct.field.NativeObject;
import de.hhu.bsinfo.neutrino.struct.field.NativeString;

import de.hhu.bsinfo.neutrino.util.MemoryUtil;
import lombok.extern.slf4j.Slf4j;
import org.agrona.concurrent.AtomicBuffer;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MemberInformation implements NativeObject {

    public static final int SIZE = 36;

    public final long handle;
    public final NativeString name;
    public final NativeInteger offset;

    public MemberInformation(long handle) {
        AtomicBuffer byteBuffer = MemoryUtil.wrap(handle, SIZE);
        name = new NativeString(byteBuffer, 0, 32);
        offset = new NativeInteger(byteBuffer, 32);
        this.handle = handle;
    }

    public static List<MemberInformation> wrap(long handle, int length) {
        var result = new ArrayList<MemberInformation>();
        for (long index = 0; index < length; index++) {
            result.add(new MemberInformation(handle + index * SIZE));
        }
        return result;
    }

    public String getName() {
        return name.get();
    }

    public int getOffset() {
        return offset.get();
    }

    @Override
    public long getHandle() {
        return handle;
    }

    @Override
    public int getNativeSize() {
        return SIZE;
    }
}
