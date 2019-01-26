package com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold;

import com.dml.accounting.AccountOwner;

/**
 * 金币账户持有者
 * 
 * @author lsc
 *
 */
public class MemberGoldAccountOwner implements AccountOwner {
	private String memberId;// 玩家

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

}
