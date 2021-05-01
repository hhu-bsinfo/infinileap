package de.hhu.bsinfo.infinileap.binding;

import de.hhu.bsinfo.infinileap.util.BitMask;
import de.hhu.bsinfo.infinileap.util.flag.IntegerFlag;
import de.hhu.bsinfo.infinileap.util.flag.LongFlag;

import static org.openucx.ucx_h.*;

public class HandlerParameters extends NativeObject {

    public HandlerParameters() {
        super(ucp_am_handler_param_t.allocate());
    }

    public HandlerParameters setId(Identifier id) {
        ucp_am_handler_param_t.id$set(segment(), id.value());
        addFieldMask(Field.ID);
        return this;
    }

    public HandlerParameters setFlags(Flag... flags) {
        ucp_am_handler_param_t.flags$set(segment(), BitMask.intOf(flags));
        addFieldMask(Field.FLAGS);
        return this;
    }

    public HandlerParameters setCallback(ActiveMessageCallback callback) {
        ucp_am_handler_param_t.cb$set(segment(), callback.upcallStub().address());
        addFieldMask(Field.CALLBACK);
        return this;
    }

    private long getFieldMask() {
        return ucp_am_handler_param_t.field_mask$get(segment());
    }

    private void setFieldMask(Field... fields) {
        ucp_am_handler_param_t.field_mask$set(segment(), BitMask.longOf(fields));
    }

    private void addFieldMask(Field... fields) {
        ucp_am_handler_param_t.field_mask$set(segment(), BitMask.longOf(fields) | getFieldMask());
    }

    public enum Field implements LongFlag {
        ID(UCP_AM_HANDLER_PARAM_FIELD_ID()),
        FLAGS(UCP_AM_HANDLER_PARAM_FIELD_FLAGS()),
        CALLBACK(UCP_AM_HANDLER_PARAM_FIELD_CB()),
        ARGUMENT(UCP_AM_HANDLER_PARAM_FIELD_ARG());

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
        WHOLE_MESSAGE(UCP_AM_FLAG_WHOLE_MSG());

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
