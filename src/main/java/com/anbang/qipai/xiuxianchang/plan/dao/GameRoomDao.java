package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;

public interface GameRoomDao {

	void save(GameRoom gameRoom);

	void updateGameRoomFinished(List<String> ids, boolean finished);

	void updateGameRoomFinished(String id, boolean finished);

	void updateGameRoomPlayerCountAndFull(String id, int playerCount, boolean full);

	GameRoom findNotFullGameRoom(Game game);

	List<GameRoom> findExpireGameRoom(long deadlineTime, boolean finished);
}
