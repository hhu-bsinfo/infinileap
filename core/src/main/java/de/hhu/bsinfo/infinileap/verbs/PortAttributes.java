package de.hhu.bsinfo.infinileap.verbs;

import static org.linux.rdma.infinileap_h.*;

import de.hhu.bsinfo.infinileap.util.Struct;
import jdk.incubator.foreign.MemoryAddress;

public final class PortAttributes extends Struct {

    public PortAttributes() {
        super(ibv_port_attr.allocate());
    }

    public PortAttributes(MemoryAddress address) {
        super(address, ibv_port_attr.$LAYOUT());
    }

    public int getState() {
        return ibv_port_attr.state$get(segment());
    }

    public int getMaxMtu() {
        return ibv_port_attr.max_mtu$get(segment());
    }

    public int getActiveMtu() {
        return ibv_port_attr.active_mtu$get(segment());
    }

    public int getSourceGidTableLength() {
        return ibv_port_attr.gid_tbl_len$get(segment());
    }

    public int getPortCapabilityFlags() {
        return ibv_port_attr.port_cap_flags$get(segment());
    }

    public int getMaxMessageSize() {
        return ibv_port_attr.max_msg_sz$get(segment());
    }

    public int getBadPartitionKeyCounter() {
        return ibv_port_attr.bad_pkey_cntr$get(segment());
    }

    public int getQueueKeyViolationCounter() {
        return ibv_port_attr.qkey_viol_cntr$get(segment());
    }

    public short getPartitionTableLength() {
        return ibv_port_attr.pkey_tbl_len$get(segment());
    }

    public short getLocalId() {
        return ibv_port_attr.lid$get(segment());
    }

    public short getSubnetManagerLocalId() {
        return ibv_port_attr.sm_lid$get(segment());
    }

    public byte getLocalIdMaskControl() {
        return ibv_port_attr.lmc$get(segment());
    }

    public byte getMaxVirtualLaneCount() {
        return ibv_port_attr.max_vl_num$get(segment());
    }

    public byte getSubnetManagerServiceLevel() {
        return ibv_port_attr.sm_sl$get(segment());
    }

    public byte getSubnetTimeout() {
        return ibv_port_attr.subnet_timeout$get(segment());
    }

    public byte getInitializationType() {
        return ibv_port_attr.init_type_reply$get(segment());
    }

    public byte getLinkWidth() {
        return ibv_port_attr.active_width$get(segment());
    }

    public byte getLinkSpeed() {
        return ibv_port_attr.active_speed$get(segment());
    }

    public byte getPortState() {
        return ibv_port_attr.phys_state$get(segment());
    }

    public byte getLinkLayerProtocol() {
        return ibv_port_attr.link_layer$get(segment());
    }

    public byte getFlags() {
        return ibv_port_attr.flags$get(segment());
    }

    public short getExtraPortCapabilityFlags() {
        return ibv_port_attr.port_cap_flags2$get(segment());
    }

    public void setState(final int value) {
        ibv_port_attr.state$set(segment(), value);
    }

    public void setMaxMtu(final int value) {
        ibv_port_attr.max_mtu$set(segment(), value);
    }

    public void setActiveMtu(final int value) {
        ibv_port_attr.active_mtu$set(segment(), value);
    }

    public void setSourceGidTableLength(final int value) {
        ibv_port_attr.gid_tbl_len$set(segment(), value);
    }

    public void setPortCapabilityFlags(final int value) {
        ibv_port_attr.port_cap_flags$set(segment(), value);
    }

    public void setMaxMessageSize(final int value) {
        ibv_port_attr.max_msg_sz$set(segment(), value);
    }

    public void setBadPartitionKeyCounter(final int value) {
        ibv_port_attr.bad_pkey_cntr$set(segment(), value);
    }

    public void setQueueKeyViolationCounter(final int value) {
        ibv_port_attr.qkey_viol_cntr$set(segment(), value);
    }

    public void setPartitionTableLength(final short value) {
        ibv_port_attr.pkey_tbl_len$set(segment(), value);
    }

    public void setLocalId(final short value) {
        ibv_port_attr.lid$set(segment(), value);
    }

    public void setSubnetManagerLocalId(final short value) {
        ibv_port_attr.sm_lid$set(segment(), value);
    }

    public void setLocalIdMaskControl(final byte value) {
        ibv_port_attr.lmc$set(segment(), value);
    }

    public void setMaxVirtualLaneCount(final byte value) {
        ibv_port_attr.max_vl_num$set(segment(), value);
    }

    public void setSubnetManagerServiceLevel(final byte value) {
        ibv_port_attr.sm_sl$set(segment(), value);
    }

    public void setSubnetTimeout(final byte value) {
        ibv_port_attr.subnet_timeout$set(segment(), value);
    }

    public void setInitializationType(final byte value) {
        ibv_port_attr.init_type_reply$set(segment(), value);
    }

    public void setLinkWidth(final byte value) {
        ibv_port_attr.active_width$set(segment(), value);
    }

    public void setLinkSpeed(final byte value) {
        ibv_port_attr.active_speed$set(segment(), value);
    }

    public void setPortState(final byte value) {
        ibv_port_attr.phys_state$set(segment(), value);
    }

    public void setLinkLayerProtocol(final byte value) {
        ibv_port_attr.link_layer$set(segment(), value);
    }

    public void setFlags(final byte value) {
        ibv_port_attr.flags$set(segment(), value);
    }

    public void setExtraPortCapabilityFlags(final short value) {
        ibv_port_attr.port_cap_flags2$set(segment(), value);
    }
}
