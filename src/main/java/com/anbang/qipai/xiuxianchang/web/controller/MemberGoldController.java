package com.anbang.qipai.xiuxianchang.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.web.vo.CommonVO;
import com.dml.accounting.AccountingRecord;
import com.dml.accounting.InsufficientBalanceException;

@RestController
@RequestMapping("/gold")
public class MemberGoldController {

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	/**
	 * 减少玩家金币
	 */
	@RequestMapping(value = "/withdraw")
	public CommonVO withdraw(String memberId, int amount, String textSummary) {
		CommonVO vo = new CommonVO();
		try {
			AccountingRecord rcd = memberGoldCmdService.withdraw(memberId, amount, textSummary,
					System.currentTimeMillis());
			memberGoldQueryService.withdraw(memberId, rcd);
			return vo;
		} catch (InsufficientBalanceException e) {
			vo.setSuccess(false);
			vo.setMsg("InsufficientBalanceException");
			return vo;
		} catch (MemberNotFoundException e) {
			vo.setSuccess(false);
			vo.setMsg("MemberNotFoundException");
			return vo;
		}
	}

	/**
	 * 赠送玩家金币
	 */
	@RequestMapping(value = "/givegoldtomember")
	public CommonVO giveGoldToMember(String memberId, int amount, String textSummary) {
		CommonVO vo = new CommonVO();
		try {
			AccountingRecord rcd = memberGoldCmdService.giveGoldToMember(memberId, amount, textSummary,
					System.currentTimeMillis());
			memberGoldQueryService.withdraw(memberId, rcd);
			return vo;
		} catch (MemberNotFoundException e) {
			vo.setSuccess(false);
			vo.setMsg("MemberNotFoundException");
			return vo;
		}
	}
}
