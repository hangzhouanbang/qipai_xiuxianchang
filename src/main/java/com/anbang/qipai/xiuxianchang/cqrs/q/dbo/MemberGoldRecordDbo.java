package com.anbang.qipai.xiuxianchang.cqrs.q.dbo;

import com.dml.accounting.AccountingSummary;

public class MemberGoldRecordDbo {

	private String id;

	private String accountId;

	private String memberId;

	private long accountingNo;

	private int accountingAmount;

	private int balanceAfter;

	private AccountingSummary summary;

	private long accountingTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public long getAccountingNo() {
		return accountingNo;
	}

	public void setAccountingNo(long accountingNo) {
		this.accountingNo = accountingNo;
	}

	public int getAccountingAmount() {
		return accountingAmount;
	}

	public void setAccountingAmount(int accountingAmount) {
		this.accountingAmount = accountingAmount;
	}

	public int getBalanceAfter() {
		return balanceAfter;
	}

	public void setBalanceAfter(int balanceAfter) {
		this.balanceAfter = balanceAfter;
	}

	public AccountingSummary getSummary() {
		return summary;
	}

	public void setSummary(AccountingSummary summary) {
		this.summary = summary;
	}

	public long getAccountingTime() {
		return accountingTime;
	}

	public void setAccountingTime(long accountingTime) {
		this.accountingTime = accountingTime;
	}

}
