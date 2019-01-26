package com.anbang.qipai.xiuxianchang.msg.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.xiuxianchang.msg.channel.source.WenzhouGameRoomSource;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;

@EnableBinding(WenzhouGameRoomSource.class)
public class WenzhouGameRoomMsgService {
	@Autowired
	private WenzhouGameRoomSource wenzhouGameRoomSource;

	public void removeGameRoom(List<String> gameIds) {
		CommonMO mo = new CommonMO();
		mo.setMsg("gameIds");
		mo.setData(gameIds);
		wenzhouGameRoomSource.wenzhouGameRoom().send(MessageBuilder.withPayload(mo).build());
	}
}
