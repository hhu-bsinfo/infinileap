package de.hhu.bsinfo.neutrino.struct;

import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.util.StructUtil;

import java.util.Map;
import java.util.stream.Collectors;

public class StructInformation extends Struct {

    public static final int SIZE = 16;

    public final NativeInteger structSize = new NativeInteger(getByteBuffer(), 0);
    public final NativeInteger memberCount = new NativeInteger(getByteBuffer(), 4);
    public final NativeLong memberInfoHandle = new NativeLong(getByteBuffer(), 8);

    private final Map<String, Integer> offsetInfoMap =
            StructUtil.wrap(MemberInformation::new, memberInfoHandle.get(), MemberInformation.SIZE, memberCount.get())
                    .stream()
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
