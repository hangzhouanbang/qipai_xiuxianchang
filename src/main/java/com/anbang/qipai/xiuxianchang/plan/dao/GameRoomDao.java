package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;

public interface GameRoomDao {

	void save(GameRoom gameRoom);

	void updateGameRoomFinished(List<String> ids, boolean finished);

	void updateGameRoomFinishedByGame(Game game, String serverGameId, boolean finished);

	void updateGameRoomPlayersCountAndFull(String id, int playersCount, boolean full);

	GameRoom findGameRoomByGame(Game game, String gameId);

	GameRoom findNotFullGameRoom(Game game);

	List<GameRoom> findExpireGameRoom(long deadlineTime, boolean finished);
}
