package com.anbang.qipai.xiuxianchang.web.vo;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;

public class MemberPlayingRoomVO {
	private String gameId;
	private String memberId;
	private Game game;
	private int playersCount;// 当前人数
	private long createTime;// 创建时间

	public MemberPlayingRoomVO(MemberGameRoom memberGameRoom) {
		this.gameId = memberGameRoom.getGameRoom().getServerGame().getGameId();
		this.memberId = memberGameRoom.getMemberId();
		this.game = memberGameRoom.getGameRoom().getGame();
		this.playersCount = memberGameRoom.getGameRoom().getPlayersCount();
		this.createTime = memberGameRoom.getGameRoom().getCreateTime();
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
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

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

}
