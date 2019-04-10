package com.anbang.qipai.xiuxianchang.cqrs.q.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldRecordDbo;

public interface MemberGoldRecordDboDao {

	void save(MemberGoldRecordDbo dbo);

	long getCountByMemberId(String memberId);

	long countByMemberIdAndSummaryAndTime(String memberId, String summary, long startTime, long endTime);

	List<MemberGoldRecordDbo> findMemberGoldRecordByMemberId(String memberId, int page, int size);
}
