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

ReflectionUtility::MemberInfo ibv_async_event_member_infos[] = {
    GET_MEMBER_INFO(ibv_async_event, element.cq),
    GET_MEMBER_INFO(ibv_async_event, element.qp),
    GET_MEMBER_INFO(ibv_async_event, element.srq),
    GET_MEMBER_INFO(ibv_async_event, element.wq),
    GET_MEMBER_INFO(ibv_async_event, element.port_num),
    GET_MEMBER_INFO(ibv_async_event, event_type)
};

ReflectionUtility::StructInfo ibv_async_event_struct_info {
    sizeof(ibv_async_event),
    sizeof(ibv_async_event_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_async_event_member_infos
};

ReflectionUtility::MemberInfo ibv_comp_channel_member_infos[] = {
    GET_MEMBER_INFO(ibv_comp_channel, context),
    GET_MEMBER_INFO(ibv_comp_channel, fd),
    GET_MEMBER_INFO(ibv_comp_channel, refcnt)
};

ReflectionUtility::StructInfo ibv_comp_channel_struct_info {
    sizeof(ibv_comp_channel),
    sizeof(ibv_comp_channel_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_comp_channel_member_infos
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

ReflectionUtility::MemberInfo ibv_wc_member_infos[] = {
    GET_MEMBER_INFO(ibv_wc, wr_id),
    GET_MEMBER_INFO(ibv_wc, status),
    GET_MEMBER_INFO(ibv_wc, opcode),
    GET_MEMBER_INFO(ibv_wc, vendor_err),
    GET_MEMBER_INFO(ibv_wc, byte_len),
    GET_MEMBER_INFO(ibv_wc, imm_data),
    GET_MEMBER_INFO(ibv_wc, invalidated_rkey),
    GET_MEMBER_INFO(ibv_wc, qp_num),
    GET_MEMBER_INFO(ibv_wc, src_qp),
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

ReflectionUtility::MemberInfo ibv_srq_init_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_srq_init_attr, srq_context),
    GET_MEMBER_INFO(ibv_srq_init_attr, attr)
};

ReflectionUtility::StructInfo ibv_srq_init_attr_struct_info {
    sizeof(ibv_srq_init_attr),
    sizeof(ibv_srq_init_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_srq_init_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_srq_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_srq_attr, max_wr),
    GET_MEMBER_INFO(ibv_srq_attr, max_sge),
    GET_MEMBER_INFO(ibv_srq_attr, srq_limit)
};

ReflectionUtility::StructInfo ibv_srq_attr_struct_info {
    sizeof(ibv_srq_attr),
    sizeof(ibv_srq_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_srq_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_qp_init_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_qp_init_attr, qp_context),
    GET_MEMBER_INFO(ibv_qp_init_attr, send_cq),
    GET_MEMBER_INFO(ibv_qp_init_attr, recv_cq),
    GET_MEMBER_INFO(ibv_qp_init_attr, srq),
    GET_MEMBER_INFO(ibv_qp_init_attr, cap),
    GET_MEMBER_INFO(ibv_qp_init_attr, qp_type),
    GET_MEMBER_INFO(ibv_qp_init_attr, sq_sig_all)
};

ReflectionUtility::StructInfo ibv_qp_init_attr_struct_info {
    sizeof(ibv_qp_init_attr),
    sizeof(ibv_qp_init_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_qp_init_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_srq_member_infos[] = {
    GET_MEMBER_INFO(ibv_srq, context),
    GET_MEMBER_INFO(ibv_srq, srq_context),
    GET_MEMBER_INFO(ibv_srq, pd),
    GET_MEMBER_INFO(ibv_srq, handle),
    GET_MEMBER_INFO(ibv_srq, mutex),
    GET_MEMBER_INFO(ibv_srq, cond),
    GET_MEMBER_INFO(ibv_srq, events_completed)
};

ReflectionUtility::StructInfo ibv_srq_struct_info {
    sizeof(ibv_srq),
    sizeof(ibv_srq_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_srq_member_infos
};

ReflectionUtility::MemberInfo ibv_ah_member_infos[] = {
    GET_MEMBER_INFO(ibv_ah, context),
    GET_MEMBER_INFO(ibv_ah, pd),
    GET_MEMBER_INFO(ibv_ah, handle)
};

ReflectionUtility::StructInfo ibv_ah_struct_info {
    sizeof(ibv_ah),
    sizeof(ibv_ah_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_ah_member_infos
};

ReflectionUtility::MemberInfo ibv_ah_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_ah_attr, grh),
    GET_MEMBER_INFO(ibv_ah_attr, dlid),
    GET_MEMBER_INFO(ibv_ah_attr, sl),
    GET_MEMBER_INFO(ibv_ah_attr, src_path_bits),
    GET_MEMBER_INFO(ibv_ah_attr, static_rate),
    GET_MEMBER_INFO(ibv_ah_attr, is_global),
    GET_MEMBER_INFO(ibv_ah_attr, port_num)
};

ReflectionUtility::StructInfo ibv_ah_attr_struct_info {
    sizeof(ibv_ah_attr),
    sizeof(ibv_ah_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_ah_attr_member_infos
};


ReflectionUtility::MemberInfo ibv_global_route_member_infos[] = {
    GET_MEMBER_INFO(ibv_global_route, dgid),
    GET_MEMBER_INFO(ibv_global_route, flow_label),
    GET_MEMBER_INFO(ibv_global_route, sgid_index),
    GET_MEMBER_INFO(ibv_global_route, hop_limit),
    GET_MEMBER_INFO(ibv_global_route, traffic_class)
};

ReflectionUtility::StructInfo ibv_global_route_struct_info {
    sizeof(ibv_global_route),
    sizeof(ibv_global_route_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_global_route_member_infos
};

ReflectionUtility::MemberInfo ibv_qp_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_qp_attr, qp_state),
    GET_MEMBER_INFO(ibv_qp_attr, cur_qp_state),
    GET_MEMBER_INFO(ibv_qp_attr, path_mtu),
    GET_MEMBER_INFO(ibv_qp_attr, path_mig_state),
    GET_MEMBER_INFO(ibv_qp_attr, qkey),
    GET_MEMBER_INFO(ibv_qp_attr, rq_psn),
    GET_MEMBER_INFO(ibv_qp_attr, sq_psn),
    GET_MEMBER_INFO(ibv_qp_attr, dest_qp_num),
    GET_MEMBER_INFO(ibv_qp_attr, qp_access_flags),
    GET_MEMBER_INFO(ibv_qp_attr, cap),
    GET_MEMBER_INFO(ibv_qp_attr, ah_attr),
    GET_MEMBER_INFO(ibv_qp_attr, alt_ah_attr),
    GET_MEMBER_INFO(ibv_qp_attr, pkey_index),
    GET_MEMBER_INFO(ibv_qp_attr, alt_pkey_index),
    GET_MEMBER_INFO(ibv_qp_attr, en_sqd_async_notify),
    GET_MEMBER_INFO(ibv_qp_attr, sq_draining),
    GET_MEMBER_INFO(ibv_qp_attr, max_rd_atomic),
    GET_MEMBER_INFO(ibv_qp_attr, max_dest_rd_atomic),
    GET_MEMBER_INFO(ibv_qp_attr, min_rnr_timer),
    GET_MEMBER_INFO(ibv_qp_attr, port_num),
    GET_MEMBER_INFO(ibv_qp_attr, timeout),
    GET_MEMBER_INFO(ibv_qp_attr, retry_cnt),
    GET_MEMBER_INFO(ibv_qp_attr, rnr_retry),
    GET_MEMBER_INFO(ibv_qp_attr, alt_port_num),
    GET_MEMBER_INFO(ibv_qp_attr, alt_timeout),
    GET_MEMBER_INFO(ibv_qp_attr, rate_limit)
};

ReflectionUtility::StructInfo ibv_qp_attr_struct_info {
    sizeof(ibv_qp_attr),
    sizeof(ibv_qp_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_qp_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_qp_cap_member_infos[] = {
    GET_MEMBER_INFO(ibv_qp_cap, max_send_wr),
    GET_MEMBER_INFO(ibv_qp_cap, max_recv_wr),
    GET_MEMBER_INFO(ibv_qp_cap, max_send_sge),
    GET_MEMBER_INFO(ibv_qp_cap, max_recv_sge),
    GET_MEMBER_INFO(ibv_qp_cap, max_inline_data)
};

ReflectionUtility::StructInfo ibv_qp_cap_struct_info {
    sizeof(ibv_qp_cap),
    sizeof(ibv_qp_cap_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_qp_cap_member_infos
};

ReflectionUtility::MemberInfo ibv_qp_member_infos[] = {
    GET_MEMBER_INFO(ibv_qp, context),
    GET_MEMBER_INFO(ibv_qp, qp_context),
    GET_MEMBER_INFO(ibv_qp, pd),
    GET_MEMBER_INFO(ibv_qp, send_cq),
    GET_MEMBER_INFO(ibv_qp, recv_cq),
    GET_MEMBER_INFO(ibv_qp, srq),
    GET_MEMBER_INFO(ibv_qp, handle),
    GET_MEMBER_INFO(ibv_qp, qp_num),
    GET_MEMBER_INFO(ibv_qp, state),
    GET_MEMBER_INFO(ibv_qp, qp_type),
    GET_MEMBER_INFO(ibv_qp, mutex),
    GET_MEMBER_INFO(ibv_qp, cond),
    GET_MEMBER_INFO(ibv_qp, events_completed)
};

ReflectionUtility::StructInfo ibv_qp_struct_info {
    sizeof(ibv_qp),
    sizeof(ibv_qp_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_qp_member_infos
};

ReflectionUtility::MemberInfo ibv_dm_member_infos[] = {
    GET_MEMBER_INFO(ibv_dm, context),
    GET_MEMBER_INFO(ibv_dm, comp_mask)
};

ReflectionUtility::StructInfo ibv_dm_struct_info {
    sizeof(ibv_dm),
    sizeof(ibv_dm_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_dm_member_infos
};

ReflectionUtility::MemberInfo ibv_alloc_dm_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_alloc_dm_attr, length),
    GET_MEMBER_INFO(ibv_alloc_dm_attr, log_align_req),
    GET_MEMBER_INFO(ibv_alloc_dm_attr, comp_mask)
};

ReflectionUtility::StructInfo ibv_alloc_dm_attr_struct_info {
    sizeof(ibv_alloc_dm_attr),
    sizeof(ibv_alloc_dm_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_alloc_dm_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_mr_member_infos[] = {
    GET_MEMBER_INFO(ibv_mr, context),
    GET_MEMBER_INFO(ibv_mr, pd),
    GET_MEMBER_INFO(ibv_mr, addr),
    GET_MEMBER_INFO(ibv_mr, length),
    GET_MEMBER_INFO(ibv_mr, handle),
    GET_MEMBER_INFO(ibv_mr, lkey),
    GET_MEMBER_INFO(ibv_mr, rkey)
};

ReflectionUtility::StructInfo ibv_mr_struct_info {
    sizeof(ibv_mr),
    sizeof(ibv_mr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_mr_member_infos
};

ReflectionUtility::MemberInfo ibv_mw_member_infos[] = {
    GET_MEMBER_INFO(ibv_mw, context),
    GET_MEMBER_INFO(ibv_mw, pd),
    GET_MEMBER_INFO(ibv_mw, rkey),
    GET_MEMBER_INFO(ibv_mw, handle),
    GET_MEMBER_INFO(ibv_mw, type)
};

ReflectionUtility::StructInfo ibv_mw_struct_info {
    sizeof(ibv_mw),
    sizeof(ibv_mw_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_mw_member_infos
};

ReflectionUtility::MemberInfo ibv_mw_bind_member_infos[] = {
    GET_MEMBER_INFO(ibv_mw_bind, wr_id),
    GET_MEMBER_INFO(ibv_mw_bind, send_flags),
    GET_MEMBER_INFO(ibv_mw_bind, bind_info)
};

ReflectionUtility::StructInfo ibv_mw_bind_struct_info {
    sizeof(ibv_mw_bind),
    sizeof(ibv_mw_bind_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_mw_bind_member_infos
};

ReflectionUtility::MemberInfo ibv_mw_bind_info_member_infos[] = {
    GET_MEMBER_INFO(ibv_mw_bind_info, mr),
    GET_MEMBER_INFO(ibv_mw_bind_info, addr),
    GET_MEMBER_INFO(ibv_mw_bind_info, length),
    GET_MEMBER_INFO(ibv_mw_bind_info, mw_access_flags)
};

ReflectionUtility::StructInfo ibv_mw_bind_info_struct_info {
    sizeof(ibv_mw_bind_info),
    sizeof(ibv_mw_bind_info_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_mw_bind_info_member_infos
};

ReflectionUtility::MemberInfo ibv_pd_member_infos[] = {
    GET_MEMBER_INFO(ibv_pd, context),
    GET_MEMBER_INFO(ibv_pd, handle)
};

ReflectionUtility::StructInfo ibv_pd_struct_info {
    sizeof(ibv_pd),
    sizeof(ibv_pd_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_pd_member_infos
};

ReflectionUtility::MemberInfo ibv_td_member_infos[] = {
    GET_MEMBER_INFO(ibv_td, context)
};

ReflectionUtility::StructInfo ibv_td_struct_info {
    sizeof(ibv_td),
    sizeof(ibv_td_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_td_member_infos
};

ReflectionUtility::MemberInfo ibv_td_init_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_td_init_attr, comp_mask)
};

ReflectionUtility::StructInfo ibv_td_init_attr_struct_info {
    sizeof(ibv_td_init_attr),
    sizeof(ibv_td_init_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_td_init_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_parent_domain_init_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_parent_domain_init_attr, pd),
    GET_MEMBER_INFO(ibv_parent_domain_init_attr, td),
    GET_MEMBER_INFO(ibv_parent_domain_init_attr, comp_mask)
};

ReflectionUtility::StructInfo ibv_parent_domain_init_attr_struct_info {
    sizeof(ibv_parent_domain_init_attr),
    sizeof(ibv_parent_domain_init_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_parent_domain_init_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_device_attr_ex_member_infos[] = {
    GET_MEMBER_INFO(ibv_device_attr_ex, orig_attr),
    GET_MEMBER_INFO(ibv_device_attr_ex, comp_mask),
    GET_MEMBER_INFO(ibv_device_attr_ex, odp_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, completion_timestamp_mask),
    GET_MEMBER_INFO(ibv_device_attr_ex, hca_core_clock),
    GET_MEMBER_INFO(ibv_device_attr_ex, device_cap_flags_ex),
    GET_MEMBER_INFO(ibv_device_attr_ex, tso_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, rss_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, max_wq_type_rq),
    GET_MEMBER_INFO(ibv_device_attr_ex, packet_pacing_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, raw_packet_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, tm_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, cq_mod_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, max_dm_size),
    GET_MEMBER_INFO(ibv_device_attr_ex, pci_atomic_caps),
    GET_MEMBER_INFO(ibv_device_attr_ex, xrc_odp_caps)
};

ReflectionUtility::StructInfo ibv_device_attr_ex_struct_info {
    sizeof(ibv_device_attr_ex),
    sizeof(ibv_device_attr_ex_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_device_attr_ex_member_infos
};

ReflectionUtility::MemberInfo ibv_odp_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_odp_caps, general_caps),
    GET_MEMBER_INFO(ibv_odp_caps, per_transport_caps.rc_odp_caps),
    GET_MEMBER_INFO(ibv_odp_caps, per_transport_caps.uc_odp_caps),
    GET_MEMBER_INFO(ibv_odp_caps, per_transport_caps.ud_odp_caps)
};

ReflectionUtility::StructInfo ibv_odp_caps_struct_info {
    sizeof(ibv_odp_caps),
    sizeof(ibv_odp_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_odp_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_tso_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_tso_caps, max_tso),
    GET_MEMBER_INFO(ibv_tso_caps, supported_qpts)
};

ReflectionUtility::StructInfo ibv_tso_caps_struct_info {
    sizeof(ibv_tso_caps),
    sizeof(ibv_tso_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_tso_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_rss_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_rss_caps, supported_qpts),
    GET_MEMBER_INFO(ibv_rss_caps, max_rwq_indirection_tables),
    GET_MEMBER_INFO(ibv_rss_caps, max_rwq_indirection_table_size),
    GET_MEMBER_INFO(ibv_rss_caps, rx_hash_fields_mask),
    GET_MEMBER_INFO(ibv_rss_caps, rx_hash_function)
};

ReflectionUtility::StructInfo ibv_rss_caps_struct_info {
    sizeof(ibv_rss_caps),
    sizeof(ibv_rss_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_rss_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_packet_pacing_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_packet_pacing_caps, qp_rate_limit_min),
    GET_MEMBER_INFO(ibv_packet_pacing_caps, qp_rate_limit_max),
    GET_MEMBER_INFO(ibv_packet_pacing_caps, supported_qpts)
};

ReflectionUtility::StructInfo ibv_packet_pacing_caps_struct_info {
    sizeof(ibv_packet_pacing_caps),
    sizeof(ibv_packet_pacing_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_packet_pacing_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_tm_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_tm_caps, max_rndv_hdr_size),
    GET_MEMBER_INFO(ibv_tm_caps, max_num_tags),
    GET_MEMBER_INFO(ibv_tm_caps, flags),
    GET_MEMBER_INFO(ibv_tm_caps, max_ops),
    GET_MEMBER_INFO(ibv_tm_caps, max_sge)
};

ReflectionUtility::StructInfo ibv_tm_caps_struct_info {
    sizeof(ibv_tm_caps),
    sizeof(ibv_tm_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_tm_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_cq_moderation_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_cq_moderation_caps, max_cq_count),
    GET_MEMBER_INFO(ibv_cq_moderation_caps, max_cq_period)
};

ReflectionUtility::StructInfo ibv_cq_moderation_caps_struct_info {
    sizeof(ibv_cq_moderation_caps),
    sizeof(ibv_cq_moderation_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_cq_moderation_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_pci_atomic_caps_member_infos[] = {
    GET_MEMBER_INFO(ibv_pci_atomic_caps, fetch_add),
    GET_MEMBER_INFO(ibv_pci_atomic_caps, swap),
    GET_MEMBER_INFO(ibv_pci_atomic_caps, compare_swap)
};

ReflectionUtility::StructInfo ibv_pci_atomic_caps_struct_info {
    sizeof(ibv_pci_atomic_caps),
    sizeof(ibv_pci_atomic_caps_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_pci_atomic_caps_member_infos
};

ReflectionUtility::MemberInfo ibv_query_device_ex_input_member_infos[] = {
    GET_MEMBER_INFO(ibv_query_device_ex_input, comp_mask)
};

ReflectionUtility::StructInfo ibv_query_device_ex_input_struct_info {
    sizeof(ibv_query_device_ex_input),
    sizeof(ibv_query_device_ex_input_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_query_device_ex_input_member_infos
};

ReflectionUtility::MemberInfo ibv_xrcd_member_infos[] = {
    GET_MEMBER_INFO(ibv_xrcd, context)
};

ReflectionUtility::StructInfo ibv_xrcd_struct_info {
    sizeof(ibv_xrcd),
    sizeof(ibv_xrcd_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_xrcd_member_infos
};

ReflectionUtility::MemberInfo ibv_xrcd_init_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_xrcd_init_attr, comp_mask),
    GET_MEMBER_INFO(ibv_xrcd_init_attr, fd),
    GET_MEMBER_INFO(ibv_xrcd_init_attr, oflags)
};

ReflectionUtility::StructInfo ibv_xrcd_init_attr_struct_info {
    sizeof(ibv_xrcd_init_attr),
    sizeof(ibv_xrcd_init_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_xrcd_init_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_cq_ex_member_infos[] = {
    GET_MEMBER_INFO(ibv_cq_ex, context),
    GET_MEMBER_INFO(ibv_cq_ex, channel),
    GET_MEMBER_INFO(ibv_cq_ex, cq_context),
    GET_MEMBER_INFO(ibv_cq_ex, handle),
    GET_MEMBER_INFO(ibv_cq_ex, cqe),
    GET_MEMBER_INFO(ibv_cq_ex, mutex),
    GET_MEMBER_INFO(ibv_cq_ex, cond),
    GET_MEMBER_INFO(ibv_cq_ex, comp_events_completed),
    GET_MEMBER_INFO(ibv_cq_ex, async_events_completed),
    GET_MEMBER_INFO(ibv_cq_ex, comp_mask),
    GET_MEMBER_INFO(ibv_cq_ex, status),
    GET_MEMBER_INFO(ibv_cq_ex, wr_id)
};

ReflectionUtility::StructInfo ibv_cq_ex_struct_info {
    sizeof(ibv_cq_ex),
    sizeof(ibv_cq_ex_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_cq_ex_member_infos
};

ReflectionUtility::MemberInfo ibv_srq_init_attr_ex_member_infos[] = {
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, srq_context),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, attr),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, comp_mask),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, srq_type),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, pd),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, xrcd),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, cq),
    GET_MEMBER_INFO(ibv_srq_init_attr_ex, tm_cap)
};

ReflectionUtility::StructInfo ibv_srq_init_attr_ex_struct_info {
    sizeof(ibv_srq_init_attr_ex),
    sizeof(ibv_srq_init_attr_ex_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_srq_init_attr_ex_member_infos
};

ReflectionUtility::MemberInfo ibv_tm_cap_member_infos[] = {
    GET_MEMBER_INFO(ibv_tm_cap, max_num_tags),
    GET_MEMBER_INFO(ibv_tm_cap, max_ops)
};

ReflectionUtility::StructInfo ibv_tm_cap_struct_info {
    sizeof(ibv_tm_cap),
    sizeof(ibv_tm_cap_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_tm_cap_member_infos
};

ReflectionUtility::MemberInfo ibv_cq_init_attr_ex_member_infos[] = {
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, cqe),
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, cq_context),
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, channel),
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, comp_vector),
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, wc_flags),
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, comp_mask),
    GET_MEMBER_INFO(ibv_cq_init_attr_ex, flags)
};

ReflectionUtility::StructInfo ibv_cq_init_attr_ex_struct_info {
    sizeof(ibv_cq_init_attr_ex),
    sizeof(ibv_cq_init_attr_ex_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_cq_init_attr_ex_member_infos
};

ReflectionUtility::MemberInfo ibv_poll_cq_attr_member_infos[] = {
    GET_MEMBER_INFO(ibv_poll_cq_attr, comp_mask)
};

ReflectionUtility::StructInfo ibv_poll_cq_attr_struct_info {
    sizeof(ibv_poll_cq_attr),
    sizeof(ibv_poll_cq_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_poll_cq_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_qp_ex_member_infos[] = {
    GET_MEMBER_INFO(ibv_qp_ex, qp_base),
    GET_MEMBER_INFO(ibv_qp_ex, comp_mask),
    GET_MEMBER_INFO(ibv_qp_ex, wr_id),
    GET_MEMBER_INFO(ibv_qp_ex, wr_flags)
};

ReflectionUtility::StructInfo ibv_qp_ex_struct_info {
    sizeof(ibv_qp_ex),
    sizeof(ibv_qp_ex_member_infos) / sizeof(ReflectionUtility::MemberInfo),
    ibv_qp_ex_member_infos
};

ReflectionUtility::MemberInfo ibv_wq_member_infos[] = {
        GET_MEMBER_INFO(ibv_wq, context),
        GET_MEMBER_INFO(ibv_wq, wq_context),
        GET_MEMBER_INFO(ibv_wq, pd),
        GET_MEMBER_INFO(ibv_wq, cq),
        GET_MEMBER_INFO(ibv_wq, wq_num),
        GET_MEMBER_INFO(ibv_wq, handle),
        GET_MEMBER_INFO(ibv_wq, state),
        GET_MEMBER_INFO(ibv_wq, wq_type),
        GET_MEMBER_INFO(ibv_wq, mutex),
        GET_MEMBER_INFO(ibv_wq, cond),
        GET_MEMBER_INFO(ibv_wq, events_completed),
        GET_MEMBER_INFO(ibv_wq, comp_mask)
};

ReflectionUtility::StructInfo ibv_wq_struct_info {
        sizeof(ibv_wq),
        sizeof(ibv_wq_member_infos) / sizeof(ReflectionUtility::MemberInfo),
        ibv_wq_member_infos
};

ReflectionUtility::MemberInfo ibv_wq_init_attr_member_infos[] = {
        GET_MEMBER_INFO(ibv_wq_init_attr, wq_context),
        GET_MEMBER_INFO(ibv_wq_init_attr, wq_type),
        GET_MEMBER_INFO(ibv_wq_init_attr, max_wr),
        GET_MEMBER_INFO(ibv_wq_init_attr, max_sge),
        GET_MEMBER_INFO(ibv_wq_init_attr, pd),
        GET_MEMBER_INFO(ibv_wq_init_attr, cq),
        GET_MEMBER_INFO(ibv_wq_init_attr, comp_mask),
        GET_MEMBER_INFO(ibv_wq_init_attr, create_flags)
};

ReflectionUtility::StructInfo ibv_wq_init_attr_struct_info {
        sizeof(ibv_wq_init_attr),
        sizeof(ibv_wq_init_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
        ibv_wq_init_attr_member_infos
};

ReflectionUtility::MemberInfo ibv_wq_attr_member_infos[] = {
        GET_MEMBER_INFO(ibv_wq_attr, attr_mask),
        GET_MEMBER_INFO(ibv_wq_attr, wq_state),
        GET_MEMBER_INFO(ibv_wq_attr, curr_wq_state),
        GET_MEMBER_INFO(ibv_wq_attr, flags),
        GET_MEMBER_INFO(ibv_wq_attr, flags_mask)
};

ReflectionUtility::StructInfo ibv_wq_attr_struct_info {
        sizeof(ibv_wq_attr),
        sizeof(ibv_wq_attr_member_infos) / sizeof(ReflectionUtility::MemberInfo),
        ibv_wq_attr_member_infos
};

std::unordered_map<std::string, ReflectionUtility::StructInfo*> ReflectionUtility::structInfos {
    {"ibv_device_attr", &ibv_device_attr_struct_info},
    {"ibv_port_attr", &ibv_port_attr_struct_info},
    {"ibv_async_event", &ibv_async_event_struct_info},
    {"ibv_comp_channel", &ibv_comp_channel_struct_info},
    {"ibv_cq", &ibv_cq_struct_info},
    {"ibv_wc", &ibv_wc_struct_info},
    {"ibv_sge", &ibv_sge_struct_info},
    {"ibv_send_wr", &ibv_send_wr_struct_info},
    {"ibv_recv_wr", &ibv_recv_wr_struct_info},
    {"ibv_srq_init_attr", &ibv_srq_init_attr_struct_info},
    {"ibv_srq_attr", &ibv_srq_attr_struct_info},
    {"ibv_srq", &ibv_srq_struct_info},
    {"ibv_ah", &ibv_ah_struct_info},
    {"ibv_ah_attr", &ibv_ah_attr_struct_info},
    {"ibv_global_route", &ibv_global_route_struct_info},
    {"ibv_qp_init_attr", &ibv_qp_init_attr_struct_info},
    {"ibv_qp_attr", &ibv_qp_attr_struct_info},
    {"ibv_qp_cap", &ibv_qp_cap_struct_info},
    {"ibv_qp", &ibv_qp_struct_info},
    {"ibv_dm", &ibv_dm_struct_info},
    {"ibv_alloc_dm_attr", &ibv_alloc_dm_attr_struct_info},
    {"ibv_mr", &ibv_mr_struct_info},
    {"ibv_mw", &ibv_mw_struct_info},
    {"ibv_mw_bind", &ibv_mw_bind_struct_info},
    {"ibv_mw_bind_info", &ibv_mw_bind_info_struct_info},
    {"ibv_pd", &ibv_pd_struct_info},
    {"ibv_td", &ibv_td_struct_info},
    {"ibv_td_init_attr", &ibv_td_init_attr_struct_info},
    {"ibv_parent_domain_init_attr", &ibv_parent_domain_init_attr_struct_info},
    {"ibv_device_attr_ex", &ibv_device_attr_ex_struct_info},
    {"ibv_odp_caps", &ibv_odp_caps_struct_info},
    {"ibv_tso_caps", &ibv_tso_caps_struct_info},
    {"ibv_rss_caps", &ibv_rss_caps_struct_info},
    {"ibv_packet_pacing_caps", &ibv_packet_pacing_caps_struct_info},
    {"ibv_tm_caps", &ibv_tm_caps_struct_info},
    {"ibv_cq_moderation_caps", &ibv_cq_moderation_caps_struct_info},
    {"ibv_pci_atomic_caps", &ibv_pci_atomic_caps_struct_info},
    {"ibv_query_device_ex_input", &ibv_query_device_ex_input_struct_info},
    {"ibv_xrcd", &ibv_xrcd_struct_info},
    {"ibv_xrcd_init_attr", &ibv_xrcd_init_attr_struct_info},
    {"ibv_srq_init_attr_ex", &ibv_srq_init_attr_ex_struct_info},
    {"ibv_tm_cap", &ibv_tm_cap_struct_info},
    {"ibv_cq_ex", &ibv_cq_ex_struct_info},
    {"ibv_cq_init_attr_ex", &ibv_cq_init_attr_ex_struct_info},
    {"ibv_poll_cq_attr", &ibv_poll_cq_attr_struct_info},
    {"ibv_wq", &ibv_wq_struct_info},
    {"ibv_wq_init_attr", &ibv_wq_init_attr_struct_info},
    {"ibv_wq_attr", &ibv_wq_attr_struct_info},
    {"ibv_qp_ex", &ibv_qp_ex_struct_info},
};

ReflectionUtility::StructInfo *ReflectionUtility::getStructInfo(const std::string& identifier) {
    if (structInfos.find(identifier) == structInfos.end()) {
        return nullptr;
    }

    return structInfos[identifier];
}
