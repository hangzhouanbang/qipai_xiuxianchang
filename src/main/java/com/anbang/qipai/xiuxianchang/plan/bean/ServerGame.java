package com.anbang.qipai.xiuxianchang.plan.bean;

/**
 * 服务器游戏相关信息
 * 
 * @author Neo
 *
 */
public class ServerGame {

	private GameServer server;
	private String gameId;// 游戏服务器那边的一个gameid

	public GameServer getServer() {
		return server;
	}

	public void setServer(GameServer server) {
		this.server = server;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

}
