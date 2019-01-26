package com.anbang.qipai.xiuxianchang.msg.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.msg.channel.sink.MemberLoginLimitRecordSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberLoginLimitRecord;
import com.anbang.qipai.xiuxianchang.plan.service.MemberAuthService;
import com.anbang.qipai.xiuxianchang.plan.service.MemberLoginLimitRecordService;
import com.google.gson.Gson;

@EnableBinding(MemberLoginLimitRecordSink.class)
public class MemberLoginLimitRecordMsgReceiver {
	@Autowired
	private MemberLoginLimitRecordService memberLoginLimitRecordService;

	@Autowired
	private MemberAuthService memberAuthService;

	private Gson gson = new Gson();

	@StreamListener(MemberLoginLimitRecordSink.MEMBERLOGINLIMITRECORD)
	public void memberClubCard(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		if ("add record".equals(msg)) {
			MemberLoginLimitRecord record = gson.fromJson(json, MemberLoginLimitRecord.class);
			memberLoginLimitRecordService.save(record);
			memberAuthService.removeSessionByMemberId(record.getMemberId());
		}
		if ("delete records".equals(msg)) {
			String[] recordIds = gson.fromJson(json, String[].class);
			memberLoginLimitRecordService.updateMemberLoginLimitRecordEfficientById(recordIds, false);
		}
	}
}
