package com.anbang.qipai.xiuxianchang.cqrs.c.service.impl;

import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.CreateGoldAccountResult;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.MemberGoldAccountManager;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.MemberHasGoldAccountAlreadyException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.dml.accounting.AccountingRecord;
import com.dml.accounting.InsufficientBalanceException;
import com.dml.accounting.TextAccountingSummary;

@Component
public class MemberGoldCmdServiceImpl extends CmdServiceBase implements MemberGoldCmdService {

	@Override
	public AccountingRecord giveGoldToMember(String memberId, Integer amount, String textSummary, Long currentTime)
			throws MemberNotFoundException {
		MemberGoldAccountManager memberGoldAccountManager = singletonEntityRepository
				.getEntity(MemberGoldAccountManager.class);
		return memberGoldAccountManager.giveGoldToMember(memberId, amount, new TextAccountingSummary(textSummary),
				currentTime);
	}

	@Override
	public AccountingRecord withdraw(String memberId, Integer amount, String textSummary, Long currentTime)
			throws InsufficientBalanceException, MemberNotFoundException {
		MemberGoldAccountManager memberGoldAccountManager = singletonEntityRepository
				.getEntity(MemberGoldAccountManager.class);
		AccountingRecord rcd = memberGoldAccountManager.withdraw(memberId, amount,
				new TextAccountingSummary(textSummary), currentTime);
		return rcd;
	}

	@Override
	public CreateGoldAccountResult createGoldAccountForMember(String memberId, Integer giveGoldAmount,
			String textSummary, Long currentTime) throws MemberHasGoldAccountAlreadyException, MemberNotFoundException {
		MemberGoldAccountManager memberGoldAccountManager = singletonEntityRepository
				.getEntity(MemberGoldAccountManager.class);
		String accountId = memberGoldAccountManager.createGoldAccountForNewMember(memberId);
		AccountingRecord accountingRecord = memberGoldAccountManager.giveGoldToMember(memberId, giveGoldAmount,
				new TextAccountingSummary(textSummary), currentTime);
		CreateGoldAccountResult result = new CreateGoldAccountResult(accountId, memberId, accountingRecord);
		return result;
	}

}
