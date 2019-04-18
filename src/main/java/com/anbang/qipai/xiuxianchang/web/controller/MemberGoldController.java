package com.anbang.qipai.xiuxianchang.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldRecordDbo;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.service.MemberXiuxianchangGoldMsgService;
import com.anbang.qipai.xiuxianchang.plan.service.MemberAuthService;
import com.anbang.qipai.xiuxianchang.web.vo.CommonVO;
import com.dml.accounting.AccountingRecord;
import com.dml.accounting.InsufficientBalanceException;

@RestController
@RequestMapping("/gold")
public class MemberGoldController {

	@Autowired
	private MemberAuthService memberAuthService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	@Autowired
	private MemberXiuxianchangGoldMsgService memberXiuxianchangGoldMsgService;

	/**
	 * 玩家申请金币补偿
	 */
	@RequestMapping(value = "/compensation")
	public CommonVO compensation(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		if (memberGoldQueryService.countCompensationByMemberId(memberId) >= 3) {
			vo.setSuccess(false);
			vo.setMsg("has no compensation");
			return vo;
		}
		try {
			AccountingRecord rcd = memberGoldCmdService.giveGoldToMember(memberId, 1500, "compensation for everyday",
					System.currentTimeMillis());
			MemberGoldRecordDbo dbo = memberGoldQueryService.withdraw(memberId, rcd);
			memberXiuxianchangGoldMsgService.withdraw(dbo);
			return vo;
		} catch (MemberNotFoundException e) {
			vo.setSuccess(false);
			vo.setMsg("MemberNotFoundException");
			return vo;
		}
	}

	/**
	 * 减少玩家金币
	 */
	@RequestMapping(value = "/withdraw")
	public CommonVO withdraw(String memberId, int amount, String textSummary) {
		CommonVO vo = new CommonVO();
		try {
			AccountingRecord rcd = memberGoldCmdService.withdraw(memberId, amount, textSummary,
					System.currentTimeMillis());
			MemberGoldRecordDbo dbo = memberGoldQueryService.withdraw(memberId, rcd);
			memberXiuxianchangGoldMsgService.withdraw(dbo);
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
			MemberGoldRecordDbo dbo = memberGoldQueryService.withdraw(memberId, rcd);
			memberXiuxianchangGoldMsgService.withdraw(dbo);
			return vo;
		} catch (MemberNotFoundException e) {
			vo.setSuccess(false);
			vo.setMsg("MemberNotFoundException");
			return vo;
		}
	}

	/**
	 * 批量减少玩家金币
	 */
	@RequestMapping(value = "/members_withdraw")
	public CommonVO members_withdraw(@RequestBody String[] memberIds, int amount, String textSummary) {
		CommonVO vo = new CommonVO();
		for (String memberId : memberIds) {
			try {
				AccountingRecord rcd = memberGoldCmdService.withdraw(memberId, amount, textSummary,
						System.currentTimeMillis());
				MemberGoldRecordDbo dbo = memberGoldQueryService.withdraw(memberId, rcd);
				memberXiuxianchangGoldMsgService.withdraw(dbo);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
			}
		}
		return vo;
	}

	/**
	 * 批量赠送玩家金币
	 */
	@RequestMapping(value = "/members_givegold")
	public CommonVO members_giveGold(@RequestBody String[] memberIds, int amount, String textSummary) {
		CommonVO vo = new CommonVO();
		for (String memberId : memberIds) {
			try {
				AccountingRecord rcd = memberGoldCmdService.giveGoldToMember(memberId, amount, textSummary,
						System.currentTimeMillis());
				MemberGoldRecordDbo dbo = memberGoldQueryService.withdraw(memberId, rcd);
				memberXiuxianchangGoldMsgService.withdraw(dbo);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
			}
		}
		return vo;
	}
}
