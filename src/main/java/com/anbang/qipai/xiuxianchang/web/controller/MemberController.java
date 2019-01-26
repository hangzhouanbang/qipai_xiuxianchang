package com.anbang.qipai.xiuxianchang.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.CreateGoldAccountResult;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.MemberHasGoldAccountAlreadyException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberDbo;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldAccountDbo;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberAuthQueryService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberLoginLimitRecord;
import com.anbang.qipai.xiuxianchang.plan.service.MemberAuthService;
import com.anbang.qipai.xiuxianchang.plan.service.MemberLoginLimitRecordService;
import com.anbang.qipai.xiuxianchang.web.vo.CommonVO;

@RestController
@RequestMapping("/member")
public class MemberController {

	@Autowired
	private MemberAuthService memberAuthService;

	@Autowired
	private MemberLoginLimitRecordService memberLoginLimitRecordService;

	@Autowired
	private MemberAuthQueryService memberAuthQueryService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	/**
	 * 查询玩家金币
	 */
	@RequestMapping("/account_info")
	public CommonVO accountInfo(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		Map data = new HashMap<>();
		vo.setData(data);
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account != null) {
			data.put("account", account.getBalance());
		} else {
			data.put("account", 0);
		}
		return vo;
	}

	@RequestMapping("/init")
	public CommonVO init() {
		CommonVO vo = new CommonVO();
		List<MemberDbo> memberList = memberAuthQueryService.findAllMembers();
		for (MemberDbo member : memberList) {
			// 创建账户
			try {
				CreateGoldAccountResult result = memberGoldCmdService.createGoldAccountForMember(member.getId(), 20000,
						"new member", System.currentTimeMillis());
				memberGoldQueryService.createMemberGoldAccount(result);
			} catch (MemberHasGoldAccountAlreadyException e) {
				e.printStackTrace();
			} catch (MemberNotFoundException e) {
				e.printStackTrace();
			}
		}
		return vo;
	}
}
