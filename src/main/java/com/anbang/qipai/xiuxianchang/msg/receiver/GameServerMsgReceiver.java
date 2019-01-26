package com.anbang.qipai.xiuxianchang.msg.receiver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.msg.channel.sink.GameServerSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.GameLaw;
import com.anbang.qipai.xiuxianchang.plan.bean.GameServer;
import com.anbang.qipai.xiuxianchang.plan.bean.LawsMutexGroup;
import com.anbang.qipai.xiuxianchang.plan.service.GameService;
import com.google.gson.Gson;

@EnableBinding(GameServerSink.class)
public class GameServerMsgReceiver {

	@Autowired
	private GameService gameService;

	private Gson gson = new Gson();

	@StreamListener(GameServerSink.GAMESERVER)
	public void gameServer(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		if ("online".equals(msg)) {
			GameServer gameServer = gson.fromJson(json, GameServer.class);
			gameService.onlineServer(gameServer);
		}
		if ("offline".equals(msg)) {
			String[] gameServerIds = gson.fromJson(json, String[].class);
			gameService.offlineServer(gameServerIds);
		}
		if ("create gamelaw".equals(msg)) {
			GameLaw law = gson.fromJson(json, GameLaw.class);
			gameService.createGameLaw(law);
		}
		if ("update gamelaw".equals(msg)) {
			GameLaw law = gson.fromJson(json, GameLaw.class);
			gameService.updateGameLaw(law);
		}
		if ("remove gamelaw".equals(msg)) {
			String lawId = gson.fromJson(json, String.class);
			gameService.removeGameLaw(lawId);
		}
		if ("create lawsmutexgroup".equals(msg)) {
			LawsMutexGroup lawsMutexGroup = gson.fromJson(json, LawsMutexGroup.class);
			gameService.addLawsMutexGroup(lawsMutexGroup);
		}
		if ("remove lawsmutexgroup".equals(msg)) {
			String groupId = gson.fromJson(json, String.class);
			gameService.removeLawsMutexGroup(groupId);
		}
	}

}
