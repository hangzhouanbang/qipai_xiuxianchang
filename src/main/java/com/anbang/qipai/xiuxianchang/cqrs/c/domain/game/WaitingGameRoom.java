package com.anbang.qipai.xiuxianchang.cqrs.c.domain.game;

import java.util.ArrayList;
import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;

/**
 * 等待开始的游戏房间
 * 
 * @author lsc
 *
 */
public class WaitingGameRoom {
	private String id;// 就是gameId
	private Game game;
	private List<String> playerIds = new ArrayList<>();// 当前玩家
	private int renshu;// 游戏人数
	private long createTime;// 创建时间

	public void createGameRoom(String gameId, String memebrId, Game game, int renshu, long createTime) {
		this.id = gameId;
		this.game = game;
		this.playerIds.add(memebrId);
		this.renshu = renshu;
		this.createTime = createTime;
	}

	public void joinGameRoom(String memebrId) throws MemberHasJoinGameRoomException {
		if (playerIds.contains(memebrId)) {
			throw new MemberHasJoinGameRoomException();
		}
		playerIds.add(memebrId);
	}

	public void leaveGameRoom(String memebrId) {
		playerIds.remove(memebrId);
	}

	public boolean isFull() {
		return playerIds.size() == renshu;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public List<String> getPlayerIds() {
		return playerIds;
	}

	public void setPlayerIds(List<String> playerIds) {
		this.playerIds = playerIds;
	}

	public int getRenshu() {
		return renshu;
	}

	public void setRenshu(int renshu) {
		this.renshu = renshu;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
