package de.hhu.bsinfo.neutrino.verbs;

import de.hhu.bsinfo.neutrino.data.NativeByte;
import de.hhu.bsinfo.neutrino.data.NativeInteger;
import de.hhu.bsinfo.neutrino.data.NativeLong;
import de.hhu.bsinfo.neutrino.struct.Struct;
import de.hhu.bsinfo.neutrino.util.LinkNative;

@LinkNative("rdma_conn_param")
public final class ConnectionParameters extends Struct {

    private final NativeLong privateData = longField("private_data");
    private final NativeByte privateDataLength = byteField("private_data_len");
    private final NativeByte responderResources = byteField("responder_resources");
    private final NativeByte initiatorDepth = byteField("initiator_depth");
    private final NativeByte flowControl = byteField("flow_control");
    private final NativeByte retryCount = byteField("retry_count");
    private final NativeByte rnrRetryCount = byteField("rnr_retry_count");
    private final NativeByte sharedReceiveQueue = byteField("srq");
    private final NativeInteger queuePairNumber = integerField("qp_num");

    public ConnectionParameters() {}

    public long getPrivateData() {
        return privateData.get();
    }

    public byte getPrivateDataLength() {
        return privateDataLength.get();
    }

    public byte getResponderResources() {
        return responderResources.get();
    }

    public byte getInitiatorDepth() {
        return initiatorDepth.get();
    }

    public byte getFlowControl() {
        return flowControl.get();
    }

    public byte getRetryCount() {
        return retryCount.get();
    }

    public byte getRnrRetryCount() {
        return rnrRetryCount.get();
    }

    public byte getSharedReceiveQueue() {
        return sharedReceiveQueue.get();
    }

    public int getQueuePairNumber() {
        return queuePairNumber.get();
    }

    public void setPrivateData(final long value) {
        privateData.set(value);
    }

    public void setPrivateDataLength(final byte value) {
        privateDataLength.set(value);
    }

    public void setResponderResources(final byte value) {
        responderResources.set(value);
    }

    public void setInitiatorDepth(final byte value) {
        initiatorDepth.set(value);
    }

    public void setFlowControl(final byte value) {
        flowControl.set(value);
    }

    public void setRetryCount(final byte value) {
        retryCount.set(value);
    }

    public void setRnrRetryCount(final byte value) {
        rnrRetryCount.set(value);
    }

    public void setSharedReceiveQueue(final byte value) {
        sharedReceiveQueue.set(value);
    }

    public void setQueuePairNumber(final int value) {
        queuePairNumber.set(value);
    }
}
