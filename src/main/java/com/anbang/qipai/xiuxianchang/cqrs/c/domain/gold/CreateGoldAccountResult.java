package com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold;

import com.dml.accounting.AccountingRecord;

/**
 * 创建金币账户结果
 * 
 * @author lsc
 *
 */
public class CreateGoldAccountResult {
	private String accountId;
	private String memberId;
	private AccountingRecord accountingRecordForGiveGold;

	public CreateGoldAccountResult(String accountId, String memberId, AccountingRecord accountingRecordForGiveGold) {
		this.accountId = accountId;
		this.memberId = memberId;
		this.accountingRecordForGiveGold = accountingRecordForGiveGold;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public AccountingRecord getAccountingRecordForGiveGold() {
		return accountingRecordForGiveGold;
	}

	public void setAccountingRecordForGiveGold(AccountingRecord accountingRecordForGiveGold) {
		this.accountingRecordForGiveGold = accountingRecordForGiveGold;
	}

}
