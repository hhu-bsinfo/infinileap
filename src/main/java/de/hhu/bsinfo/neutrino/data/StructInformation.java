package de.hhu.bsinfo.neutrino.data;

import java.util.Map;
import java.util.stream.Collectors;

public class StructInformation extends Struct {

    private static final int SIZE = 16;

    public final NativeInteger structSize = new NativeInteger(getByteBuffer(), 0);
    public final NativeInteger memberCount = new NativeInteger(getByteBuffer(), 4);
    public final NativeLong memberInfoHandle = new NativeLong(getByteBuffer(), 8);

    private final Map<String, Integer> offsetInfoMap = MemberInformation.wrap(memberInfoHandle.get(), memberCount.get()).stream()
        .collect(Collectors.toUnmodifiableMap(MemberInformation::getName, MemberInformation::getOffset));

    public StructInformation() {
        super(SIZE);
    }

    public StructInformation(long handle) {
        super(handle, SIZE);
    }

    public int getOffset(String memberName) {
        if (offsetInfoMap.containsKey(memberName)) {
            return offsetInfoMap.get(memberName);
        }

        throw new IllegalArgumentException(String.format("No member found with name %s", memberName));
    }
}
