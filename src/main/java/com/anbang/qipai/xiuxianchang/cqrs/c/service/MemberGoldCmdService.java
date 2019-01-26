package com.anbang.qipai.xiuxianchang.cqrs.c.service;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.CreateGoldAccountResult;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.MemberHasGoldAccountAlreadyException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.dml.accounting.AccountingRecord;
import com.dml.accounting.InsufficientBalanceException;

public interface MemberGoldCmdService {

	CreateGoldAccountResult createGoldAccountForMember(String memberId, Integer giveGoldAmount, String textSummary,
			Long currentTime) throws MemberHasGoldAccountAlreadyException, MemberNotFoundException;

	AccountingRecord giveGoldToMember(String memberId, Integer amount, String textSummary, Long currentTime)
			throws MemberNotFoundException;

	AccountingRecord withdraw(String memberId, Integer amount, String textSummary, Long currentTime)
			throws InsufficientBalanceException, MemberNotFoundException;

}
