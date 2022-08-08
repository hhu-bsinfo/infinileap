package de.hhu.bsinfo.infinileap.engine.agent.message;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

import java.lang.invoke.VarHandle;

public class SendActiveMessage {

    private static final String FIELD_CHANNEL_ID = "channel_id";
    private static final String FIELD_BUFFER_ID = "buffer_id";
    private static final String FIELD_MESSAGE_ID = "message_id";
    private static final String FIELD_LENGTH = "length";

    private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName(FIELD_CHANNEL_ID),
            ValueLayout.JAVA_INT.withName(FIELD_BUFFER_ID),
            ValueLayout.JAVA_INT.withName(FIELD_MESSAGE_ID),
            ValueLayout.JAVA_INT.withName(FIELD_LENGTH)
    );

    public static final int BYTES = (int) LAYOUT.byteSize();

    private static final VarHandle CHANNEL_ID = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FIELD_CHANNEL_ID));
    private static final VarHandle BUFFER_ID = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FIELD_BUFFER_ID));

    private static final VarHandle MESSAGE_ID = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FIELD_MESSAGE_ID));
    private static final VarHandle LENGTH = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement(FIELD_LENGTH));

    public static int getChannelId(MemorySegment segment) {
        return (int) CHANNEL_ID.get(segment);
    }

    public static void setChannelId(MemorySegment segment, int id) {
        CHANNEL_ID.set(segment, id);
    }

    public static int getBufferId(MemorySegment segment) {
        return (int) BUFFER_ID.get(segment);
    }

    public static void setBufferId(MemorySegment segment, int id) {
        BUFFER_ID.set(segment, id);
    }

    public static int getMessageId(MemorySegment segment) {
        return (int) MESSAGE_ID.get(segment);
    }

    public static void setMessageId(MemorySegment segment, int id) {
        MESSAGE_ID.set(segment, id);
    }

    public static int getLength(MemorySegment segment) {
        return (int) LENGTH.get(segment);
    }

    public static void setLength(MemorySegment segment, int length) {
        LENGTH.set(segment, length);
    }
}
