package com.anbang.qipai.xiuxianchang.cqrs.q.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.q.dao.MemberDboDao;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberDbo;

@Component
public class MemberAuthQueryService {

	@Autowired
	private MemberDboDao memberDboDao;

	public void updateMemberBaseInfo(MemberDbo member) {
		memberDboDao.updateMemberBaseInfo(member.getId(), member.getNickname(), member.getHeadimgurl(),
				member.getGender());
	}

	public void insertMember(MemberDbo member) {
		memberDboDao.insert(member);
	}

	public MemberDbo findByMemberId(String memberId) {
		return memberDboDao.findByMemberId(memberId);
	}

	public List<MemberDbo> findAllMembers() {
		return memberDboDao.findAllMembers();
	}
}
