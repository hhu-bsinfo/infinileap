package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;
import jdk.incubator.foreign.MemoryAddress;
import jdk.incubator.foreign.MemorySegment;
import org.openucx.ucx_h.*;

import static org.openucx.ucx_h.*;

public class MappingParameters extends NativeObject {

    public MappingParameters() {
        super(ucp_mem_map_params_t.allocate());
    }

    public MappingParameters setSegment(MemorySegment segment) {
        return setAddress(segment.address()).setLength(segment.byteSize());
    }

    public MappingParameters setAddress(MemoryAddress address) {
        ucp_mem_map_params_t.address$set(segment(), address);
        addFieldMask(Field.ADDRESS);
        return this;
    }

    public MappingParameters setLength(long length) {
        ucp_mem_map_params_t.length$set(segment(), length);
        addFieldMask(Field.LENGTH);
        return this;
    }

    public MappingParameters setFlags(Flag... flags) {
        ucp_mem_map_params_t.flags$set(segment(), BitMask.intOf(flags));
        addFieldMask(Field.FLAGS);
        return this;
    }

    private long getFieldMask() {
        return ucp_mem_map_params_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_mem_map_params_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_mem_map_params_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    public enum Field implements LongFlag {
        ADDRESS(UCP_MEM_MAP_PARAM_FIELD_ADDRESS()),
        LENGTH(UCP_MEM_MAP_PARAM_FIELD_LENGTH()),
        FLAGS(UCP_MEM_MAP_PARAM_FIELD_FLAGS());

        private final long value;

        Field(long value) {
            this.value = value;
        }

        @Override
        public long getValue() {
            return value;
        }
    }

    public enum Flag implements IntegerFlag {
        NONBLOCK(UCP_MEM_MAP_NONBLOCK()),
        ALLOCATE(UCP_MEM_MAP_ALLOCATE()),
        FIXED(UCP_MEM_MAP_FIXED());

        private final int value;

        Flag(int value) {
            this.value = value;
        }

        @Override
        public int getValue() {
            return value;
        }
    }
}
