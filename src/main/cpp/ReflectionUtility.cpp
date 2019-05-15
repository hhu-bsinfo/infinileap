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

std::unordered_map<std::string, ReflectionUtility::StructInfo*> ReflectionUtility::structInfos {
    {"ibv_device_attr", &ibv_device_attr_struct_info},
    {"ibv_port_attr", &ibv_port_attr_struct_info}
};

ReflectionUtility::StructInfo *ReflectionUtility::getStructInfo(const std::string& identifier) {
    if (structInfos.find(identifier) == structInfos.end()) {
        return nullptr;
    }

    return structInfos[identifier];
}
