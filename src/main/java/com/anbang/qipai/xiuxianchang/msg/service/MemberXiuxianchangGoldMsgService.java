package com.anbang.qipai.xiuxianchang.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldRecordDbo;
import com.anbang.qipai.xiuxianchang.msg.channel.source.MemberXiuxianchangGoldsSource;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;

@EnableBinding(MemberXiuxianchangGoldsSource.class)
public class MemberXiuxianchangGoldMsgService {

	@Autowired
	private MemberXiuxianchangGoldsSource memberXiuxianchangGoldsSource;

	public void withdraw(MemberGoldRecordDbo dbo) {
		CommonMO mo = new CommonMO();
		mo.setMsg("accounting");
		mo.setData(dbo);
		memberXiuxianchangGoldsSource.memberXiuxianchangGolds().send(MessageBuilder.withPayload(mo).build());
	}
}
