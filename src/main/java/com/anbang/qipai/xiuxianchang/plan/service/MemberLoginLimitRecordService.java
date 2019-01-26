package com.anbang.qipai.xiuxianchang.plan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anbang.qipai.xiuxianchang.plan.bean.MemberLoginLimitRecord;
import com.anbang.qipai.xiuxianchang.plan.dao.MemberLoginLimitRecordDao;

@Service
public class MemberLoginLimitRecordService {

	@Autowired
	private MemberLoginLimitRecordDao memberLoginLimitRecordDao;

	public void save(MemberLoginLimitRecord record) {
		memberLoginLimitRecordDao.save(record);
	}

	public MemberLoginLimitRecord findByMemberId(String memberId, boolean efficient) {
		return memberLoginLimitRecordDao.findByMemberId(memberId, efficient);
	}

	public void updateMemberLoginLimitRecordEfficientById(String[] ids, boolean efficient) {
		memberLoginLimitRecordDao.updateMemberLoginLimitRecordEfficientById(ids, efficient);
	}

}
