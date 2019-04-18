package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;

public interface GameRoomDao {

	void save(GameRoom gameRoom);

	void updateGameRoomFinished(List<String> ids, boolean finished);

	void updateGameRoomFinishedByGame(Game game, String serverGameId, boolean finished);

	void updateGameRoomServerGameId(String id, String serverGameId);

	void updateGameRoomPlayersCountAndFull(String id, int playersCount, boolean full);

	void updateGameRoomDeadlineTime(Game game, String serverGameId, long deadlineTime);

	GameRoom findGameRoomByGame(Game game, String gameId);

	List<GameRoom> findNotFullGameRoom(Game game);

	List<GameRoom> findExpireGameRoom(long deadlineTime, boolean finished);
}
