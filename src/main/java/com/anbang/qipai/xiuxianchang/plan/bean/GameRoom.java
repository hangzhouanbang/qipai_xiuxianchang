package com.anbang.qipai.xiuxianchang.plan.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 游戏房间
 * 
 * @author Neo
 *
 */
public class GameRoom {
	private String id;
	private Game game;// 游戏
	private int playersCount;// 当前人数
	private int panCountPerJu;// 开局人数
	private boolean full;// 人满
	private List<GameLaw> laws;// 玩法
	private ServerGame serverGame;
	private long createTime;// 创建时间
	private long deadlineTime;// 有效时间
	private boolean finished;// 是否完成

	public boolean validateLaws() {
		if (laws != null) {
			Set<String> groupIdSet = new HashSet<>();
			for (GameLaw law : laws) {
				String groupId = law.getMutexGroupId();
				if (groupId != null) {
					// contain this element,return false
					if (!groupIdSet.add(groupId)) {
						return false;
					}
				}
			}
		}
		return true;
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

	public int getPlayersCount() {
		return playersCount;
	}

	public void setPlayersCount(int playersCount) {
		this.playersCount = playersCount;
	}

	public int getPanCountPerJu() {
		return panCountPerJu;
	}

	public void setPanCountPerJu(int panCountPerJu) {
		this.panCountPerJu = panCountPerJu;
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}

	public List<GameLaw> getLaws() {
		return laws;
	}

	public void setLaws(List<GameLaw> laws) {
		this.laws = laws;
	}

	public ServerGame getServerGame() {
		return serverGame;
	}

	public void setServerGame(ServerGame serverGame) {
		this.serverGame = serverGame;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(long deadlineTime) {
		this.deadlineTime = deadlineTime;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

}
