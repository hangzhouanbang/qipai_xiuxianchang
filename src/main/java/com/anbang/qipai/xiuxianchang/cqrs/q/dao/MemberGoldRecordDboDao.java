package com.anbang.qipai.xiuxianchang.cqrs.q.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldRecordDbo;

public interface MemberGoldRecordDboDao {

	void save(MemberGoldRecordDbo dbo);

	long getCountByMemberId(String memberId);

	List<MemberGoldRecordDbo> findMemberGoldRecordByMemberId(String memberId, int page, int size);
}
