package com.anbang.qipai.xiuxianchang.msg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.xiuxianchang.msg.channel.source.DianpaoGameRoomSource;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;

@EnableBinding(DianpaoGameRoomSource.class)
public class DianpaoGameRoomMsgService {
	@Autowired
	private DianpaoGameRoomSource dianpaoGameRoomSource;

	public void removeGameRoom(List<String> gameIds) {
		CommonMO mo = new CommonMO();
		mo.setMsg("gameIds");
		mo.setData(gameIds);
		dianpaoGameRoomSource.dianpaoGameRoom().send(MessageBuilder.withPayload(mo).build());
	}
}
