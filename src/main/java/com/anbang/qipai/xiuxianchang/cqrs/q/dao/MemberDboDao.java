package com.anbang.qipai.xiuxianchang.cqrs.q.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberDbo;

public interface MemberDboDao {

	void insert(MemberDbo dbo);

	MemberDbo findByMemberId(String memberId);

	List<MemberDbo> findAllMembers();

	void updateMemberBaseInfo(String memberId, String nickname, String headimgurl, String gender);
}
