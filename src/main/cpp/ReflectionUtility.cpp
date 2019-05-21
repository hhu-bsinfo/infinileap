#include <neutrino/ReflectionUtility.hpp>
#include <infiniband/verbs.h>

#define GET_MEMBER_INFO(structName, memberName) ReflectionUtility::MemberInfo{#memberName, offsetof(struct structName, memberName)}

ReflectionUtility::MemberInfo ibv_device_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_device_attr, fw_ver),
    GET_MEMBER_INFO(ibv_device_attr, node_guid),
    GET_MEMBER_INFO(ibv_device_attr, sys_image_guid),
    GET_MEMBER_INFO(ibv_device_attr, max_mr_size),
    GET_MEMBER_INFO(ibv_device_attr, page_size_cap),
    GET_MEMBER_INFO(ibv_device_attr, vendor_id),
    GET_MEMBER_INFO(ibv_device_attr, vendor_part_id),
    GET_MEMBER_INFO(ibv_device_attr, hw_ver),
    GET_MEMBER_INFO(ibv_device_attr, max_qp),
    GET_MEMBER_INFO(ibv_device_attr, max_qp_wr),
    GET_MEMBER_INFO(ibv_device_attr, device_cap_flags),
    GET_MEMBER_INFO(ibv_device_attr, max_sge),
    GET_MEMBER_INFO(ibv_device_attr, max_sge_rd),
    GET_MEMBER_INFO(ibv_device_attr, max_cq),
    GET_MEMBER_INFO(ibv_device_attr, max_cqe),
    GET_MEMBER_INFO(ibv_device_attr, max_mr),
    GET_MEMBER_INFO(ibv_device_attr, max_pd),
    GET_MEMBER_INFO(ibv_device_attr, max_qp_rd_atom),
    GET_MEMBER_INFO(ibv_device_attr, max_ee_rd_atom),
    GET_MEMBER_INFO(ibv_device_attr, max_res_rd_atom),
    GET_MEMBER_INFO(ibv_device_attr, max_qp_init_rd_atom),
    GET_MEMBER_INFO(ibv_device_attr, max_ee_init_rd_atom),
    GET_MEMBER_INFO(ibv_device_attr, atomic_cap),
    GET_MEMBER_INFO(ibv_device_attr, max_ee),
    GET_MEMBER_INFO(ibv_device_attr, max_rdd),
    GET_MEMBER_INFO(ibv_device_attr, max_mw),
    GET_MEMBER_INFO(ibv_device_attr, max_raw_ipv6_qp),
    GET_MEMBER_INFO(ibv_device_attr, max_raw_ethy_qp),
    GET_MEMBER_INFO(ibv_device_attr, max_mcast_grp),
    GET_MEMBER_INFO(ibv_device_attr, max_mcast_qp_attach),
    GET_MEMBER_INFO(ibv_device_attr, max_total_mcast_qp_attach),
    GET_MEMBER_INFO(ibv_device_attr, max_ah),
    GET_MEMBER_INFO(ibv_device_attr, max_fmr),
    GET_MEMBER_INFO(ibv_device_attr, max_map_per_fmr),
    GET_MEMBER_INFO(ibv_device_attr, max_srq),
    GET_MEMBER_INFO(ibv_device_attr, max_srq_wr),
    GET_MEMBER_INFO(ibv_device_attr, max_srq_sge),
    GET_MEMBER_INFO(ibv_device_attr, max_pkeys),
    GET_MEMBER_INFO(ibv_device_attr, local_ca_ack_delay),
    GET_MEMBER_INFO(ibv_device_attr, phys_port_cnt)
};

ReflectionUtility::StructInfo ibv_device_attr_struct_info {
    sizeof(ibv_device_attr),
    sizeof(ibv_device_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_device_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_port_attr_member_infos[] {
    GET_MEMBER_INFO(ibv_port_attr, state),
    GET_MEMBER_INFO(ibv_port_attr, max_mtu),
    GET_MEMBER_INFO(ibv_port_attr, active_mtu),
    GET_MEMBER_INFO(ibv_port_attr, gid_tbl_len),
    GET_MEMBER_INFO(ibv_port_attr, port_cap_flags),
    GET_MEMBER_INFO(ibv_port_attr, max_msg_sz),
    GET_MEMBER_INFO(ibv_port_attr, bad_pkey_cntr),
    GET_MEMBER_INFO(ibv_port_attr, qkey_viol_cntr),
    GET_MEMBER_INFO(ibv_port_attr, pkey_tbl_len),
    GET_MEMBER_INFO(ibv_port_attr, lid),
    GET_MEMBER_INFO(ibv_port_attr, sm_lid),
    GET_MEMBER_INFO(ibv_port_attr, lmc),
    GET_MEMBER_INFO(ibv_port_attr, max_vl_num),
    GET_MEMBER_INFO(ibv_port_attr, sm_sl),
    GET_MEMBER_INFO(ibv_port_attr, subnet_timeout),
    GET_MEMBER_INFO(ibv_port_attr, init_type_reply),
    GET_MEMBER_INFO(ibv_port_attr, active_width),
    GET_MEMBER_INFO(ibv_port_attr, active_speed),
    GET_MEMBER_INFO(ibv_port_attr, phys_state),
    GET_MEMBER_INFO(ibv_port_attr, link_layer),
    GET_MEMBER_INFO(ibv_port_attr, flags),
    GET_MEMBER_INFO(ibv_port_attr, port_cap_flags2),
};

ReflectionUtility::StructInfo ibv_port_attr_struct_info {
    sizeof(ibv_port_attr),
    sizeof(ibv_port_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_port_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_cq_member_infos[] {
    GET_MEMBER_INFO(ibv_cq, context),
    GET_MEMBER_INFO(ibv_cq, channel),
    GET_MEMBER_INFO(ibv_cq, cq_context),
    GET_MEMBER_INFO(ibv_cq, handle),
    GET_MEMBER_INFO(ibv_cq, cqe),
    GET_MEMBER_INFO(ibv_cq, mutex),
    GET_MEMBER_INFO(ibv_cq, cond),
    GET_MEMBER_INFO(ibv_cq, comp_events_completed),
    GET_MEMBER_INFO(ibv_cq, async_events_completed),
};

ReflectionUtility::StructInfo ibv_cq_struct_info {
    sizeof(ibv_cq),
    sizeof(ibv_cq_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_cq_member_infos
};

ReflectionUtility::MemberInfo ibv_wc_member_infos[] {
    GET_MEMBER_INFO(ibv_wc, wr_id),
    GET_MEMBER_INFO(ibv_wc, status),
    GET_MEMBER_INFO(ibv_wc, opcode),
    GET_MEMBER_INFO(ibv_wc, vendor_err),
    GET_MEMBER_INFO(ibv_wc, byte_len),
    GET_MEMBER_INFO(ibv_wc, imm_data),
    GET_MEMBER_INFO(ibv_wc, invalidated_rkey),
    GET_MEMBER_INFO(ibv_wc, qp_num),
    GET_MEMBER_INFO(ibv_wc, wc_flags),
    GET_MEMBER_INFO(ibv_wc, pkey_index),
    GET_MEMBER_INFO(ibv_wc, slid),
    GET_MEMBER_INFO(ibv_wc, sl),
    GET_MEMBER_INFO(ibv_wc, dlid_path_bits)
};

ReflectionUtility::StructInfo ibv_wc_struct_info {
    sizeof(ibv_wc),
    sizeof(ibv_wc_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_wc_member_infos
};

ReflectionUtility::MemberInfo ibv_sge_member_infos[] = {
    GET_MEMBER_INFO(ibv_sge, addr),
    GET_MEMBER_INFO(ibv_sge, length),
    GET_MEMBER_INFO(ibv_sge, lkey)
};

ReflectionUtility::StructInfo ibv_sge_struct_info {
    sizeof(ibv_sge),
    sizeof(ibv_sge_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_sge_member_infos
};

ReflectionUtility::MemberInfo ibv_send_wr_member_infos[] = {
    GET_MEMBER_INFO(ibv_send_wr, wr_id),
    GET_MEMBER_INFO(ibv_send_wr, next),
    GET_MEMBER_INFO(ibv_send_wr, sg_list),
    GET_MEMBER_INFO(ibv_send_wr, num_sge),
    GET_MEMBER_INFO(ibv_send_wr, opcode),
    GET_MEMBER_INFO(ibv_send_wr, send_flags),
    GET_MEMBER_INFO(ibv_send_wr, imm_data),
    GET_MEMBER_INFO(ibv_send_wr, invalidate_rkey),
    GET_MEMBER_INFO(ibv_send_wr, wr.rdma.remote_addr),
    GET_MEMBER_INFO(ibv_send_wr, wr.rdma.rkey),
    GET_MEMBER_INFO(ibv_send_wr, wr.atomic.remote_addr),
    GET_MEMBER_INFO(ibv_send_wr, wr.atomic.compare_add),
    GET_MEMBER_INFO(ibv_send_wr, wr.atomic.swap),
    GET_MEMBER_INFO(ibv_send_wr, wr.atomic.rkey),
    GET_MEMBER_INFO(ibv_send_wr, wr.ud.ah),
    GET_MEMBER_INFO(ibv_send_wr, wr.ud.remote_qpn),
    GET_MEMBER_INFO(ibv_send_wr, wr.ud.remote_qkey),
    GET_MEMBER_INFO(ibv_send_wr, qp_type.xrc.remote_srqn),
    GET_MEMBER_INFO(ibv_send_wr, bind_mw.mw),
    GET_MEMBER_INFO(ibv_send_wr, bind_mw.rkey),
    GET_MEMBER_INFO(ibv_send_wr, bind_mw.bind_info),
    GET_MEMBER_INFO(ibv_send_wr, tso.hdr),
    GET_MEMBER_INFO(ibv_send_wr, tso.hdr_sz),
    GET_MEMBER_INFO(ibv_send_wr, tso.mss)
};

ReflectionUtility::StructInfo ibv_send_wr_struct_info {
    sizeof(ibv_send_wr),
    sizeof(ibv_send_wr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_send_wr_member_infos
};

ReflectionUtility::MemberInfo ibv_recv_wr_member_infos[] = {
    GET_MEMBER_INFO(ibv_recv_wr, wr_id),
    GET_MEMBER_INFO(ibv_recv_wr, next),
    GET_MEMBER_INFO(ibv_recv_wr, sg_list),
    GET_MEMBER_INFO(ibv_recv_wr, num_sge)
};

ReflectionUtility::StructInfo ibv_recv_wr_struct_info {
    sizeof(ibv_recv_wr),
    sizeof(ibv_recv_wr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_recv_wr_member_infos
};

std::unordered_map<std::string, ReflectionUtility::StructInfo*> ReflectionUtility::structInfos {
    {"ibv_device_attr", &ibv_device_attr_struct_info},
    {"ibv_port_attr", &ibv_port_attr_struct_info},
    {"ibv_cq", &ibv_cq_struct_info},
    {"ibv_wc", &ibv_wc_struct_info},
    {"ibv_sge", &ibv_sge_struct_info},
    {"ibv_send_wr", &ibv_send_wr_struct_info},
    {"ibv_recv_wr", &ibv_recv_wr_struct_info}
};

ReflectionUtility::StructInfo *ReflectionUtility::getStructInfo(const std::string& identifier) {
    if (structInfos.find(identifier) == structInfos.end()) {
        return nullptr;
    }

    return structInfos[identifier];
}