package com.anbang.qipai.xiuxianchang.msg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import com.anbang.qipai.xiuxianchang.msg.channel.source.RuianGameRoomSource;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;

@EnableBinding(RuianGameRoomSource.class)
public class RuianGameRoomMsgService {

	@Autowired
	private RuianGameRoomSource ruianGameRoomSource;

	public void removeGameRoom(List<String> gameIds) {
		CommonMO mo = new CommonMO();
		mo.setMsg("gameIds");
		mo.setData(gameIds);
		ruianGameRoomSource.ruianGameRoom().send(MessageBuilder.withPayload(mo).build());
	}

	public void createGameRoom(String httpUrl, String wsUrl, String gameId, String game) {
		CommonMO mo = new CommonMO();
		mo.setMsg("create gameroom");
		Map data = new HashMap<>();
		mo.setData(data);
		data.put("httpUrl", httpUrl);
		data.put("wsUrl", wsUrl);
		data.put("gameId", gameId);
		data.put("game", game);
		ruianGameRoomSource.ruianGameRoom().send(MessageBuilder.withPayload(mo).build());
	}
}
