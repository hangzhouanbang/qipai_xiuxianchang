package com.anbang.qipai.xiuxianchang.msg.receiver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.GameRoomCmdService;
import com.anbang.qipai.xiuxianchang.msg.channel.sink.WenzhouMajiangGameSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.service.GameService;
import com.google.gson.Gson;

@EnableBinding(WenzhouMajiangGameSink.class)
public class WenzhouMajiangGameMsgReceiver {

	@Autowired
	private GameService gameService;

	@Autowired
	private GameRoomCmdService gameRoomCmdService;

	private Gson gson = new Gson();

	@StreamListener(WenzhouMajiangGameSink.WENZHOUMAJIANGGAME)
	public void receive(CommonMO mo) {
		String msg = mo.getMsg();
		if ("playerQuit".equals(msg)) {// 有人退出游戏
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			try {
				gameRoomCmdService.leaveGameRoom(gameId, playerId);
				gameService.leaveGameRoom(Game.wenzhouMajiang, gameId, playerId);
			} catch (GameRoomNotFoundException e) {
				e.printStackTrace();
			}
		}
		if ("ju finished".equals(msg)) {// 一局游戏结束
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			gameService.updateGameRoomFinishedByGame(Game.wenzhouMajiang, gameId, true);
			gameService.finishMemberGameRoom(Game.wenzhouMajiang, gameId);
		}
	}
}
