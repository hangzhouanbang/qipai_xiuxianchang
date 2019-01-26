package com.anbang.qipai.xiuxianchang.cqrs.c.domain.game;

import java.util.HashMap;
import java.util.Map;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;

/**
 * 游戏房间管理
 * 
 * @author lsc
 *
 */
public class GameRoomManager {

	private Map<String, WaitingGameRoom> gameIdGameMap = new HashMap<>();

	/**
	 * 创建房间
	 */
	public String createGame(String gameId, String memberId, Game game, int renshu, long createTime)
			throws GameRoomHasExistAlreadyException {
		if (gameIdGameMap.containsKey(gameId)) {
			throw new GameRoomHasExistAlreadyException();
		}
		WaitingGameRoom room = new WaitingGameRoom();
		room.createGameRoom(gameId, memberId, game, renshu, createTime);
		gameIdGameMap.put(gameId, room);
		return gameId;
	}

	/**
	 * 加入房间
	 */
	public String joinGame(String gameId, String memberId)
			throws GameRoomNotFoundException, MemberHasJoinGameRoomException {
		WaitingGameRoom room = gameIdGameMap.get(gameId);
		if (room == null) {
			throw new GameRoomNotFoundException();
		}
		room.joinGameRoom(memberId);
		if (room.isFull()) {// 人满
			gameIdGameMap.remove(gameId);
		}
		return memberId;
	}

	/**
	 * 离开房间
	 */
	public String leaveGameRoom(String gameId, String memberId) throws GameRoomNotFoundException {
		WaitingGameRoom room = gameIdGameMap.get(gameId);
		if (room == null) {
			throw new GameRoomNotFoundException();
		}
		room.leaveGameRoom(memberId);
		return memberId;
	}

	public String removeGameRoom(String gameId) {
		gameIdGameMap.remove(gameId);
		return gameId;
	}

	public Map<String, WaitingGameRoom> getGameIdGameMap() {
		return gameIdGameMap;
	}

	public void setGameIdGameMap(Map<String, WaitingGameRoom> gameIdGameMap) {
		this.gameIdGameMap = gameIdGameMap;
	}

}
