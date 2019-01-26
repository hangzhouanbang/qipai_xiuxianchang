package com.anbang.qipai.xiuxianchang.cqrs.q.dao;

import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldAccountDbo;

public interface MemberGoldAccountDboDao {

	void save(MemberGoldAccountDbo accountDbo);

	void update(String id, int balance);

	MemberGoldAccountDbo findByMemberId(String memberId);

}
