package de.hhu.bsinfo.neutrino.data;

import java.util.ArrayList;
import java.util.List;

public class MemberInformation extends Struct {

    private static final int SIZE = 36;

    public final NativeString name = new NativeString(getByteBuffer(), 0, 32);
    public final NativeInteger offset = new NativeInteger(getByteBuffer(), 32);

    public MemberInformation() {
        super(SIZE);
    }

    public MemberInformation(long handle) {
        super(handle, SIZE);
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
}
