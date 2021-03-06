package com.anbang.qipai.xiuxianchang.cqrs.c.service.impl;

import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomManager;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.GameRoomCmdService;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;

@Component
public class GameRoomCmdServiceImpl extends CmdServiceBase implements GameRoomCmdService {

	@Override
	public String createGame(String gameId, String memberId, Game game, Integer renshu, Long createTime)
			throws Exception {
		GameRoomManager gameRoomManager = singletonEntityRepository.getEntity(GameRoomManager.class);
		return gameRoomManager.createGame(gameId, memberId, game, renshu, createTime);
	}

	@Override
	public String joinGame(String gameId, String memberId) throws Exception {
		GameRoomManager gameRoomManager = singletonEntityRepository.getEntity(GameRoomManager.class);
		return gameRoomManager.joinGame(gameId, memberId);
	}

	@Override
	public String leaveGameRoom(String gameId, String memberId) throws Exception {
		GameRoomManager gameRoomManager = singletonEntityRepository.getEntity(GameRoomManager.class);
		return gameRoomManager.leaveGameRoom(gameId, memberId);
	}

	@Override
	public String removeGameRoom(String gameId) throws Exception {
		GameRoomManager gameRoomManager = singletonEntityRepository.getEntity(GameRoomManager.class);
		return gameRoomManager.removeGameRoom(gameId);
	}
}
