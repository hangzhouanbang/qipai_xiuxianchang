package com.anbang.qipai.xiuxianchang.cqrs.q.dbo;

public class MemberGoldAccountDbo {

	private String id;

	private String memberId;

	private int balance;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

}
