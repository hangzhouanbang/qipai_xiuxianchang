package com.anbang.qipai.xiuxianchang.msg.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.CreateGoldAccountResult;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberDbo;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberAuthQueryService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.channel.sink.MembersSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.google.gson.Gson;

@EnableBinding(MembersSink.class)
public class MemberMsgReceiver {

	@Autowired
	private MemberAuthQueryService memberAuthQueryService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	private Gson gson = new Gson();

	@StreamListener(MembersSink.MEMBERS)
	public void recordMembers(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		MemberDbo member = gson.fromJson(json, MemberDbo.class);
		if ("newMember".equals(msg)) {
			memberAuthQueryService.insertMember(member);
			// 创建账户
			try {
				CreateGoldAccountResult result = memberGoldCmdService.createGoldAccountForMember(member.getId(), 20000,
						"new member", System.currentTimeMillis());
				memberGoldQueryService.createMemberGoldAccount(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("update member info".equals(msg)) {
			memberAuthQueryService.updateMemberBaseInfo(member);
		}
	}
}
